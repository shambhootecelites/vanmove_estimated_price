package com.vanmove.passesger.activities

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.maps.model.*
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.SavedLocBookedMovesAdapter
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.Booking
import com.vanmove.passesger.model.SavedPlacesData
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.CONSTANTS.callfrom
import com.vanmove.passesger.utils.ShowProgressDialog
import com.vanmove.passesger.utils.ShowProgressDialog.closeDialog
import com.vanmove.passesger.utils.ShowProgressDialog.showDialog2
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.activity_choose_pick_up.*
import kotlinx.android.synthetic.main.activity_choose_pick_up.lv_previous_booked
import kotlinx.android.synthetic.main.fragment_previous_booked.*
import kotlinx.android.synthetic.main.fragment_previous_booked.view.*
import org.json.JSONException
import java.util.*


class SavedPlaces : AppCompatActivity(R.layout.activity_save_places), View.OnClickListener, OnItemClickRecycler {
    private var booking_previous_list: ArrayList<SavedPlacesData>? = null
    private var RegistrationID: String? = null
    private var passenger_id: String? = null


    private var vAnimator: ValueAnimator? = null
    var context: Context? = null
    var first_time = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@SavedPlaces
        linkViews()

        RegistrationID = getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id = getPreferences(CONSTANTS.passenger_id, context!!)
        booking_previous_list = ArrayList()

        getSavedPlaces(passenger_id)
    }


    private fun linkViews() {

        vAnimator = ValueAnimator()

        btn_continue.setOnClickListener(this)
        btn_back.setOnClickListener(this)



    }

    public override fun onResume() {
        super.onResume()
        try {
            Utils.closeKeyboard(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onStop() {
        super.onStop()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_back -> finish()
            R.id.btn_continue -> {
                startActivityForResult(
                    Intent(this, AddPlaces::class.java)
                        .putExtra(callfrom,intent.getStringExtra(callfrom).toString())
                        .putExtra("add_place","add_place"), 200
                )

            }
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
                val  placeName=data!!.getStringExtra("pickup").toString()
                val  latitude = data!!.getStringExtra("latitude")!!.toDouble()
                val  longitude = data!!.getStringExtra("longitude")!!.toDouble()

                val intent = Intent()
                intent.putExtra("pickup", placeName)
                intent.putExtra("latitude", latitude)
                intent.putExtra("longitude", longitude)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else if (requestCode == 100) {

            }
        }
    }












    private fun getSavedPlaces(
        user_id: String?) {

        showDialog2(context)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Utils.updateTLS(context)
        }
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.get_saved_addresses,
            null,
            Response.Listener { jsonObject ->
                Log.d("TAG","Response::"+ jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                   /* {"status":{"code":"1000","message":"Success."},
                        "data":[
                        {"id":5,"user_id":0,"destination":"Green Hosue-2, NATIONAL AGRI FOOD BIOTECHNOLOGY INSTITUTE, Sector 81, Sahibzada Ajit Singh Nagar, Punjab 140306, India",
                        "latitude":"30.665593421602576","longitude":"76.71671122312546"},{"id":6,"user_id":0,
                            "destination":"MI Block, Sector 81, Sahibzada Ajit Singh Nagar,
                            Punjab 140308, India","latitude":"30.667709897608866","longitude":"76.72237604856491"},
                            {"id":7,"user_id":0,"destination":"MP7P+G5F, Industrial Area, Industrial Area Mohali Phase 9, Sahibzada Ajit Singh Nagar, Punjab 140308, India","latitude":"30.662566714036863","longitude":"76.7352794855833"},{"id":8,"user_id":0,"destination":"Unnamed Road, Sahibzada Ajit Singh Nagar, Punjab 140306, India","latitude":"30.661360618920227","longitude":"76.71854216605425"},{"id":9,"user_id":0,"destination":"Jwala Nagar, Saharanpur, Uttar Pradesh, India","latitude":"29.968003389929706","longitude":"77.55520664155483"},{"id":10,"user_id":0,"destination":"958, Phase 7, Sector 61, Sahibzada Ajit Singh Nagar, Chandigarh 140301, India","latitude":"30.705747512403263","longitude":"76.72872751951218"},{"id":11,"user_id":0,"destination":"2256, Sukhna Path, Sector 50, Chandigarh, 160050, India","latitude":"30.694382344651366","longitude":"76.74557916820049"},{"id":12,"user_id":0,"destination":"944, Sector 70, Sahibzada Ajit Singh Nagar, Punjab 160062, India","latitude":"30.69935172518624","longitude":"76.72011595219374"}]}
*/
                    if (jsonStatus.getString("code") == "1000") {
                        val jsonArray = jsonObject.getJSONArray("data")
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
                            val id =
                                jsonObject_inner.getString("id")
                            val user_id = jsonObject_inner.getString("user_id")
                            val destination =
                                jsonObject_inner.getString("destination")

                            val latitude =
                                jsonObject_inner.getString("latitude")

                            val longitude =
                                jsonObject_inner.getString("longitude")


                            val mSavedPlacesData = SavedPlacesData()
                            mSavedPlacesData.id = id
                            mSavedPlacesData.user_id = user_id
                            mSavedPlacesData.destination = destination
                            mSavedPlacesData.latitude = latitude
                            mSavedPlacesData.longitude = longitude

                            booking_previous_list!!.add(mSavedPlacesData)
                        }

                        lv_previous_booked!!.adapter = SavedLocBookedMovesAdapter(this, booking_previous_list!!, this@SavedPlaces)

                    } else {
                        Utils.showToastTest(
                            this,
                            jsonStatus.getString("message")
                        )
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
                headers["user_id"] = user_id!!
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
        intent.putExtra("pickup", booking_previous_list!!.get(position).destination)
        intent.putExtra("latitude", booking_previous_list!!.get(position).latitude)
        intent.putExtra("longitude", booking_previous_list!!.get(position).longitude)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}