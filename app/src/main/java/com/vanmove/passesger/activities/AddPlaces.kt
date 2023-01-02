package com.vanmove.passesger.activities
import android.Manifest
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.*
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.BookedMovesAdapter
import com.vanmove.passesger.adapters.LastLocBookedMovesAdapter
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.Booking
import com.vanmove.passesger.services.AppFetchAddressIntentService
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.Utils.GetAddressFromLocation
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.activity_choose_pick_up.*
import kotlinx.android.synthetic.main.activity_choose_pick_up.lv_previous_booked
import kotlinx.android.synthetic.main.fragment_previous_booked.*
import kotlinx.android.synthetic.main.fragment_previous_booked.view.*
import org.json.JSONException
import java.util.*
import android.view.ViewGroup
import com.vanmove.passesger.utils.*
import com.vanmove.passesger.utils.CONSTANTS.callfrom
import com.vanmove.passesger.utils.CONSTANTS.pickup_from_loc
import com.vanmove.passesger.utils.CONSTANTS.where_to_loc
import com.vanmove.passesger.utils.ShowProgressDialog.closeDialog
import com.vanmove.passesger.utils.ShowProgressDialog.showDialog2
import org.json.JSONObject


class AddPlaces : AppCompatActivity(R.layout.activity_add_places), View.OnClickListener,
    OnMapReadyCallback, OnCameraIdleListener, OnClickTwoButtonsAlertDialog, OnItemClickRecycler {
    private var booking_previous_list: ArrayList<Booking>? = null
    private var RegistrationID: String? = null
    private var passenger_id: String? = null


    var pickup_location: String? = null
    var pickup_lat: String? = null
    var pickup_lng: String? = null
    var home_name: String? = ""
    var home_lat: String? = ""
    var home_lng: String? = ""
    private var myMap: GoogleMap? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var gps: GPSTracker? = null
    private var marker: Marker? = null
    private var resultReceiver: AppAddressResultReceiver? =
        null
    private var myCircle: Circle? = null
    private var vAnimator: ValueAnimator? = null
    var context: Context? = null
    var first_time = true
    val search_msg =
        "You can  &quot;Edit&quot; the above address excluding the postcode.If you wish to change the postcode, please click the &quot;New Address&quot; button."
    var ispickup: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@AddPlaces
        linkViews()

        gps = GPSTracker(this@AddPlaces, context)
        if (gps!!.canGetLocation()) {
            latitude = gps!!.latitude
            longitude = gps!!.longitude
        } else {
            gps!!.showSettingsAlert()
        }
        resultReceiver =
            AppAddressResultReceiver(Handler())

        showMap(savedInstanceState)

        getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            context!!
        ).let {
            pick_up_tv.text = it
        }

        pick_up_tv.setOnClickListener {

            ispickup = true
            showAlertMessageWithTwoButtons(
                context,
                this@AddPlaces,
                "edit_address", "Edit Address",
                getString(R.string.search_msg),
                "Edit", "New Address"
            )

        }
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        booking_previous_list = ArrayList()
        getDriverPreviousBookings(passenger_id, RegistrationID)

    }


    private fun linkViews() {

        vAnimator = ValueAnimator()
        tv_add_home.setOnClickListener(this)
        tv_add_work.setOnClickListener(this)
        // recent_ride_session.setOnClickListener(this)
        ll_save_place.setOnClickListener(this)
        edit_home.setOnClickListener(this)
        edit_work.setOnClickListener(this)
        btn_continue.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        where_to_tv.setOnClickListener(this)
        pickup_location = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            context!!
        )
        pickup_lat = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            context!!
        )
        pickup_lng = getPreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            context!!
        )
        val callFrom=intent.getStringExtra(callfrom).toString()
        if (callFrom.equals(where_to_loc)){
            recent_session.visibility=View.VISIBLE

        }
        else if (callFrom.equals(pickup_from_loc)){
            recent_session.visibility=View.VISIBLE

        }
        else
        {
            recent_session.visibility=View.GONE
        }

    }

    public override fun onResume() {
        super.onResume()
        try {
            Utils.closeKeyboard(this)
            showAddress()
            mapView!!.onResume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    private fun showMap(savedInstanceState: Bundle?) {
        try {
            mapView!!.onCreate(savedInstanceState)
            MapsInitializer.initialize(this@AddPlaces)
            mapView!!.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_add_work -> if (!work_address!!.text.toString().isEmpty()) {
                savePreferences(

                    CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                    work_address!!.text.toString(),
                    context!!
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
                    getPreferences(
                        CONSTANTS.PREFERENCE_ADD_WORK_LATITUDE_EXTRA,
                        context!!
                    ),
                    context!!
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
                    getPreferences(
                        CONSTANTS.PREFERENCE_ADD_WORK_LONGITUDE_EXTRA,
                        context!!
                    ),
                    context!!
                )
                val intent = Intent()
                intent.putExtra("pickup", work_address!!.text.toString())
                intent.putExtra(
                    "latitude",
                    getPreferences(
                        CONSTANTS.PREFERENCE_ADD_WORK_LATITUDE_EXTRA,
                        context!!
                    )
                )
                intent.putExtra(
                    "longitude",
                    getPreferences(
                        CONSTANTS.PREFERENCE_ADD_WORK_LONGITUDE_EXTRA,
                        context!!
                    )
                )
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            /*   R.id.recent_ride_session -> {
                   if (Recent_Ride.text.toString().isEmpty()) {
                       AlertDialogManager.showAlertMessage(
                           context,
                           "No previous move address"
                       )

                   } else {
                       val intent = Intent()
                       intent.putExtra("pickup", Recent_Ride!!.text.toString())
                       intent.putExtra(
                           "latitude",
                           getPreferences(
                               CONSTANTS.PREFERENCE_LAST_RIDE_LATITUDE_EXTRA,
                               context!!
                           )
                       )
                       intent.putExtra(
                           "longitude",
                           getPreferences(
                               CONSTANTS.PREFERENCE_LAST_RIDE_LONGITUDE_EXTRA,
                               context!!
                           )
                       )
                       setResult(Activity.RESULT_OK, intent)
                       finish()
                   }

               }*/
            R.id.btn_back -> finish()
            R.id.tv_add_home -> if (!home_address!!.text.toString().isEmpty()) {
                home_name = getPreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LOCATION_NAME,
                    context!!
                )
                home_lat = getPreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LATITUDE_EXTRA,
                    context!!
                )
                home_lng = getPreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LONGITUDE_EXTRA,
                    context!!
                )
                val intent1 = Intent()
                intent1.putExtra("pickup", home_address!!.text.toString())
                intent1.putExtra(
                    "latitude",
                    getPreferences(
                        CONSTANTS.PREFERENCE_ADD_HOME_LATITUDE_EXTRA,
                        context!!
                    )
                )
                intent1.putExtra(
                    "longitude",
                    getPreferences(
                        CONSTANTS.PREFERENCE_ADD_HOME_LONGITUDE_EXTRA,
                        context!!
                    )
                )
                setResult(Activity.RESULT_OK, intent1)
                finish()
            }
            R.id.edit_home -> {
                savePreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LOCATION_NAME,
                    where_to_tv!!.text.toString(),
                    context!!
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LATITUDE_EXTRA,
                    latitude.toString() + "",
                    context!!
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LONGITUDE_EXTRA,
                    longitude.toString() + "",
                    context!!
                )
                home_address!!.text = where_to_tv!!.text
            }
            R.id.edit_work -> {
                savePreferences(
                    CONSTANTS.PREFERENCE_ADD_WORK_LOCATION_NAME,
                    where_to_tv!!.text.toString(),
                    context!!
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_ADD_WORK_LATITUDE_EXTRA,
                    latitude.toString() + "",
                    context!!
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_ADD_WORK_LONGITUDE_EXTRA,
                    longitude.toString() + "",
                    context!!
                )
                work_address!!.text = where_to_tv!!.text
            }
            R.id.where_to_tv -> if (where_to_tv!!.text.toString().isEmpty()) {
                PlacePicker(200)

            } else {
                PlacePicker(200)
            }
            R.id.btn_continue -> {
                where_to_tv!!.text.toString().let {
                    if (it.isEmpty()) {
                        showToast(
                            "Select Where to location"
                        )
                    } else if (it.contains("Loading...")) {
                        showToast(
                            "Press again continue button"
                        )
                    } else {
                        if(intent.hasExtra("add_place")){
                            savePlace(it, marker!!.position.longitude.toString(),marker!!.position.latitude.toString(),passenger_id)
                        }
                        else{
                            val intent1 = Intent()
                            intent1.putExtra("pickup", it)
                            intent1.putExtra("latitude", "" + marker!!.position.latitude)
                            intent1.putExtra("longitude", "" + marker!!.position.longitude)
                            setResult(Activity.RESULT_OK, intent1)
                            finish()
                        }
                    }
                }

            }
            R.id.ll_save_place -> {
                ispickup = false
                startActivityForResult(
                    Intent(this, SavedPlaces::class.java).putExtra(callfrom,intent.getStringExtra(callfrom).toString()), 3001
                )

            }
        }
    }
//cr02
    //sw11

    private fun PlacePicker(request_Code: Int) {
        if (!Places.isInitialized()) {
            Places.initialize(
                context!!, getString(R.string.google_map_api_key)
            )
        }
        val placeFields4 =
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        val intent_pick_up_tv = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields4)
            //.setCountry(CONSTANTS.current_country)
            .build(context!!)
        startActivityForResult(intent_pick_up_tv, request_Code)
    }

    private fun showAddress() {

        /*   getPreferences(
               CONSTANTS.PREFERENCE_LAST_RIDE_ADDRESS_EXTRA,
               context!!
           ).toString().let {
               if (!it.isEmpty()) {
                   Recent_Ride!!.text = it
               }
           }*/

        if (!TextUtils.isEmpty(
                getPreferences(
                    CONSTANTS.PREFERENCE_ADD_HOME_LOCATION_NAME,
                    context!!
                )
            )
        ) {
            home_address!!.text = getPreferences(
                CONSTANTS.PREFERENCE_ADD_HOME_LOCATION_NAME,
                context!!
            )
        }
        if (!TextUtils.isEmpty(
                getPreferences(
                    CONSTANTS.PREFERENCE_ADD_WORK_LOCATION_NAME,
                    context!!
                )
            )
        ) {
            work_address!!.text = getPreferences(
                CONSTANTS.PREFERENCE_ADD_WORK_LOCATION_NAME,
                context!!
            )
        }
    }

    public override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        // ShowProgressDialog.showDialog2(context)
        var placeName = ""
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 200) {
              //  edit_msg!!.visibility = View.VISIBLE
                Autocomplete.getPlaceFromIntent(data!!).let {
                    placeName = "" + it.name + " - " + it.address
                    latitude = it.latLng!!.latitude
                    longitude = it.latLng!!.longitude
                    where_to_tv!!.text = placeName
                    val new_lating = LatLng(latitude, longitude)
                    val cameraPosition = CameraPosition.Builder()
                        .target(new_lating)
                        .zoom(15f)
                        .build()
                    marker!!.position = LatLng(latitude, longitude)
                    first_time = true

//                    Handler().postDelayed(
//                        {
//                           ShowProgressDialog.closeDialog() // This method will be executed once the timer is over
//                        },
//                        3000 // value in milliseconds
//                    )



                    myMap!!.animateCamera(
                        CameraUpdateFactory
                            .newCameraPosition(cameraPosition)
                    )
                }

            } else if (requestCode == 100) {
                Autocomplete.getPlaceFromIntent(data!!).let {
                    val name = it.name + " - " + it.address
                    name.let {
                        pick_up_tv.text = it

                        savePreferences(
                            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                            it,
                            context!!
                        )
                    }
                    it.latLng!!.latitude.let {
                        savePreferences(
                            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
                            "" + it,
                            context!!
                        )

                    }
                    it.latLng!!.longitude.let {
                        savePreferences(
                            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
                            "" + it, context!!
                        )
                    }


                }

            }
            else if (requestCode == 3001) {
               val  placeName=data!!.getStringExtra("pickup").toString()
               val  latitude = data!!.getStringExtra("latitude")!!.toString()
               val  longitude = data!!.getStringExtra("longitude")!!.toString()


                val intent = Intent()
                intent.putExtra("pickup", placeName)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        setUserCurrentLocation()




        myMap!!.setOnCameraChangeListener { cameraPosition ->
            latitude = cameraPosition.target.latitude
            longitude = cameraPosition.target.longitude
            val latLng = LatLng(latitude, longitude)
            if (marker != null) {
                marker!!.remove()
                if (myCircle != null) {
                    myCircle!!.remove()
                }
            }
            val icon =
                BitmapDescriptorFactory.fromResource(R.drawable.black_marker)
            marker = myMap!!.addMarker(
                MarkerOptions()
                    .title("Distance ")
                    .position(latLng)
                    .icon(icon)

            )
            if (vAnimator!!.isStarted) {

                vAnimator!!.end()

            }
            myCircle = myMap!!.addCircle(
                CircleOptions().center(latLng)
                    .fillColor(Color.parseColor("#97C1D7"))
                    .strokeColor(Color.parseColor("#97C1D7")).radius(350.0)
            )
            vAnimator!!.repeatCount = ValueAnimator.INFINITE
            vAnimator!!.repeatMode = ValueAnimator.RESTART
            vAnimator!!.setIntValues(0, 100)
            vAnimator!!.duration = 2000
            vAnimator!!.setEvaluator(IntEvaluator())
            vAnimator!!.interpolator = AccelerateDecelerateInterpolator()
            vAnimator!!.addUpdateListener { valueAnimator ->
                val animatedFraction = valueAnimator.animatedFraction
                myCircle!!.setRadius(animatedFraction * 100.toDouble())
            }
            vAnimator!!.start()
            val location = Location("")
            location.latitude = latitude
            location.longitude = longitude
            if (first_time) {
                first_time = false
            } else {
                startAddressLocationGetService(location)

            }


        }
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this@AddPlaces,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // myMap!!.isMyLocationEnabled = true
    }

    private fun setUserCurrentLocation() {
        try {
            if (marker == null) {
                val icon_marker =
                    BitmapDescriptorFactory.fromResource(R.drawable.black_marker)
                val user_position = LatLng(latitude, longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(user_position)
                markerOptions.icon(icon_marker)
                marker = myMap!!.addMarker(markerOptions)
                myMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(user_position, 15f))
            }
            myMap!!.setOnCameraIdleListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startAddressLocationGetService(location: Location) {
        try {
            where_to_tv!!.text = "Loading..."
            val intent = Intent(this@AddPlaces, AppFetchAddressIntentService::class.java)
            intent.putExtra(CONSTANTS.RECEIVER, resultReceiver)
            intent.putExtra(CONSTANTS.LOCATION_DATA_EXTRA, location)
            startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCameraIdle() {
        latitude = myMap!!.cameraPosition.target.latitude
        longitude = myMap!!.cameraPosition.target.longitude
    }



    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name == "edit_address") {
            ShowEditDialog()
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {
        if (dialog_name == "edit_address") {


            if (ispickup!!) {
                PlacePicker(100)
            } else {
                PlacePicker(200)

            }
        }
    }

    private fun ShowEditDialog() {
        val alertDialog =
            AlertDialog.Builder(context!!)
                .setTitle("Edit Address")
        val input = EditText(context)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp
        alertDialog.setView(input)
        if (ispickup!!) {
            input.setText(pick_up_tv!!.text.toString())

        } else {
            input.setText(where_to_tv!!.text.toString())

        }
        alertDialog.setPositiveButton("Save") { dialogInterface, i ->
            val address = input.text.toString()
            if (address.isEmpty()) {
                Toast.makeText(context, "Address cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                dialogInterface.dismiss()
                // input.hideKeyboard()
                Utils.closeKeyboard(this)

                if (ispickup!!) {
                    pick_up_tv!!.text = address
                } else {
                    where_to_tv!!.text = address

                }
            }
        }
            .setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
                //  input.hideKeyboard()
                Utils.closeKeyboard(this)

            }
            .show()
    }

    internal inner class AppAddressResultReceiver(handler: Handler?) :
        ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val mAddressOutput = resultData.getString(CONSTANTS.RESULT_DATA_KEY)
            if (mAddressOutput == null) {
                GetAddressBackEndTask().execute()
            } else {
                try {
                    if (mAddressOutput != getString(R.string.service_not_available)) {
                        where_to_tv!!.text = mAddressOutput
                        where_to_tv!!.isSelected = true
                    } else {
                        GetAddressBackEndTask()
                            .execute()
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
            if (resultCode == CONSTANTS.SUCCESS_RESULT) {
                //HelperToastMessage.showSampleToast(getActivity(), getString(R.string.address_found));
            }
        }
    }

    private inner class GetAddressBackEndTask :
        AsyncTask<String?, Void?, String>() {
        var pick_up_address: String? = null
        protected override fun doInBackground(vararg params: String?): String? {
            pick_up_address = GetAddressFromLocation(
                context,
                latitude,
                longitude
            )
            if (TextUtils.isEmpty(pick_up_address)) {
                pick_up_address = "Unnamed Location"
            }
            return pick_up_address!!
        }

        override fun onPostExecute(result: String) {
            if (TextUtils.isEmpty(result)) {
                where_to_tv!!.text = "Unnamed Location"
            } else {
                where_to_tv!!.text = result
            }
        }

        override fun onPreExecute() {}
        protected override fun onProgressUpdate(vararg values: Void?) {}
    }

    private fun savePlace(
        destination: String?,
        longitude: String?,
        latitude: String?,
        userId: String?
    ) {

        showDialog2(context)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Utils.updateTLS(context)
        }
        val req_data: MutableMap<String?, String?> =
            HashMap()
        req_data["destination"] = destination!!
        req_data["latitude"] = latitude!!
        req_data["longitude"] = longitude!!
        req_data["userId"] = userId!!
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.saved_addresses,
            JSONObject(req_data as Map<*, *>),
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                Log.d("TAG", "response::"+jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getInt("code") == 1000) {
                        Toast.makeText(
                            this,
                            jsonStatus.getString("message"),Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            jsonStatus.getString("message"),Toast.LENGTH_LONG
                        ).show()
                    }
                   closeDialog()


                } catch (e: JSONException) {
                   closeDialog()
                }
            }
            , Response.ErrorListener {
               closeDialog()

            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers =
                  HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                return headers
            }
        }
        MyRequestQueue.getRequestInstance(this)!!.addRequest(jsonObjReq)
    }
    private fun getDriverPreviousBookings(
        passenger_id: String?,
        registration_id: String?
    ) {

        progress_bar_last_loc.visible()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Utils.updateTLS(context)
        }
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.get_passenger_previous_moves,
            null,
            Response.Listener { jsonObject ->
                Log.d("TAG", "response::"+jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code") == "1000") {
                        val jsonArray = jsonObject.getJSONArray("bookings")
                        if (jsonArray.length() > 0) {
                            //  no_recent_ride_ll!!.visibility = View.GONE
                            lv_previous_booked!!.visibility = View.VISIBLE
                        } else {
                            lv_previous_booked!!.visibility = View.GONE
                            //no_recent_ride_ll!!.visibility = View.VISIBLE
                        }
                        booking_previous_list!!.clear()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject_inner = jsonArray.getJSONObject(i)
                            val request_id =
                                jsonObject_inner.getString("request_id")
                            val pickup = jsonObject_inner.getString("pickup")
                            val destination =
                                jsonObject_inner.getString("destination")

                            val destination_latitude =
                                jsonObject_inner.getString("destination_latitude")

                            val destination_longitude =
                                jsonObject_inner.getString("destination_longitude")

                            val timestamp =
                                jsonObject_inner.getString("request_trip_start_time")
                            val rate_grand_total =
                                jsonObject_inner.getString("rate_grand_total")
                            val offered_advance =
                                jsonObject_inner.getDouble("offered_advance")
                            val is_future = jsonObject_inner.getInt("is_future")
                            val booking = Booking()
                            booking.request_id = request_id
                            booking.pick_up_name = pickup
                            booking.drop_off_name = destination
                            booking.trip_end_time = timestamp
                            booking.rate_grand_total = rate_grand_total
                            booking.is_future = is_future
                            booking.offered_advance = offered_advance
                            booking.drop_off_latitude=destination_latitude
                            booking.drop_off_longitude=destination_longitude
                            booking_previous_list!!.add(booking)
                        }

                        lv_previous_booked!!.adapter = LastLocBookedMovesAdapter(
                            this,
                            booking_previous_list!!, this@AddPlaces
                        )
                        val params: ViewGroup.LayoutParams = lv_previous_booked.getLayoutParams()
                        if (booking_previous_list!!.size>3){
                            params.height =700
                        }
                        else
                        {
                            params.height =   ViewGroup.LayoutParams.WRAP_CONTENT
                        }

                        lv_previous_booked.setLayoutParams(params)
                    } else {
                        Utils.showToastTest(
                            this,
                            jsonStatus.getString("message")
                        )
                    }
                    progress_bar_last_loc.gone()


                } catch (e: JSONException) {
                    progress_bar_last_loc.gone()
                }
            }
            , Response.ErrorListener {
                progress_bar_last_loc.gone()

            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["passenger_id"] = passenger_id!!
                headers["registration_id"] = registration_id!!
                return headers
            }
        }
        MyRequestQueue.getRequestInstance(this)!!.addRequest(jsonObjReq)
    }
    override fun onClickRecycler(view: View?, position: Int) {
        /* if (Recent_Ride.text.toString().isEmpty()) {
             AlertDialogManager.showAlertMessage(
                 context,
                 "No previous move address"
             )

         } else {
             val intent = Intent()
             intent.putExtra("pickup", Recent_Ride!!.text.toString())
             intent.putExtra(
                 "latitude",
                 getPreferences(
                     CONSTANTS.PREFERENCE_LAST_RIDE_LATITUDE_EXTRA,
                     context!!
                 )
             )
             intent.putExtra(
                 "longitude",
                 getPreferences(
                     CONSTANTS.PREFERENCE_LAST_RIDE_LONGITUDE_EXTRA,
                     context!!
                 )
             )
             setResult(Activity.RESULT_OK, intent)
             finish()
         }*/
        val intent = Intent()
        intent.putExtra("pickup", booking_previous_list!!.get(position).drop_off_name)
        intent.putExtra("latitude", booking_previous_list!!.get(position).drop_off_latitude)
        intent.putExtra("longitude", booking_previous_list!!.get(position).drop_off_longitude)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}