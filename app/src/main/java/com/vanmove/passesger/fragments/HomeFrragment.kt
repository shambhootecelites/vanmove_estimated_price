package com.vanmove.passesger.fragments

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.AdvancedPayment.AdvancedPaymentActivity
import com.vanmove.passesger.activities.Ask
import com.vanmove.passesger.activities.ChooseWhereto
import com.vanmove.passesger.activities.SelectVechileActivity
import com.vanmove.passesger.enum.JobStatus
import com.vanmove.passesger.fragments.MoveNavigationScreen.ConnectedFragment
import com.vanmove.passesger.fragments.MoveNavigationScreen.FragmentMoveStarted
import com.vanmove.passesger.fragments.MoveNavigationScreen.FragmentVanOnItsWay
import com.vanmove.passesger.interfaces.DirectionFinderListener
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.model.APIModel.Request
import com.vanmove.passesger.services.AppFetchAddressIntentService
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.*
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessage
import com.vanmove.passesger.utils.AlertDialogManager.showAlertMessageWithTwoButtons
import com.vanmove.passesger.utils.DateTimePicker.ShowDatePicker
import com.vanmove.passesger.utils.ShowProgressDialog.closeDialog
import com.vanmove.passesger.utils.ShowProgressDialog.showDialog2
import com.vanmove.passesger.utils.Utils.KmToMile
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.hideKeyboard
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.showToast
import com.vanmove.passesger.utils.Utils.showToastLong
import com.vanmove.passesger.utils.Utils.showToastTest
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.date_picker_dialog.view.*
import kotlinx.android.synthetic.main.dropoff.view.*
import kotlinx.android.synthetic.main.duration_help_layout.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.Build
import android.widget.NumberPicker

import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.adapters.LastLocBookedMovesAdapter
import com.vanmove.passesger.adapters.TimeSlotsAdapter
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.*
import kotlinx.android.synthetic.main.activity_choose_pick_up.*
import kotlinx.android.synthetic.main.activity_choose_pick_up.lv_previous_booked
import kotlinx.android.synthetic.main.choose_time_slots_dialog.*
import kotlinx.android.synthetic.main.home_fragment.mapView
import kotlinx.android.synthetic.main.time_slots_row_items.view.*


class HomeFrragment : Fragment(R.layout.home_fragment),
    View.OnClickListener, LocationListener,
    OnMapReadyCallback, OnCameraIdleListener, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener, OnCameraChangeListener,
    OnClickTwoButtonsAlertDialog,OnItemClickRecycler{

    private var drivers_around_list: ArrayList<Drivers>? = ArrayList()


    private var driver_available_position = 0
    private var timer: Timer? = null
    private var minute: Int? = null
    private var myMap: GoogleMap? = null
    private var marker: Marker? = null
    var str_dest = ""
    var currentAddress = ""
    var distance_driver: String? = null
    private var driver_id: String? = null
    private var vehicle_class_id: String? = null
    private var vehicle_class_name: String? = null
    private var latitude_driver: String? = null
    private var longitude_driver: String? = null
    private var pickip_latitude = 0.0
    private var pickup_longitude = 0.0
    private var RegistrationID: String? = null
    private var passenger_id: String? = null
    var transaction: FragmentTransaction? = null
    private var doAsynchronousTaskGetDriver: TimerTask? = null

    private var mLocationRequest: LocationRequest? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var resultReceiver: AppAddressResultReceiver? = null
    private var distance_in_miles = "0.0"
    private var move_duration = ""
    private var polylinePaths: MutableList<Polyline>? =
        ArrayList()
    var source_marker: Marker? = null
    var destination_marker: Marker? = null
    var destination_lat = 0.0
    var destination_lon = 0.0
    var destintaion_: String? = null
    var location: Location? = null
    var address_manual = false

    var custome_marker: View? = null
    var tv_marker: TextView? = null
    var accept_move: Request? = null

    var active_booking: Request? = null
    val handler = Handler()
   /* */

    var selectedYYYY = 0
    var selectedMM = 0;
    var selectedDD = 0;

    var currentYear = ""
    var currentMonth = ""
    var currentDay =""
    var compare_format = SimpleDateFormat("yyyy-MM-dd")
    var date_future_booking_str = ""

    private val minHour = 8
    private val minMinute = 0

    private val maxHour = 8
    private val maxMinute = 0


  /*  fun showDatePickerDialog(context: Context) {

        var date: Calendar = Calendar.getInstance()
        var thisAYear = date.get(Calendar.YEAR).toInt()
        var thisAMonth = date.get(Calendar.MONTH).toInt()
        var thisADay = date.get(Calendar.DAY_OF_MONTH).toInt()

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view2, thisYear, thisMonth, thisDay ->
            // Display Selected date in textbox
            thisAMonth = thisMonth + 1
            thisADay = thisDay
            thisAYear = thisYear

            // statusDateID.setText("Date: " + thisAMonth + "/" + thisDay + "/" + thisYear)
            val newDate:Calendar =Calendar.getInstance()
            newDate.set(thisYear, thisMonth, thisDay)

            compare_format = SimpleDateFormat("yyyy-MM-dd")
            selectedYear = thisYear
            selectedMonth = thisAMonth;
            selectedDay = thisDay;


            currentYear = SimpleDateFormat("yyyy").format(Date())
            currentMonth = SimpleDateFormat("MM").format(Date())
            currentDay = SimpleDateFormat("dd").format(Date())

            showTimePikerDialog(context)
        }, thisAYear, thisAMonth, thisADay)

      //  dpd.datePicker.setScrollCaptureCallback(false)
        dpd.show()

        // mh.entryDate = date.timeInMillis
        println("DATE DATA: "+thisAYear+ " "+thisAMonth+" " + thisADay)
        // println("DATE CHANGED MILLISECS = "+mh.entryDate)
    }*/
    var selectedDate="";
    fun showDatePickerDialog(req_type:REQUEST_TYPE) {

        val datePickerDialog: Dialog = Utils.dialog(activity!!,R.layout.date_picker_dialog, false)
        val root_layout = datePickerDialog.findViewById<LinearLayout>(R.id.root_layout)

        val datePickerView=datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
        val timePicker=datePickerDialog.findViewById<RangeTimePicker>(R.id.timPicker)

        datePickerView.visibility=View.VISIBLE
        timePicker.visibility=View.GONE
        datePickerView.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);


        datePickerDialog.findViewById<View>(R.id.cancelButton).setOnClickListener {
            datePickerDialog.dismiss()
        }
        when(req_type){
            REQUEST_TYPE.REQUEST_AS_LETTER->{

               var date: Calendar = Calendar.getInstance()
                var thisAYear = date.get(Calendar.YEAR).toInt()
                var thisAMonth = date.get(Calendar.MONTH).toInt()
                var thisADay = date.get(Calendar.DAY_OF_MONTH).toInt()
                date.add(Calendar.DATE, 1);
                datePickerView.datePicker.minDate=date.timeInMillis
                datePickerDialog.findViewById<View>(R.id.okButton).setOnClickListener {
                    datePickerDialog.dismiss()
                    thisAMonth = datePickerView.datePicker.month + 1
                    thisADay = datePickerView.datePicker.dayOfMonth
                    thisAYear = datePickerView.datePicker.year

                    val newDate:Calendar =Calendar.getInstance()
                    newDate.set(thisAYear, thisAMonth, datePickerView.datePicker.dayOfMonth)

                    compare_format = SimpleDateFormat("yyyy-MM-dd")
                    selectedYYYY = datePickerView.datePicker.year
                    selectedMM = thisAMonth;
                    selectedDD = datePickerView.datePicker.dayOfMonth;

                    selectedDate=""+selectedYYYY+"-"+selectedMM+"-"+selectedDD

                    currentYear = SimpleDateFormat("yyyy").format(Date())
                    currentMonth = SimpleDateFormat("MM").format(Date())
                    currentDay = SimpleDateFormat("dd").format(Date())
                    //showTimePickerDialog()
                    showTimeSlotsDialog(req_type,selectedDate)


                }


            }
            REQUEST_TYPE.REQUEST_ASAP->{



                val input_time_format = SimpleDateFormat("HH:mm")
                val outputformat = SimpleDateFormat("hh:mm aa")
                var currentHours = SimpleDateFormat("HH:mm").format(Date())
                val  toCompareCurentHoursSession = input_time_format.parse(currentHours)
                val  toCompareTimeMorningSession = input_time_format.parse("10:00")
                val  toCompareTimeEveningSession = input_time_format.parse("16:00")




                if(toCompareCurentHoursSession.after(toCompareTimeEveningSession)){ // booking for condition before 09:00AM

                    showToastLong(getString(R.string.no_slots_available_for_today))
                    datePickerDialog.dismiss()
                    return
                }

               /* else if(toCompareCurentHoursSession.before(toCompareTimeMorningSession)){ // booking for condition before 10:00AM


                }
                else {// booking for condition after 09:00AM

                }
*/








                var date: Calendar = Calendar.getInstance()
                var thisAYear = date.get(Calendar.YEAR).toInt()
                var thisAMonth = date.get(Calendar.MONTH).toInt()
                var thisADay = date.get(Calendar.DAY_OF_MONTH).toInt()
                datePickerView.datePicker.minDate=date.timeInMillis
                datePickerView.datePicker.maxDate=date.timeInMillis
                datePickerDialog.findViewById<View>(R.id.okButton).setOnClickListener {
                    datePickerDialog.dismiss()
                    thisAMonth = datePickerView.datePicker.month + 1
                    thisADay = datePickerView.datePicker.dayOfMonth
                    thisAYear = datePickerView.datePicker.year

                    val newDate:Calendar =Calendar.getInstance()
                    newDate.set(thisAYear, thisAMonth, datePickerView.datePicker.dayOfMonth)

                    compare_format = SimpleDateFormat("yyyy-MM-dd")
                    selectedYYYY = datePickerView.datePicker.year
                    selectedMM = thisAMonth;
                    selectedDD = datePickerView.datePicker.dayOfMonth;

                    selectedDate=""+selectedYYYY+"-"+selectedMM+"-"+selectedDD

                    currentYear = SimpleDateFormat("yyyy").format(Date())
                    currentMonth = SimpleDateFormat("MM").format(Date())
                    currentDay = SimpleDateFormat("dd").format(Date())
                    //showTimePickerDialog()
                    showTimeSlotsDialog(req_type,selectedDate)
                }


            }
        }



        root_layout.setOnClickListener {
            datePickerDialog.dismiss()
        }
    }

    fun showTimePickerDialog() {
        val datePickerDialog: Dialog = Utils.dialog(activity!!,R.layout.date_picker_dialog, false)
        val root_layout = datePickerDialog.findViewById<LinearLayout>(R.id.root_layout)

        val datePicker=datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
        val timePickerView=datePickerDialog.findViewById<RangeTimePicker>(R.id.timPicker)

        datePicker.visibility=View.GONE
        timePickerView.visibility=View.VISIBLE
        timePickerView.timPicker.setIs24HourView(false)
        setTimePickerInterval(timePickerView)
        timePickerView.setMaxTime(selectedDate,20,0)
        val currentDay = SimpleDateFormat("dd").format(Date())
        val currentHours = SimpleDateFormat("HH").format(Date())

        val currentMin = SimpleDateFormat("mm").format(Date())

        if(currentDay.equals(selectedDD)){
            timePickerView.setMinTime(selectedDate,currentHours.toInt(),currentMin.toInt())
        }
        else{
            timePickerView.setMinTime(selectedDate,8,0)

        }
        datePickerDialog.findViewById<View>(R.id.cancelButton).setOnClickListener {
            datePickerDialog.dismiss()
        }

        datePickerDialog.findViewById<View>(R.id.okButton).setOnClickListener {
            var hour: Int
            val minute: Int
            val am_pm: String
            if (Build.VERSION.SDK_INT >= 23) {
                hour = timePickerView.timPicker.getHour()
                minute = getMinute()
            } else {
                hour = timePickerView.timPicker.getCurrentHour()
                minute = getMinute()
            }
            if (hour > 12) {
                am_pm = "PM"
                hour = hour - 12
            } else {
                am_pm = "AM"
            }

            compare_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            date_future_booking_str = ""+selectedYYYY+"-"+selectedMM+"-"+selectedDD+" "+hour+":"+minute+" "+am_pm
            System.out.println("date_future_booking_str::"+date_future_booking_str)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm aa")

            val  date = inputFormat.parse(date_future_booking_str)
            date_future_booking_str=compare_format.format(date)

            val selectedHours = SimpleDateFormat("HH").format(date)

            val selectedMin = SimpleDateFormat("mm").format(date)
            val selectedYear = SimpleDateFormat("yyyy").format(date)

            val selectedMonth = SimpleDateFormat("MM").format(date)

            val  selectedDay = SimpleDateFormat("dd").format(date)

            val currentHours = SimpleDateFormat("HH").format(Date())

            val currentMin = SimpleDateFormat("mm").format(Date())


            val currentYear = SimpleDateFormat("yyyy").format(Date())

            val currentMonth = SimpleDateFormat("MM").format(Date())

            val currentDay = SimpleDateFormat("dd").format(Date())
            val currentHoursInt = currentHours.toInt() + 2


            System.out.println("date_future_booking_str::"+date_future_booking_str)
            System.out.println("selectedHours::"+selectedHours)
            System.out.println("selectedMin::"+selectedMin)
            System.out.println("currentHours::"+currentHours)
            System.out.println("currentHoursInt::"+currentHoursInt)
            System.out.println("currentMin::"+currentMin)

            savePreferences(CONSTANTS.date_future_booking_str,
                date_future_booking_str,
                context!!
            )

            if (currentYear.toInt() == selectedYear.toInt()) {
                if (currentMonth.toInt() == selectedMonth.toInt()) {
                    if (currentDay.toInt() == selectedDay.toInt()) {
                        if (currentHoursInt > selectedHours.toInt()) {
                            showToastLong(getString(R.string.job_time))
                        } else if (currentHoursInt < selectedHours.toInt()) {
                            moveToSelectVehicle()
                        } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() == selectedMin.toInt()) {
                            moveToSelectVehicle()
                        } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() < selectedMin.toInt()) {
                            moveToSelectVehicle()
                        } else {
                            showToastLong(getString(R.string.job_time))
                        }
                    }else{
                        moveToSelectVehicle()
                    }
                }
                else{
                    moveToSelectVehicle()
                }
            }
            else{
                moveToSelectVehicle()
            }
            datePickerDialog.dismiss()
        }



        root_layout.setOnClickListener {
            datePickerDialog.dismiss()
        }
    }
    var selectedTimeSlots="";
    private var time_slots_list:ArrayList<TimeSlot>? = null
    fun showTimeSlotsDialog(req_type:REQUEST_TYPE,selectedDate:String) {
        time_slots_list = ArrayList()
        val datePickerDialog: Dialog = Utils.dialog(activity!!, R.layout.choose_time_slots_dialog, false)
        val root_layout = datePickerDialog.findViewById<LinearLayout>(R.id.root_layout)
        Utils.animateUp((activity as AppCompatActivity?)!!,root_layout)
       // time_slots_list!!.addAll(Utils.getTimeSlotsArray(selectedDate))

        time_slots_list!!.addAll(Utils.getTimeSlots(selectedDate,req_type,Utils.getTimeSlotsArray(selectedDate)))
        System.out.println("time_slots_list::"+ time_slots_list!!.size)
        val lv_time_slots=datePickerDialog.findViewById<RecyclerView>(R.id.lv_time_slots)
      //  lv_time_slots!!.adapter = TimeSlotsAdapter(activity!!, time_slots_list!!, this)

       lv_time_slots!!.adapter = TimeSlotsAdapter(activity!!,req_type,
            time_slots_list!!, object : OnItemClickRecycler {
                override fun onClickRecycler(view: View?, position: Int) {
                    datePickerDialog.dismiss()
                    Utils.downSourceDestinationView(activity as AppCompatActivity,root_layout, datePickerDialog)
                    selectedTimeSlots= time_slots_list!![position].time
                    if(view!!.getTag().toString().isNotEmpty()){
                        selectedTimeSlots=view!!.getTag().toString()
                        System.out.println("selectedTimeSlots::"+ selectedTimeSlots)
                        savePreferences(CONSTANTS.date_future_booking_str,
                            selectedDate,
                            context!!
                        )
                        savePreferences(CONSTANTS.time_slots_future_booking_str,
                            selectedTimeSlots,
                            context!!
                        )
                        moveToSelectVehicle()
                    }
                    else{
                      //compare_format = SimpleDateFormat("yyyy-MM-dd")
                        val input_time_format = SimpleDateFormat("yyyy-MM-dd HH:mm")

                        val selectedDateTime=selectedDate+" "+selectedTimeSlots


                        val  date = input_time_format.parse(selectedDateTime)

                        System.out.println("selectedDateTime::"+selectedDateTime)

                        System.out.println("date_future_booking_str::"+selectedDate)

                        System.out.println("input_time_formate _date_future_booking_str::"+date)

                    //    date_future_booking_str=compare_format.format(selectedDate)

                        val selectedHours = SimpleDateFormat("HH").format(date)

                        val selectedMin = SimpleDateFormat("mm").format(date)
                        val selectedYear = SimpleDateFormat("yyyy").format(date)

                        val selectedMonth = SimpleDateFormat("MM").format(date)

                        val  selectedDay = SimpleDateFormat("dd").format(date)

                        val currentHours = SimpleDateFormat("HH").format(Date())

                        val currentMin = SimpleDateFormat("mm").format(Date())


                        val currentYear = SimpleDateFormat("yyyy").format(Date())

                        val currentMonth = SimpleDateFormat("MM").format(Date())

                        val currentDay = SimpleDateFormat("dd").format(Date())
                        val currentHoursInt = currentHours.toInt() + 2


                      //  System.out.println("date_future_booking_str::"+date_future_booking_str)
                        System.out.println("selectedHours::"+selectedHours)
                        System.out.println("selectedMin::"+selectedMin)
                        System.out.println("currentHours::"+currentHours)
                        System.out.println("currentHoursInt::"+currentHoursInt)
                        System.out.println("currentMin::"+currentMin)

                        savePreferences(CONSTANTS.date_future_booking_str,
                            selectedDate,
                            context!!
                        )
                        savePreferences(CONSTANTS.time_slots_future_booking_str,
                            selectedTimeSlots,
                            context!!
                        )
                        if (currentYear.toInt() == selectedYear.toInt()) {
                            if (currentMonth.toInt() == selectedMonth.toInt()) {
                                if (currentDay.toInt() == selectedDay.toInt()) {
                                    if (currentHoursInt > selectedHours.toInt()) {
                                        showToastLong(getString(R.string.job_time))
                                    } else if (currentHoursInt < selectedHours.toInt()) {
                                        moveToSelectVehicle()
                                    } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() == selectedMin.toInt()) {
                                        moveToSelectVehicle()
                                    } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() < selectedMin.toInt()) {
                                        moveToSelectVehicle()
                                    } else {
                                        showToastLong(getString(R.string.job_time))
                                    }
                                }else{
                                    moveToSelectVehicle()
                                }
                            }
                            else{
                                moveToSelectVehicle()
                            }
                        }
                        else{
                            moveToSelectVehicle()
                        }

                    }

                }
            }
        )


        datePickerDialog.findViewById<View>(R.id.imclose).setOnClickListener {
            datePickerDialog.dismiss()
            Utils.downSourceDestinationView(activity as AppCompatActivity,root_layout, datePickerDialog)

        }


        root_layout.setOnClickListener {
            datePickerDialog.dismiss()
           // Utils.downSourceDestinationView(activity as AppCompatActivity,root_layout, datePickerDialog)

        }

    }

    override fun onClickRecycler(view: View?, position: Int) {
     //   TODO("Not yet implemented")
    }
    /**
     * Set TimePicker interval by adding a custom minutes list
     *
     * @param timePicker
     */
    private fun setTimePickerInterval(timePicker: TimePicker) {
        picker=timePicker;
        timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        setMinutePicker()
    }
    private val INTERVAL = 30


  //  private val FORMATTER = DecimalFormat("00")

    private var picker: TimePicker? = null // set in onCreate
    private var minutePicker: NumberPicker? = null

    fun setMinutePicker() {
        val numValues = 60 / INTERVAL
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            val result=i*INTERVAL;
            if (result<10){
                displayedValues[i] = "0$result"
            }
            else{
                displayedValues[i] = result.toString()
            }
        }

        val minute = picker?.findViewById<NumberPicker>(Resources.getSystem().getIdentifier("minute", "id", "android"))
        if (minute != null) {
            minutePicker = minute
            minutePicker!!.minValue = 0
            minutePicker!!.maxValue = numValues - 1
            minutePicker!!.displayedValues = displayedValues
        }
    }

    fun getMinute(): Int {
        return if (minutePicker != null) {
            minutePicker!!.getValue() * INTERVAL
        } else {
            picker!!.currentMinute
        }
    }

/*
    private fun showTimePikerDialog(context: Context){
        val mTimePicker: RangeTimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = RangeTimePickerDialog(context,android.R.style.Theme_Holo_Light_Dialog,object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                compare_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                var currentHours = SimpleDateFormat("HH").format(Date())

                var currentMin = SimpleDateFormat("mm").format(Date())


                var currentYear = SimpleDateFormat("yyyy").format(Date())

                var currentMonth = SimpleDateFormat("MM").format(Date())

                var currentDay = SimpleDateFormat("dd").format(Date())
                var currentHoursInt = currentHours.toInt() + 2

                //val date_future_booking_str = compare_format.format(date)

                date_future_booking_str = ""+selectedYYYY+"-"+selectedMM+"-"+selectedDD+" "+selectedHours+":"+selectedMin+":"+"00"

                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm aa")

                val  date = inputFormat.parse(date_future_booking_str)


                 currentHours = SimpleDateFormat("HH").format(Date())

                val selectedHours = SimpleDateFormat("HH").format(date)
                 currentMin = SimpleDateFormat("mm").format(Date())
                val selectedMin = SimpleDateFormat("mm").format(date)
                val selectedYear = SimpleDateFormat("yyyy").format(date)
                currentYear = SimpleDateFormat("yyyy").format(Date())
                val selectedMonth = SimpleDateFormat("MM").format(date)
                currentMonth = SimpleDateFormat("MM").format(Date())
                val  selectedDay = SimpleDateFormat("dd").format(date)
                 currentDay = SimpleDateFormat("dd").format(Date())
                 currentHoursInt = currentHours.toInt() + 2




                date_future_booking_str=compare_format.format(date)


                savePreferences(CONSTANTS.date_future_booking_str,
                    date_future_booking_str,
                    context!!
                )

                moveToSelectVehicle()


                //   selectedTime.setText(String.format("%d : %d", hourOfDay, minute))
            }
        }, hour, minute, false)
        mTimePicker.setMax(maxHour,maxMinute)
        mTimePicker.setMin(minHour,minute)
        mTimePicker.show()
    }
*/


    private val listener: SlideDateTimeListener = object : SlideDateTimeListener() {
        override fun onDateTimeSet(date: Date) {
            val compare_format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentHours = SimpleDateFormat("HH").format(Date())
            val selectedHours = SimpleDateFormat("HH").format(date)
            val currentMin = SimpleDateFormat("mm").format(Date())
            val selectedMin = SimpleDateFormat("mm").format(date)
            val selectedYear = SimpleDateFormat("yyyy").format(date)
            val currentYear = SimpleDateFormat("yyyy").format(Date())
            val selectedMonth = SimpleDateFormat("MM").format(date)
            val currentMonth = SimpleDateFormat("MM").format(Date())
            val selectedDay = SimpleDateFormat("dd").format(date)
            val currentDay = SimpleDateFormat("dd").format(Date())
            val currentHoursInt = currentHours.toInt() + 2

            val date_future_booking_str = compare_format.format(date)
            savePreferences(
                CONSTANTS.date_future_booking_str,
                date_future_booking_str,
                context!!
            )
            //System.out: date_future_booking_str::2022-11-08 23:19:03
            System.out.println("date_future_booking_str::"+date_future_booking_str)
            System.out.println("selectedHours::"+selectedHours)
            System.out.println("selectedMin::"+selectedMin)
            System.out.println("currentHours::"+currentHours)
            System.out.println("currentHoursInt::"+currentHoursInt)
            System.out.println("currentMin::"+currentMin)





            if (currentYear.toInt() == selectedYear.toInt()) {
                if (currentMonth.toInt() == selectedMonth.toInt()) {
                    if (currentDay.toInt() == selectedDay.toInt()) {
                        if (currentHoursInt > selectedHours.toInt()) {
                            showToastLong(getString(R.string.job_time))
                        } else if (currentHoursInt < selectedHours.toInt()) {
                            moveToSelectVehicle()
                        } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() == selectedMin.toInt()) {
                            moveToSelectVehicle()
                        } else if (currentHoursInt == selectedHours.toInt() && currentMin.toInt() < selectedMin.toInt()) {
                            moveToSelectVehicle()
                        } else {
                            showToastLong(getString(R.string.job_time))
                        }
                    }else{
                        moveToSelectVehicle()
                    }
                }
                else{
                    moveToSelectVehicle()
                }
            }
            else{
                moveToSelectVehicle()
            }
        }

        override fun onDateTimeCancel() {
            // Overriding onDateTimeCancel() is optional.
        }
    }

    private fun moveToSelectVehicle() {
        startActivity(
            Intent(activity, SelectVechileActivity::class.java)
                .putExtra("distance_in_miles", distance_in_miles)
        )
        activity!!.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        custome_marker =
            (context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.dropoff,
                null
            )
        tv_marker = custome_marker!!.drop_off_tv
        btn_selected_van.setOnClickListener(this)
        calendar_booking.setOnClickListener(this)
        card_where_to.setOnClickListener(this)
        linear_pickup.setOnClickListener(this)
        iv_navigate_user_location.setOnClickListener(this)
        helper_select!!.setOnClickListener(this)
        Select_estimated_duration_spinner.setOnClickListener(this)
        estimate.setOnClickListener(this)


        // Clear Record
        savePreferences(
            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
            "",
            context!!
        )
        savePreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
            "",
            context!!
        )
        transaction = fragmentManager!!.beginTransaction()
        showMap(savedInstanceState)
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)


        resultReceiver = AppAddressResultReceiver(Handler())
        val first_name =
            getPreferences(CONSTANTS.first_name, context!!)
        user_name.setText(GetdayStatus() + ", " + first_name)
        timer = Timer()


        duration_help.setOnClickListener {
            ShowMessage()
        }


    }


    private fun ShowMessage() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.duration_help_layout, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.close.setOnClickListener {
            alertDialog.dismiss()
        }
    }


    private fun GetdayStatus(): String {
        var status = ""
        val c = Calendar.getInstance()
        val timeOfDay = c[Calendar.HOUR_OF_DAY]
        if (timeOfDay >= 0 && timeOfDay <= 11) {
            status = "Good Morning"
        } else if (timeOfDay >= 12 && timeOfDay <= 16) {
            status = "Good Afternoon"
        } else if (timeOfDay >= 17) {
            status = "Good Evening"
        }
        return status
    }

    private fun get_active_booking_detail(
        passenger_id: String?,
        RegistrationID_: String?
    ) {
        showDialog2(context)
        val url = Utils.get_active_booking_detail
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: Map<String?, String?> =
            HashMap()
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam),
                Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            if (!jsonObject.isNull("active_booking")) {


                                active_booking = Gson().fromJson<Request>(
                                    jsonObject.getJSONObject("active_booking").toString(),
                                    Request::class.java
                                )

                                AlertDialogManager.showAlertMessageWithTwoButtons(
                                    context,
                                    this@HomeFrragment,
                                    "Active_Job", "Alert",
                                    "You have an active job", "Contiune", "Dismiss"
                                )

                            } else {
                                val json_array = jsonObject.getJSONArray("unpaid_advance_offer")
                                val array = Gson().fromJson<Array<Request>>(
                                    json_array.toString(),
                                    Array<Request>::class.java
                                )
                                val arrayList = ArrayList(array.toMutableList())
                                if (arrayList.size > 0) {
                                    accept_move = arrayList.get(0)
                                    val msg =
                                        "Congratulations - Your offer has been accepted by a driver, secure your driver right away."
                                    AlertDialogManager.showAlertMessageWithTwoButtons(
                                        context,
                                        this@HomeFrragment,
                                        "Upcoming_Move", "Alert",
                                        msg, "Confirm Booking", "Cancel Offer"
                                    )
                                }
                            }


                        } else if (jsonStatus.getString("code").equals("3000")

                            || jsonStatus.getString("code").equals("2003")
                            || jsonStatus.getString("code").equals("2003")
                        ) {
                            savePreferences(
                                CONSTANTS.login,
                                "false", context!!
                            )
                            val intent = Intent(activity, Ask::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            activity!!.finish()

                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }

                    closeDialog()
                },
                Response.ErrorListener { error ->
                    VolleyLog.d(
                        "TAG",
                        "Error: " + error.message
                    )
                    closeDialog()

                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["passenger_id"] = passenger_id!!
                    headers["registration_id"] = RegistrationID_!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
    }

    private fun move_to_navgaiation_screen() {
        try {

            active_booking!!.run {
                savePreferences(
                    CONSTANTS.REQUEST_ID,
                    active_booking!!.requestId,
                    context!!
                )


                val transaction = activity!!.supportFragmentManager.beginTransaction()
                var newFragment: Fragment? = null

                when (isType) {

                    "Regular" -> {

                        when (isStatus) {
                            JobStatus.ACCEPTED.value -> {
                                newFragment = ConnectedFragment()

                            }
                            JobStatus.GO_TO_PICK_UP.value -> {
                                newFragment = FragmentVanOnItsWay()

                            }

                            JobStatus.ARRIVED.value -> {
                                newFragment = FragmentVanOnItsWay()

                            }

                            JobStatus.STARTED.value -> {
                                newFragment = FragmentMoveStarted()

                            }
                            else -> {
                                newFragment = FragmentMoveStarted()

                            }

                        }


                    }

                    "Fixed" -> {


                        when (isStatus) {
                            JobStatus.GO_TO_PICK_UP.value -> {
                                newFragment = FragmentVanOnItsWay()

                            }
                            JobStatus.ARRIVED.value -> {
                                newFragment = FragmentMoveStarted()

                            }

                            JobStatus.STARTED.value -> {
                                newFragment = FragmentMoveStarted()

                            }
                            else -> {
                                newFragment = FragmentMoveStarted()

                            }

                        }
                    }

                    else -> {
                    }


                }
                transaction.replace(R.id.container_fragments, newFragment!!)
                transaction.commitAllowingStateLoss()


            }

        } catch (error: Exception) {
            error.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        moveVehicelContinuously("0", pickip_latitude, pickup_longitude)
        view!!.hideKeyboard()
        try {
            mapView!!.onResume()
            if (mGoogleApiClient == null) {
                if (checkPlayServices()) {
                    buildGoogleApiClient()
                    createLocationRequest()
                }
            } else {
                mGoogleApiClient!!.connect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        try {
            mapView!!.onDestroy()
//            if (doAsynchronousTaskGetDriver != null) {
            timer!!.cancel()
            doAsynchronousTaskGetDriver!!.cancel()
            doAsynchronousTaskGetDriver = null
            handler.removeCallbacksAndMessages(0)
//            }
        } catch (e: Exception) {
            Timber.d("TimberExc: ${e.message}")
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onPause() {
        try {
            mapView!!.onPause()
//            if (doAsynchronousTaskGetDriver != null) {
            timer!!.cancel()
            doAsynchronousTaskGetDriver!!.cancel()
            doAsynchronousTaskGetDriver = null
            handler.removeCallbacksAndMessages(0)

//            }
        } catch (e: Exception) {
            Timber.d("TimberExc: ${e.message}")
            e.printStackTrace()
        }
        super.onPause()

    }

    override fun onDetach() {
        super.onDetach()
        try {
            mapView!!.onPause()
            if (doAsynchronousTaskGetDriver != null) {
                timer!!.cancel()
                doAsynchronousTaskGetDriver!!.cancel()
                doAsynchronousTaskGetDriver = null
                handler.removeCallbacksAndMessages(0)
            }
        } catch (e: Exception) {
            Timber.d("TimberExc: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            mapView!!.onPause()
            if (doAsynchronousTaskGetDriver != null) {
                timer!!.cancel()
                doAsynchronousTaskGetDriver!!.cancel()
                doAsynchronousTaskGetDriver = null
                handler.removeCallbacksAndMessages(0)
                // handler.removeCallbacks(doAsynchronousTaskGetDriver)

            }
        } catch (e: Exception) {
            Timber.d("TimberExc: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun onStop() {
        try {
            mapView!!.onStop()
            if (mGoogleApiClient != null) {
                stopLocationUpdates()
                mGoogleApiClient!!.disconnect()
            }
            if (doAsynchronousTaskGetDriver != null) {
                timer!!.cancel()
                doAsynchronousTaskGetDriver!!.cancel()
                doAsynchronousTaskGetDriver = null
                handler.removeCallbacksAndMessages(0) }
        } catch (e: Exception) {
            Timber.d("TimberExc: ${e.message}")
            e.printStackTrace()

        }
        super.onStop()

    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    private fun stopLocationUpdates() {
        try {
            if (mGoogleApiClient!!.isConnected) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            } else {
                mGoogleApiClient!!.connect()
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLocationChanged(location: Location) {
        if (location != null && destination_lat == 0.0 && destination_lon == 0.0) {
            if (location.latitude != 0.0 && location.longitude != 0.0) {
                this.location = location
                if (location.hasBearing()) {
                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(pickip_latitude, pickup_longitude))
                        .zoom(15f)
                        .bearing(location.bearing)
                        .tilt(0f)
                        .build()
                    //Camera Position
                    myMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        }
    }


    private fun PlacePicker() {
        if (!Places.isInitialized()) {
            Places.initialize(
                context!!,
                getString(R.string.google_map_api_key)
            )
        }
        val placeFields4 =
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        val intent_pick_up_tv = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, placeFields4
        )
            .setCountry(CONSTANTS.current_country)
            .build(context!!)
        startActivityForResult(intent_pick_up_tv, 100)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.card_where_to -> {
                val pickup = idCurrentAddress!!.text.toString()
                if (pickup == CONSTANTS.Loading) {
                    Toast.makeText(
                        context,
                        "Pickup Loacation is not Selected",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                        pickup,
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                        dropoff_tv!!.text.toString() + "",
                        context!!
                    )
                    startActivityForResult(
                        Intent(activity, ChooseWhereto::class.java).putExtra(
                            "move_edit_dropoff",
                            "move_edit_dropoff"
                        ), 9320
                    )
                }
            }
          /*  R.id.estimate -> showAlertMessage(
                context, "The nearest van is approximately " +
                        tv_nearest_driver_time!!.text
                            .toString() + " away, incase you need a van ASAP."
            )*/
            R.id.linear_pickup -> {

                PlacePicker()
            }

            R.id.calendar_booking -> {


                currentAddress = idCurrentAddress!!.text.toString()
                str_dest = dropoff_tv!!.text.toString()
                if (str_dest.isEmpty()) {
                    showToast("Select Where to?")
                }
                else if (currentAddress.contains("Current Location")) {
                    showToast("Select PickUp Loctaion")
                }
                else if (helper_select!!.text.toString() == "Need extra help with loading/offloading ?"
                    ||  helper_select!!.text.toString() == "Need extra help with loading ?") {
                    Toast.makeText(
                        context,
                        "Please select helper",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (Select_estimated_duration_spinner!!.text
                        .toString() == "Select estimated duration"
                ) {
                    Toast.makeText(context, "Select estimated duration", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    savePreferences(
                        CONSTANTS.estimated_duartion,
                        Select_estimated_duration_spinner!!.text.toString(),
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.distance_in_miles,
                        distance_in_miles,
                        context!!
                    )

                    // ShowDatePicker(activity!!.supportFragmentManager, listener)
                    showDatePickerDialog(REQUEST_TYPE.REQUEST_AS_LETTER)



                  //  showToastLong(getString(R.string.job_time))


                }

            }
            R.id.btn_selected_van -> {
              savePreferences(
                    CONSTANTS.date_future_booking_str,
                    "",
                    context!!
                )
                savePreferences(
                    CONSTANTS.estimated_duartion,
                    Select_estimated_duration_spinner!!.text.toString(),
                    context!!
                )
                savePreferences(
                    CONSTANTS.distance_in_miles,
                    distance_in_miles,
                    context!!
                )
                currentAddress = idCurrentAddress!!.text.toString()
                str_dest = dropoff_tv!!.text.toString()
               /* val nearest = tv_nearest_driver_time!!.text.toString()
                if (nearest.contains("N/A")) {
                    showToast(
                        "No Driver Near You.\nClick on Request For Later."
                    )
                } */
               // else
                if (str_dest.isEmpty()) {
                    showToast("Select Where to?")
                } else if (currentAddress.contains("Current Location")) {
                    showToast("Select PickUp Loctaion")
                } else if (helper_select!!.text
                        .toString() == "Need extra help with loading/offloading ?"
                    ||  helper_select!!.text.toString() == "Need extra help with loading ?"
                ) {
                    Toast.makeText(
                        context,
                        "Please select helper",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (Select_estimated_duration_spinner!!.text
                        .toString() == "Select estimated duration"
                ) {
                    Toast.makeText(context, "Select estimated duration", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    savePreferences(
                        CONSTANTS.date_future_booking_str,
                        "",
                        context!!
                    )
                   /*
                    startActivity( Intent(activity, SelectVechileActivity::class.java)
                     .putExtra("distance_in_miles", distance_in_miles) )
                    activity!!.overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
                    */
                    showDatePickerDialog(REQUEST_TYPE.REQUEST_ASAP)
                   // showToastLong(getString(R.string.job_time))
                }




            }
            R.id.iv_navigate_user_location -> {
                val destintaion = getPreferences(
                    CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                    context!!
                )
                if (destintaion!!.isEmpty()) {
                    myMap!!.setOnCameraIdleListener(this)
                    val latitude = location!!.latitude
                    val longitude = location!!.longitude
                    val new_lating = LatLng(latitude, longitude)
                    val cameraPosition = CameraPosition.Builder()
                        .target(new_lating).zoom(15f).build()
                    myMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    val location = Location("")
                    location.latitude = latitude
                    location.longitude = longitude
                    startAddressLocationGetService(location)
                } else {
                    val destination_lat =
                        getPreferences(
                            CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
                            context!!
                        )!!.toDouble()
                    val destination_lon =
                        getPreferences(
                            CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
                            context!!
                        )!!.toDouble()
                    val pickup_lat = LatLng(pickip_latitude, pickup_longitude)
                    val dropoff_lat = LatLng(destination_lat, destination_lon)
                    val builder = LatLngBounds.Builder()
                    builder.include(pickup_lat)
                    builder.include(dropoff_lat)
                    val bounds = builder.build()
                    val padding = 200 // offset from edges of the map in pixels
                    myMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                }
            }
            R.id.helper_select -> show_helper_popmenu(v)
            R.id.Select_estimated_duration_spinner -> show_estimated_popmenu(v)
        }
    }

    private fun show_estimated_popmenu(view: View) {
        val popup = PopupMenu(context, view)
        val list = CONSTANTS.estimated_duration_list
        for (title in list) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            val estimated_duartion = item.title.toString()
            Select_estimated_duration_spinner!!.text = estimated_duartion
            true
        }
        popup.show()
    }

    private fun show_helper_popmenu(view: View) {
        val popup = PopupMenu(context, view)
        val list = CONSTANTS.helper_list
        for (title in list) {
            popup.menu.add(title)
        }
        popup.setOnMenuItemClickListener { item ->
            val title = item.title.toString()
            helper_select!!.text = title
            if (title == CONSTANTS.helper_list[1]) {
                savePreferences(
                    CONSTANTS.helpers,
                    "0",
                    context!!
                )
                if (doAsynchronousTaskGetDriver != null) {
                    timer!!.cancel()
                    doAsynchronousTaskGetDriver!!.cancel()
                    doAsynchronousTaskGetDriver = null
                    moveVehicelContinuously("0", pickip_latitude, pickup_longitude)
                }
            } else if (title == CONSTANTS.helper_list[2]) {
                savePreferences(
                    CONSTANTS.helpers,
                    "1",
                    context!!
                )
                if (doAsynchronousTaskGetDriver != null) {
                    timer!!.cancel()
                    doAsynchronousTaskGetDriver!!.cancel()
                    doAsynchronousTaskGetDriver = null
                    moveVehicelContinuously("1", pickip_latitude, pickup_longitude)
                }
            } else if (title == CONSTANTS.helper_list[3]) {
                savePreferences(
                    CONSTANTS.helpers,
                    "2",
                    context!!
                )
                if (doAsynchronousTaskGetDriver != null) {
                    timer!!.cancel()
                    doAsynchronousTaskGetDriver!!.cancel()
                    doAsynchronousTaskGetDriver = null
                    moveVehicelContinuously("2", pickip_latitude, pickup_longitude)
                }
            } else if (title == CONSTANTS.helper_list[4]) {
                savePreferences(
                    CONSTANTS.helpers,
                    "3",
                    context!!
                )
                if (doAsynchronousTaskGetDriver != null) {
                    timer!!.cancel()
                    doAsynchronousTaskGetDriver!!.cancel()
                    doAsynchronousTaskGetDriver = null
                    moveVehicelContinuously("3", pickip_latitude, pickup_longitude)
                }
            }
            true
        }
        popup.show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                moveVehicelContinuously("0",pickip_latitude, pickup_longitude)

                if (requestCode == 100) {
                    address_manual = true

                    val place = Autocomplete.getPlaceFromIntent(data)
                    val placeName = "" + place.name + " - " + place.address
                    pickip_latitude = place.latLng!!.latitude
                    pickup_longitude = place.latLng!!.longitude
                    //TODO: vehicles
//                    getAllVehicles("0", pickip_latitude, pickup_longitude)
                    moveVehicelContinuously("0",pickip_latitude, pickup_longitude)
                    idCurrentAddress!!.text = placeName
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                        placeName + "",
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
                        pickip_latitude.toString() + "",
                        context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
                        pickup_longitude.toString() + "",
                        context!!
                    )
                    val destintaion =
                        getPreferences(
                            CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                            context!!
                        )
                    if (!destintaion!!.isEmpty()) {
                        // SHOW DRIECTIO
                        val destination_lat =
                            getPreferences(
                                CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
                                context!!
                            )!!.toDouble()
                        val destination_lon =
                            getPreferences(
                                CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
                                context!!
                            )!!.toDouble()
                        val directionFinder = DirectionFinder(
                            this,
                            LatLng(pickip_latitude, pickup_longitude),
                            LatLng(
                                destination_lat,
                                destination_lon
                            ), "path"
                        )
                        myMap!!.setOnCameraIdleListener(null)
                        directionFinder.showDirection()

                    } else {
                        val new_lating = LatLng(pickip_latitude, pickup_longitude)
                        val cameraPosition = CameraPosition.Builder()
                            .target(new_lating).zoom(15f).build()
                        myMap!!.moveCamera(
                            CameraUpdateFactory
                                .newCameraPosition(cameraPosition)
                        )
                    }
                } else if (requestCode == 9320) {
                    // if pickup chnages
                    getPreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA, context!!
                    ).let {
                        idCurrentAddress!!.text = it

                    }


                    getPreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
                        context!!
                    ).let {
                        pickip_latitude = it!!.toDouble()

                    }
                    getPreferences(
                        CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
                        context!!
                    ).let {
                        pickup_longitude = it!!.toDouble()

                    }


                    destination_lat = data.getStringExtra("latitude")!!.toDouble()
                    destination_lon = data.getStringExtra("longitude")!!.toDouble()
                    destintaion_ = data.getStringExtra("pickup")

                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                        destintaion_, context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LATITUDE_EXTRA,
                        "" + destination_lat, context!!
                    )
                    savePreferences(
                        CONSTANTS.PREFERENCE_DESTINATION_LONGITUDE_EXTRA,
                        "" + destination_lon, context!!
                    )
                    dropoff_tv!!.text = destintaion_
                    val directionFinder = DirectionFinder(
                        this,
                        LatLng(pickip_latitude, pickup_longitude),
                        LatLng(
                            destination_lat,
                            destination_lon
                        ), "path"
                    )
                    myMap!!.setOnCameraIdleListener(null)
                    directionFinder.showDirection()
                }
            }
        }
    }

    private fun showMap(savedInstanceState: Bundle?) {
        try {
            mapView!!.onCreate(savedInstanceState)
            MapsInitializer.initialize(activity)
            mapView!!.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap
        myMap!!.setOnCameraIdleListener(this)
        get_active_booking_detail(passenger_id, RegistrationID)

    }

    private fun startAddressLocationGetService(location: Location) {
        try {
            idCurrentAddress!!.text = "Loading..."
            val intent = Intent(activity, AppFetchAddressIntentService::class.java)
            intent.putExtra(CONSTANTS.RECEIVER, resultReceiver)
            intent.putExtra(CONSTANTS.LOCATION_DATA_EXTRA, location)
            activity!!.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onConnected(bundle: Bundle?) {
        startLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                activity!!, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 565
            )


        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest,
                this
            )
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            CONSTANTS.current_country = GetCountry(location)
            val destintaion = getPreferences(
                CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                context!!
            )
            if (!address_manual) {
                if (!destintaion!!.isEmpty()) {
                    iv_navigate_user_location!!.performClick()
                } else {
                    if (location != null) {
                        pickip_latitude = location!!.latitude
                        pickup_longitude = location!!.longitude
                        myMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    pickip_latitude,
                                    pickup_longitude
                                ), 15f
                            )
                        )
                    }
                }
            }
        }

    }

    private fun GetCountry(location: Location?): String {
        var mCountryCode = "UK"
        try {
            val geocoder: Geocoder
            val addresses: List<Address>
            geocoder = Geocoder(context, Locale.getDefault())
            addresses = geocoder.getFromLocation(
                location!!.latitude,
                location.longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            mCountryCode = addresses[0]
                .countryCode // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mCountryCode
    }

    override fun onDirectionFinderStart(operationName: String?) {
        if (operationName == "path") {
            if (polylinePaths != null) {
                for (polyline in polylinePaths!!) {
                    polyline.remove()
                }
            }
        }
    }

    override fun onDirectionFinderSuccess(
        routes: List<Route?>?,
        operationName: String?
    ) {
        if (operationName == "path") {
            polylinePaths = ArrayList()
            for (route in routes!!) {
                if (marker != null) {
                    marker!!.remove()
                    marker = null
                }
                move_duration = route!!.duration!!.text
                CONSTANTS.move_duration = move_duration
                val distance_meter = route.distance!!.value
                distance_in_miles = KmToMile("" + distance_meter)
                val endLocation_lat = route.endLocation!!.latitude
                val endLocation_long = route.endLocation!!.longitude
                val starLocation_lat = route.startLocation!!.latitude
                val starLocation_long = route.startLocation!!.longitude
                val startlatlng = LatLng(starLocation_lat, starLocation_long)
                val enlatlng = LatLng(endLocation_lat, endLocation_long)
                val icon =
                    BitmapDescriptorFactory.fromResource(R.drawable.black_marker)
                if (source_marker != null) {
                    source_marker!!.remove()
                }
                if (destination_marker != null) {
                    destination_marker!!.remove()
                }
                source_marker = myMap!!.addMarker(
                    MarkerOptions()
                        .position(startlatlng)
                        .icon(icon)

                )
                tv_marker!!.text = move_duration + " TO DROP OFF"
                val icon2 = BitmapDescriptorFactory.fromBitmap(
                    createDrawableFromView(
                        context!!
                        , custome_marker!!
                    )
                )
                destination_marker = myMap!!.addMarker(
                    MarkerOptions()
                        .position(enlatlng)
                        .icon(icon2)

                )
                destination_marker!!.showInfoWindow()
                val polylineOptions =
                    PolylineOptions().geodesic(true).color(Color.BLACK).width(7f)
                for (i in route.points!!.indices){
                    polylineOptions.add(route.points!![i])
                    polylinePaths!!.add(myMap!!.addPolyline(polylineOptions))
                }
            }
            val pick = LatLng(pickip_latitude, pickup_longitude)
            val dest = LatLng(
                destination_lat,
                destination_lon
            )
            val builder = LatLngBounds.Builder()
            builder.include(pick)
            builder.include(dest)
            val bounds = builder.build()
            val padding = 200
            try {
                myMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
            } catch (error: Exception) {
                error.printStackTrace()
            }
        }
    }

    private fun createDrawableFromView(
        context: Context,
        view: View
    ): Bitmap? {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    val chatWindowConfiguration: ChatWindowConfiguration
        get() {
            val email = getPreferences(CONSTANTS.username, context!!)
            val first_name =
                getPreferences(CONSTANTS.first_name, context!!)
            val last_name =
                getPreferences(CONSTANTS.last_name, context!!)
            val full_name = "$first_name $last_name"
            return ChatWindowConfiguration(
                CONSTANTS.KEY_LICENCE_NUMBER, null, full_name, email,
                null
            )
        }
    override fun onCameraChange(cameraPosition: CameraPosition) {}
    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name.equals("Upcoming_Move")) {
            myMap!!.setOnCameraIdleListener(this)
            startActivity(Intent(context, AdvancedPaymentActivity::class.java).apply {
                putExtra("request_id", accept_move!!.requestId)
            })

        } else if (dialog_name.equals("cancel_offer")) {

            showDialogForContact()
//            cancelRequest(
//                passenger_id, RegistrationID,
//                accept_move!!.requestId
//            )

        } else if (dialog_name.equals("cancel_move")) {
            cancelRequest(
                passenger_id, RegistrationID, active_booking!!.requestId
            )

        } else if (dialog_name.equals("Active_Job")) {

            move_to_navgaiation_screen()
        } else if (dialog_name.equals("contact_support_dialog")){

            val intent = Intent(context, ChatWindowActivity::class.java)
            val config = chatWindowConfiguration
            intent.putExtras(config.asBundle())
            startActivity(intent)
        }

    }

    private fun showDialogForContact() {
        showAlertMessageWithTwoButtons(
            activity,
            this,
            "contact_support_dialog",
            "Alert",
            "Please contact booking team to cancel your offer",
            "Live Chat",
            "Call"
        )
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {
        if (dialog_name.equals("Upcoming_Move")) {
            // cancel offer
            AlertDialogManager.showAlertMessageWithTwoButtons(
                context,
                this@HomeFrragment,
                "cancel_offer", "Alert",
                "Do you want to cancel your offer ?", "Yes", "No"
            )
        } else if (dialog_name.equals("Active_Job")) {
            AlertDialogManager.showAlertMessageWithTwoButtons(
                context,
                this@HomeFrragment,
                "cancel_move", "Alert",
                "Are you sure you want to cancel the job ?", "Yes", "No"
            )
        } else if (dialog_name.equals("contact_support_dialog")){
            DIAL_NUMBER(CONSTANTS.Booking_Team)
        }
    }


    private fun DIAL_NUMBER(number: String) {
        try {
            val uri = "tel:$number"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelRequest(
        passenger_id: String?,
        RegistrationID_: String?,
        request_id: String?
    ) {
        showDialog2(context)

        val url = Utils.update_booking_request_status_passenger
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = request_id
        postParam["status"] = "CANCELLED"
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            showToast(
                                "Booking Request Cancelled"
                            )
                        } else {
                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    closeDialog()

                },
                Response.ErrorListener { error ->
                    closeDialog()

                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["passenger_id"] = passenger_id!!
                    headers["registration_id"] = RegistrationID_!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }


    internal inner class AppAddressResultReceiver(handler: Handler?) :
        ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            if (resultData == null) {
                GetAddressBackEndTask().execute()
                return
            }
            val mAddressOutput = resultData.getString(CONSTANTS.RESULT_DATA_KEY)
            if (mAddressOutput == null) {
                GetAddressBackEndTask().execute()
            } else {
                try {
                    if (mAddressOutput != getString(R.string.service_not_available)) {
                        savePreferences(
                            CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                            mAddressOutput,
                            context!!
                        )
                        idCurrentAddress!!.text = mAddressOutput
                        idCurrentAddress!!.isSelected = true
                    } else {
                        GetAddressBackEndTask()
                            .execute()
                    }
                } catch (error: Exception) {
                    error.printStackTrace()
                }
            }
        }
    }


    private fun moveVehicelContinuously(
        helper: String,
        lat: Double,
        lng: Double
    ) {
        timer = Timer()
        doAsynchronousTaskGetDriver = object : TimerTask() {
            override fun run() {
                handler.post {
                    //  if (marker != null) {
                    getAllVehicles(helper, lat, lng)
                    runAPIGetDriversAround(helper, lat, lng)
                    //  }
                }
            }
        }
        timer!!.schedule(doAsynchronousTaskGetDriver, 0, 5000)
    }

    private fun getAllVehicles(
        helper: String,
        lat: Double,
        lng: Double
    ) {
        val url =
            Utils.get_vehicle_classes_passenger_all_Url
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["latitude"] = "" + lat
        postParam["longitude"] = "" + lng
        postParam["helpers_count"] = helper
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url,
                JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    val result = jsonObject.toString()
                    Utils.GetMinTime.clear()
                    Utils.DriversArroundList.clear()
                    try {
                        var getNearestTime = 1

                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000")) {
                            val first = JSONObject(result)
                            val JA = first.getJSONArray("data")
                            try {
                                for (i in 0 until JA.length()) {
                                    val jsonData = JA.getJSONObject(i)
                                    val jsonArray_drivers =
                                        jsonData.getJSONArray("drivers")
                                    if (jsonArray_drivers.length() > 0) {
                                        getNearestTime = 0
                                    }
                                    for (i2 in 0 until jsonArray_drivers.length()) {
                                        val jsonData2 =
                                            jsonArray_drivers.getJSONObject(i2)
                                        driver_id = jsonData2.getString("driver_id")
                                        vehicle_class_id =
                                            jsonData2.getString("vehicle_class_id")
                                        vehicle_class_name =
                                            jsonData2.getString("vehicle_class_name")
                                        latitude_driver = jsonData2.getString("latitude")
                                        longitude_driver = jsonData2.getString("longitude")
                                        distance_driver = jsonData2.getString("distance")
                                        val drivers = GetDrivers(
                                            driver_id!!,
                                            vehicle_class_id!!,
                                            vehicle_class_name!!,
                                            latitude_driver!!,
                                            longitude_driver!!,
                                            distance_driver!!
                                        )
                                        Utils.DriversArroundList.add(
                                            drivers
                                        )
/*
                                        if (getNearestTime == 0) {
                                            try {
                                                val distance =
                                                    distance_driver!!.toDouble()
                                                val seconds =
                                                    distance * 1000 / 6.8
                                                minute = seconds.toInt() / 60
                                                val minTime = MinTime(minute!!)
                                                Utils.GetMinTime.add(
                                                    minTime
                                                )
                                                Log.d(
                                                    "time_int",
                                                    " time_int" + minute + "minute2 " + " distance " + distance
                                                )
                                                getNearestTime = 1
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
*/
                                    }
                                    getNearestTime = 0
                                    minute = null
                                }
                              /*  if (shoertestTime == null) {
                                    tv_nearest_driver_time!!.text = "ETA\nN/A"
                                } else if (shoertestTime == 0 || shoertestTime == 1) {
                                    tv_nearest_driver_time!!.text = "ETA\n1\nMIN"
                                } else {
                                    tv_nearest_driver_time!!.text = "ETA\n${shoertestTime}\nMIN"
                                }*/
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                ,
                Response.ErrorListener { error ->
                    VolleyLog.d(
                        "TAG",
                        "Error: " + error.message
                    )
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["registration_id"] = RegistrationID!!
                    headers["passenger_id"] = passenger_id!!
                    return headers
                }
            }
        if  (context != null) {
            MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
        }
    }

    private val shoertestTime: Int?
        get() {
            var shortest: Int? = null

            if (Utils.GetMinTime.size != 0) {
                shortest = Utils.GetMinTime[0].min_time
                for (i in 1 until Utils.GetMinTime.size) {
                    if (shortest!! > Utils.GetMinTime[i].min_time) {
                        shortest = Utils.GetMinTime[i].min_time
                    }
                }
            }
            return shortest
        }
    val onLineDriverMarkers: ArrayList<Marker> = arrayListOf()
    private fun runAPIGetDriversAround(helper: String, lat: Double, lng: Double) {
        val marker_options = MarkerOptions()
        val url = Utils.get_vehicle_classes_passenger_all_Url
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> = HashMap()
        postParam["latitude"] = "" + lat
        postParam["longitude"] = "" + lng
        postParam["only_driver"] = ""
        postParam["helpers_count"] = "0"

        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.POST, url, JSONObject(postParam as Map<*, *>), Response.Listener
        { jsonObject ->
            Log.d("TAG", jsonObject.toString())
            val result = jsonObject.toString()

            val jsonStatus = jsonObject.getJSONObject("status")
            if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                val first = JSONObject(result)
                val jsonArrayDrivers = first.getJSONArray("drivers")
                drivers_around_list!!.removeAll(drivers_around_list!!)
                for (marker in onLineDriverMarkers){
                    marker.remove()
                }
                onLineDriverMarkers.removeAll(onLineDriverMarkers)
                //         if (drivers_around_list!!.size == 0) {
                for (i in 0 until jsonArrayDrivers.length()) {
                    val jsonObject_driver =
                        jsonArrayDrivers.getJSONObject(i)
                    val driver_id =
                        jsonObject_driver.getString("driver_id")
                    val is_porter =
                        jsonObject_driver.getString("is_porter")
                    val latitude =
                        jsonObject_driver.getString("latitude")
                    val longitude =
                        jsonObject_driver.getString("longitude")
                    val distance =
                        jsonObject_driver.getString("distance")
                    val latLng_driver = LatLng(
                        latitude.toDouble(),
                        longitude.toDouble()
                    )
                    marker_options.position(latLng_driver)

                    if (is_porter == "1") {
                        marker_options.icon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_marker_porter
                            )
                        )
                    } else {
                        marker_options.icon(
                            BitmapDescriptorFactory.fromResource(
                                R.drawable.ic_marker_driver
                            )
                        )
                    }
                    marker_options.flat(true)
                    val marker = myMap!!.addMarker(marker_options)
                    onLineDriverMarkers.add(marker)
                    drivers_around_list!!.add(
                        Drivers(
                            driver_id,
                            is_porter,
                            distance,
                            latLng_driver,
                            marker,
                            true,
                            is_porter
                        )
                    )
                }
                //                 }
//                    else{
//                        for (i in 0 until jsonArrayDrivers.length()) {
//                            val jsonObject_driver =
//                                jsonArrayDrivers.getJSONObject(i)
//                            val driver_id =
//                                jsonObject_driver.getString("driver_id")
//                            val is_porter =
//                                jsonObject_driver.getString("is_porter")
//                            val latitude =
//                                jsonObject_driver.getString("latitude")
//                            val longitude =
//                                jsonObject_driver.getString("longitude")
//                            val distance =
//                                jsonObject_driver.getString("distance")
//                            val latLng_driver = LatLng(
//                                latitude.toDouble(),
//                                longitude.toDouble()
//                            )
//                            marker_options.position(latLng_driver)
//
//                            if (drivers_around_list!!.any {
//                                        drivers ->
//                                    drivers.driver_id == driver_id
//                                }){
//                                drivers_around_list!![i].driver_updated_latlng = latLng_driver
//                            }else{
//                                if (is_porter == "1") {
//                                    marker_options.icon(
//                                        BitmapDescriptorFactory.fromResource(
//                                            R.drawable.ic_marker_porter
//                                        )
//                                    )
//                                } else {
//                                    marker_options.icon(
//                                        BitmapDescriptorFactory.fromResource(
//                                            R.drawable.ic_marker_driver
//                                        )
//                                    )
//                                }
//                                marker_options.flat(true)
//                                val marker = myMap!!.addMarker(marker_options)
//                                drivers_around_list!!.add(
//                                    Drivers(
//                                        driver_id,
//                                        is_porter,
//                                        distance,
//                                        latLng_driver,
//                                        marker,
//                                        true,
//                                        is_porter
//                                    )
//                                )
//                            }
//                        }
//                    }
            }
        },
            Response.ErrorListener { error ->
                VolleyLog.d(
                    "TAG",
                    "Error: " + error.message
                )
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["registration_id"] = RegistrationID!!
                headers["passenger_id"] = passenger_id!!
                return headers
            }
        }
        if (context != null) {
            MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
        }
    }









//    private fun runAPIGetDriversAround_1(helper: String, lat: Double, lng: Double) {
//        val url = Utils.get_vehicle_classes_passenger_all_Url
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//            updateTLS(context)
//        }
//        val postParam: MutableMap<String?, String?> = HashMap()
//        postParam["latitude"] = "" + lat
//        postParam["longitude"] = "" + lng
////        postParam["only_driver"] = ""
//        postParam["helpers_count"] = "0"
//
//        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.POST, url, JSONObject(postParam as Map<*, *>), Response.Listener
//        { jsonObject ->
//                    Log.d("TAG", jsonObject.toString())
//                    val result = jsonObject.toString()
//                    try {
//                        val jsonStatus = jsonObject.getJSONObject("status")
//                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
//                            val first = JSONObject(result)
//                            val data_array = first.getJSONArray("data")
//                            try {
//                                for (j in 0 until data_array.length()) {
//                                    val jsonData = data_array.getJSONObject(j)
//                                    val id = jsonData.getString("id")
//
//                                    // for movbment
//                                    val jsonArray_drivers = jsonData.getJSONArray("drivers")
//                                    val marker_options = MarkerOptions()
//                                    if (jsonArray_drivers.length() > 0) {
//                                        try {
//                                            if (drivers_around_list!!.size == 0) {
//                                                for (i in 0 until jsonArray_drivers.length()) {
//                                                    val jsonObject_driver =
//                                                        jsonArray_drivers.getJSONObject(i)
//                                                    val driver_id =
//                                                        jsonObject_driver.getString("driver_id")
//                                                    val vehicle_class_id =
//                                                        jsonObject_driver.getString("vehicle_class_id")
//                                                    val latitude =
//                                                        jsonObject_driver.getString("latitude")
//                                                    val longitude =
//                                                        jsonObject_driver.getString("longitude")
//                                                    val distance =
//                                                        jsonObject_driver.getString("distance")
//                                                    val latLng_driver = LatLng(
//                                                        latitude.toDouble(),
//                                                        longitude.toDouble()
//                                                    )
//                                                    marker_options.position(latLng_driver)
//
//                                                    if (vehicle_class_id == "13") {
//                                                        marker_options.icon(
//                                                            BitmapDescriptorFactory.fromResource(
//                                                                R.drawable.ic_marker_porter
//                                                            )
//                                                        )
//                                                    } else {
//                                                         marker_options.icon(
//                                                            BitmapDescriptorFactory.fromResource(
//                                                                R.drawable.ic_marker_driver
//                                                            )
//                                                        )
//                                                    }
//                                                    marker_options.flat(true)
//                                                    val marker = myMap!!.addMarker(marker_options)
//                                                    drivers_around_list!!.add(
//                                                        Drivers(
//                                                            driver_id,
//                                                            vehicle_class_id,
//                                                            distance,
//                                                            latLng_driver,
//                                                            marker,
//                                                            true
//                                                        )
//                                                    )
//                                                }
//                                            } else {
//                                                for (i in drivers_around_list!!.indices) {
//                                                    val driver =
//                                                        drivers_around_list!![i]
//                                                    drivers_around_list!![i] = Drivers(
//                                                        driver.driver_id,
//                                                        driver.vehicle_class_id,
//                                                        driver.distance,
//                                                        driver.driver_updated_latlng,
//                                                        driver.marker,
//                                                        false
//                                                    )
//                                                }
//                                                for (i in 0 until jsonArray_drivers.length()) {
//                                                    val jsonObject_driver =
//                                                        jsonArray_drivers.getJSONObject(i)
//                                                    val driver_id =
//                                                        jsonObject_driver.getString("driver_id")
//                                                    val vehicle_class_id =
//                                                        jsonObject_driver.getString("vehicle_class_id")
//                                                    val latitude =
//                                                        jsonObject_driver.getString("latitude")
//                                                    val longitude =
//                                                        jsonObject_driver.getString("longitude")
//                                                    val distance =
//                                                        jsonObject_driver.getString("distance")
//                                                    val latLng_driver = LatLng(
//                                                        latitude.toDouble(),
//                                                        longitude.toDouble()
//                                                    )
//                                                    val new_driver = Drivers(
//                                                        driver_id, vehicle_class_id, distance,
//                                                        latLng_driver
//                                                    )
//                                                    if (isDriverAvailableOnList(
//                                                            driver_id,
//                                                            drivers_around_list
//                                                        )
//                                                    ) {
//                                                        val old_driver =
//                                                            drivers_around_list!![driver_available_position]
//                                                        if (new_driver.driver_id == old_driver.driver_id) {
//                                                            val driver_marker =
//                                                                old_driver.marker
//                                                            val previous_location =
//                                                                Location("")
//                                                            previous_location.latitude =
//                                                                driver_marker!!.position.latitude
//                                                            previous_location.longitude =
//                                                                driver_marker.position.longitude
//                                                            val update_location =
//                                                                Location("")
//                                                            update_location.latitude =
//                                                                new_driver.driver_updated_latlng.latitude
//                                                            update_location.longitude =
//                                                                new_driver.driver_updated_latlng.longitude
//                                                            val distance_covered =
//                                                                previous_location.distanceTo(
//                                                                    update_location
//                                                                )
//                                                            if (distance_covered >= CONSTANTS.DISTANCE_TO_ROTATE) {
//                                                                val bearing =
//                                                                    previous_location.bearingTo(
//                                                                        update_location
//                                                                    )
//                                                                Log.d(
//                                                                    "driver_bearing",
//                                                                    "Bearing: $bearing"
//                                                                )
//                                                                driver_marker.rotation = bearing
//                                                                smoothlyMoveTaxi(
//                                                                    driver_marker,
//                                                                    new_driver.driver_updated_latlng
//                                                                )
//                                                            }
//                                                            drivers_around_list!![i] = Drivers(
//                                                                old_driver.driver_id,
//                                                                old_driver.vehicle_class_id,
//                                                                new_driver.distance,
//                                                                new_driver.driver_updated_latlng,
//                                                                old_driver.marker,
//                                                                true
//                                                            )
//                                                        } else {
//                                                            marker_options.position(new_driver.driver_updated_latlng)
//                                                            if (vehicle_class_id == "13") {
//                                                                marker_options.icon(
//                                                                    BitmapDescriptorFactory.fromResource(
//                                                                        R.drawable.ic_marker_porter
//                                                                    )
//                                                                )
//                                                            } else {
//                                                                marker_options.icon(
//                                                                    BitmapDescriptorFactory.fromResource(
//                                                                        R.drawable.ic_marker_driver
//                                                                    )
//                                                                )
//                                                            }
//                                                            marker_options.flat(true)
//                                                            val marker =
//                                                                myMap!!.addMarker(marker_options)
//                                                            drivers_around_list!!.add(
//                                                                Drivers(
//                                                                    driver_id,
//                                                                    vehicle_class_id,
//                                                                    distance,
//                                                                    new_driver.driver_updated_latlng,
//                                                                    marker,
//                                                                    true
//                                                                )
//                                                            )
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            for (i in drivers_around_list!!.indices) {
//                                                val driver = drivers_around_list!![i]
//                                                if (!driver.Is_driver_available()) {
//                                                    driver.marker!!.remove()
//                                                    drivers_around_list!!.removeAt(i)
//                                                }
//                                            }
//
//
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                        }
//                                    } else {
//                                        for (i in drivers_around_list!!.indices) {
//                                            val driver = drivers_around_list!![i]
//                                            if (driver.vehicle_class_id.equals(id)) {
//                                                driver.marker!!.remove()
//                                                drivers_around_list!!.removeAt(i)
//                                            }
//                                        }
//                                    }
//                                }
//                            } catch (e: Exception) {
//                                e.printStackTrace()
//                            }
//                        } else if (jsonStatus.getString("message").contains("Invalid")) {
//                            savePreferences(
//                                CONSTANTS.login,
//                                "false",
//                                context!!
//                            )
//                            val intent = Intent(context, Ask::class.java)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                            startActivity(intent)
//                            activity!!.finish()
//                        } else if (jsonStatus.getString("message")
//                                .contains("User is not logged in")
//                        ) {
//                            savePreferences(
//                                CONSTANTS.login,
//                                "false",
//                                context!!
//                            )
//                            val intent = Intent(context, Ask::class.java)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                            startActivity(intent)
//                            activity!!.finish()
//                        } else {
//                            Toast.makeText(
//                                context,
//                                jsonStatus.getString("message"),
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                        // Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                ,
//                Response.ErrorListener { error ->
//                    VolleyLog.d(
//                        "TAG",
//                        "Error: " + error.message
//                    )
//                }
//            ) {
//                override fun getHeaders(): Map<String, String> {
//                    val headers =
//                        HashMap<String, String>()
//                    headers["Content-Type"] = "application/json; charset=utf-8"
//                    headers["registration_id"] = RegistrationID!!
//                    headers["passenger_id"] = passenger_id!!
//                    return headers
//                }
//            }
//        MyRequestQueue.getRequestInstance(context!!)!!.addRequest(jsonObjReq)
//    }

    private fun smoothlyMoveTaxi(
        driver_marker: Marker?,
        final_position: LatLng
    ) {
        val startPosition = driver_marker!!.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 5000f
        val hideMarker = false
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                val currentPosition = LatLng(
                    startPosition.latitude * (1 - t) + final_position.latitude * t,
                    startPosition.longitude * (1 - t) + final_position.longitude * t
                )
                driver_marker.position = currentPosition

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 14)
                } else {
                    if (hideMarker) {
                        driver_marker.isVisible = false
                    } else {
                        driver_marker.isVisible = true
                    }
                }
            }
        })
    }

    private fun isDriverAvailableOnList(
        driver_id: String,
        drivers_around_list: ArrayList<Drivers>?
    ): Boolean {
        for (i in drivers_around_list!!.indices) {
            val drivers = drivers_around_list[i]
            if (drivers.driver_id == driver_id) {
                driver_available_position = i
                return true
            }
        }
        return false
    }

    override fun onCameraIdle() {
        pickip_latitude = myMap!!.cameraPosition.target.latitude
        pickup_longitude = myMap!!.cameraPosition.target.longitude
        savePreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LATITUDE_EXTRA,
            "" + pickip_latitude,
            context!!
        )
        savePreferences(
            CONSTANTS.PREFERENCE_PICK_UP_LONGITUDE_EXTRA,
            "" + pickup_longitude,
            context!!
        )

        val latLng = LatLng(pickip_latitude, pickup_longitude)
        if (marker != null) {
            marker!!.remove()
        }
        val icon = BitmapDescriptorFactory.fromResource(R.drawable.black_marker)
        marker = myMap!!.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(icon)
        )
        val location = Location("")
        location.latitude = pickip_latitude
        location.longitude = pickup_longitude
        startAddressLocationGetService(location)

        try {
            if (doAsynchronousTaskGetDriver != null) {
                timer!!.cancel()
                doAsynchronousTaskGetDriver!!.cancel()
                doAsynchronousTaskGetDriver = null
            }
            moveVehicelContinuously(
                "0", marker!!.position.latitude,
                marker!!.position.longitude
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class GetAddressBackEndTask :
        AsyncTask<String?, Void?, String?>() {
        var pick_up_address: String? = null

        override fun onPostExecute(result: String?) {
            if (TextUtils.isEmpty(result)) {
                idCurrentAddress!!.text = "Unnamed Location"
            } else {
                savePreferences(
                    CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                    result,
                    context!!
                )
                idCurrentAddress!!.text = result
            }
        }

        override fun onPreExecute() {}
        protected override fun onProgressUpdate(vararg values: Void?) {}
        override fun doInBackground(vararg params: String?): String? {
            try {
                try {
                    pick_up_address = getStringFromLocation(
                        pickip_latitude,
                        pickup_longitude
                    )
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                println(e)
            }
            if (TextUtils.isEmpty(pick_up_address)) {
                pick_up_address = "Unnamed Location"
            }
            return pick_up_address
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setInterval(CONSTANTS.UPDATE_INTERVAL.toLong())
        mLocationRequest!!.setFastestInterval(CONSTANTS.FATEST_INTERVAL.toLong())
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setSmallestDisplacement(CONSTANTS.DISPLACEMENT.toFloat())
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }

    private fun checkPlayServices(): Boolean {
        val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    activity,
                    CONSTANTS.PLAY_SERVICES_RESOLUTION_REQUEST
                ).show()
            } else {
                showToastTest(
                    activity,
                    "This device is not supported"
                )
                activity!!.finish()
            }
            return false
        }
        return true
    }

    companion object {
        @Throws(IOException::class, JSONException::class)
        private fun getStringFromLocation(lat: Double, lng: Double): String? {
            var address_loc: String? = null
            val address = String.format(
                Locale.getDefault(),
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=%1\$f,%2\$f&sensor=true&language="
                        + Locale.getDefault().country + "&key=" +
                        R.string.google_map_api_key
                ,
                lat,
                lng
            )
            val httpGet = HttpGet(address)
            val client: HttpClient = DefaultHttpClient()
            val response: HttpResponse
            val stringBuilder = StringBuilder()
            var retList: MutableList<Address?>? = null
            response = client.execute(httpGet)
            val entity = response.entity
            val stream = entity.content
            var b: Int
            while (stream.read().also { b = it } != -1) {
                stringBuilder.append(b.toChar())
            }
            val jsonObject = JSONObject(stringBuilder.toString())
            retList = ArrayList()
            if ("OK".equals(jsonObject.getString("status"), ignoreCase = true)) {
                val results = jsonObject.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val result = results.getJSONObject(i)
                    val indiStr = result.getString("formatted_address")
                    if (i == 0) {
                        address_loc = indiStr
                    }
                    val addr =
                        Address(Locale.getDefault())
                    addr.setAddressLine(0, indiStr)
                    retList.add(addr)
                }
            }
            return address_loc
        }
    }
}