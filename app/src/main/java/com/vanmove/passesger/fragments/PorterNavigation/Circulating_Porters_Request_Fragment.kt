package com.vanmove.passesger.fragments.PorterNavigation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.MainScreenActivity
import com.vanmove.passesger.fragments.PorterHistory.PorterHistory_Tab_Fragment
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.model.APIModel.PorterHistoryModel
import com.vanmove.passesger.model.PorterDetailHistory
import com.vanmove.passesger.utils.AlertDialogManager
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.fragment_circulating_porter_request.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class Circulating_Porters_Request_Fragment : Fragment(R.layout.fragment_circulating_porter_request),
    View.OnClickListener, OnClickTwoButtonsAlertDialog {
    private var arrayList_current_Porters: ArrayList<PorterHistoryModel> = ArrayList()
    private var registerationId: String? = null
    private var passenger_id: String? = null
    private var requestId: String? = null
    private var porter_id: String? = null

    var mobile: String? = ""
    var alertDialog: AlertDialog? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerationId =
            Utils.getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            Utils.getPreferences(CONSTANTS.passenger_id, context!!)

        linkViews()
    }


    private fun linkViews() {

        view!!.btn_status!!.setOnClickListener {
            val transaction =
                fragmentManager!!.beginTransaction()
            transaction.replace(R.id.container_fragments, PorterHistory_Tab_Fragment())
            transaction.commitAllowingStateLoss()
        }
    }

    override fun onResume() {
        super.onResume()
        context!!.registerReceiver(
            mMessageReceiver,
            IntentFilter(CONSTANTS.unique_name_accepted_porter)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            context!!.unregisterReceiver(mMessageReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {}
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (intent.getStringExtra("porter_not_found").equals("true", ignoreCase = true)) {
//                showToast("Sorry No Porter Found")
                showDialogNoPorterFound()
            } else {
                currentPorters

//                val porter = arrayList_current_Porters[0]
//                val porter_id = porter.porterId
//
//                startActivity(Intent(activity, PorterNavigationActivty::class.java).apply {
//                    putExtra("porter_id", porter)
//                    putExtra("pic", porter.picture)
//                    putExtra("email", porter.email)
//                    putExtra("full_name", porter.firstName + " " + porter.last_name)
//                    putExtra("mobile", porter.mobile)
//                    putExtra("porter_request_id", porter.requestId)
//                })
//                val newFragment = PorterHistory_Tab_Fragment()
//                val transaction =
//                    fragmentManager!!.beginTransaction()
//
//                newFragment.arguments = Bundle().apply {
//                    putString(CONSTANTS.from_circulation_screen, CONSTANTS.from_circulation_screen)
//
//                }
//                transaction.replace(R.id.container_fragments, newFragment)
//                transaction.commitAllowingStateLoss()

            }
        }
    }

    private fun showDialogNoPorterFound() {
        AlertDialogManager.showAlertMessageWithTwoButtons(
            activity,
            this,
            "no_porter_found",
            "Alert",
            "Sorry, there are no porters currently available in your area.",
            "Call office",
            "Cancel Request"
        )
    }

    private val currentPorters: Unit
        get() {
            porter_id = Utils.getPreferences(CONSTANTS.PORTER_ID, context!!)
            requestId = Utils.getPreferences(CONSTANTS.REQUEST_ID, context!!)
            val req = requestId
            Log.d("IDs", "RequestId: $requestId, porterId: $porter_id")

            val postParam: MutableMap<String?, String?> =
                HashMap()
            postParam["request_id"] = requestId
            postParam["user_type"] = "passenger"
            postParam["porter_id"] = porter_id

            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(postParam as Map<*, *>).toString()
            )
            val headers: MutableMap<String?, String?> =
                HashMap()
            headers["Content-Type"] = "application/json; charset=utf-8"
            headers["registration_id"] = registerationId
            headers["user_id"] = passenger_id
            CONSTANTS.mApiService.currentPorterDetail(body, headers)!!
                .enqueue(object : Callback<PorterDetailHistory?> {
                    override fun onResponse(
                        call: Call<PorterDetailHistory?>,
                        response: Response<PorterDetailHistory?>
                    ) {
                        if (response.body() != null) {
                            if (response.body()!!.status!!.code == "1000") {
                                val porterHistory = response.body()
                                val jsonObject = response.body()!!.porters
//                                arrayList_current_Porters =
//                                    response.body()!!.current_porter_jobs as ArrayList<PorterHistoryModel>
//                                if (arrayList_current_Porters.size == 0) {
//                                    view!!.tv_is_data_available!!.visibility = View.VISIBLE
//                                } else {
//                                    view!!.tv_is_data_available!!.visibility = View.GONE
//                                }
//                                val porter = arrayList_current_Porters
//                                val porter_id = porter.porterId

                                startActivity(Intent(activity, PorterNavigationActivty::class.java).apply {
                                    putExtra("porter_id", porter_id)
                                    Log.d("Circulating:", "porterId: $porter_id")
                                    putExtra("pic", jsonObject!!.picture)
                                    putExtra("email", jsonObject.email)
                                    putExtra("full_name", jsonObject.firstName + " " + Utils.getPreferences(CONSTANTS.PORTER_LAST_NAME, context!!))
                                    putExtra("mobile", jsonObject.mobile)
                                    putExtra("porter_request_id", jsonObject.requestId)

                                })
                            } else {
                                Toast.makeText(
                                    context,
                                    response.body()!!.status!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, response.raw().message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(
                        call: Call<PorterDetailHistory?>,
                        e: Throwable
                    ) {
                        Utils.showToast(e.message)
                    }
                })
        }

    override fun clickPositiveDialogButton(dialog_name: String?) {
        if (dialog_name.equals("no_porter_found")){
            DIAL_NUMBER(CONSTANTS.Booking_Team)
        }
    }

    private fun DIAL_NUMBER(number: String) {
        try {
            val uri = "tel:$number"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(uri)
            startActivity(intent)
            activity!!.finish()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun clickNegativeDialogButton(dialog_name: String?) {
        if (dialog_name.equals("no_porter_found")){
            startActivity(Intent(context, MainScreenActivity::class.java))
            activity!!.finish()
        }
    }

    private fun contact_office() {

    }
}