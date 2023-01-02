package com.vanmove.passesger.fragments.PorterHistory

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.Current_Porters_Adapter
import com.vanmove.passesger.fragments.PorterNavigation.PorterNavigationActivty
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.model.APIModel.PorterHistory
import com.vanmove.passesger.model.APIModel.PorterHistoryModel
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Dial_Number
import com.vanmove.passesger.utils.Utils.Show_Sms_intent
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.gone
import com.vanmove.passesger.utils.Utils.updateTLS
import com.vanmove.passesger.utils.Utils.visible
import kotlinx.android.synthetic.main.current_poters_fragment.view.*
import kotlinx.android.synthetic.main.custom_dialog_call_sms_options.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class Current_Porters_Fragment : Fragment(R.layout.current_poters_fragment),
    OnItemClickRecycler, View.OnClickListener {
    private var arrayList_current_Porters: ArrayList<PorterHistoryModel> = ArrayList()
    private var registerationId: String? = null
    private var passenger_id: String? = null
    var mobile: String? = ""
    var alertDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerationId =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        currentPorters
    }

    private val currentPorters: Unit
        get() {
            view!!.progress_bar.visible()
            val postParam: MutableMap<String?, String?> =
                HashMap()
            postParam["user_type"] = "passenger"
            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(postParam as Map<*, *>).toString()
            )
            val headers: MutableMap<String?, String?> =
                HashMap()
            headers["Content-Type"] = "application/json; charset=utf-8"
            headers["registration_id"] = registerationId
            headers["user_id"] = passenger_id
            CONSTANTS.mApiService.current_porter_requests(body, headers)!!
                .enqueue(object : Callback<PorterHistory?> {
                    override fun onResponse(
                        call: Call<PorterHistory?>,
                        response: Response<PorterHistory?>
                    ) {
                        if (response.body() != null) {
                            if ( response.body()?.status?.code == "1000") {
                                arrayList_current_Porters =
                                    response.body()?.current_porter_jobs as ArrayList<PorterHistoryModel>
                                if (arrayList_current_Porters.size == 0) {
                                    view!!.tv_is_data_available!!.visibility = View.VISIBLE
                                } else {
                                    view!!.tv_is_data_available!!.visibility = View.GONE
                                }
                                view!!.lv_current_porters!!.adapter = Current_Porters_Adapter(
                                    context!!,
                                    arrayList_current_Porters,
                                    this@Current_Porters_Fragment
                                )
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
                        view!!.progress_bar.gone()
                    }

                    override fun onFailure(
                        call: Call<PorterHistory?>,
                        e: Throwable
                    ) {
                        Utils.showToast(e.message)
                        view!!.progress_bar.gone()
                    }
                })
        }

    private fun cancelCurrentPorters(
        request_id: String?, passenger_id: String?,
        registerationId: String?, porter_id: String?, position: Int
    ) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading.....")
        progressDialog.show()
        progressDialog.setCancelable(true)
        progressDialog.setCanceledOnTouchOutside(true)
        val url = Utils.cancel_porter_request
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["request_id"] = request_id
        postParam["status"] = "CANCELLED"
        postParam["user_type"] = "passenger"
        postParam["porter_id"] = porter_id
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                com.android.volley.Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            arrayList_current_Porters.removeAt(position)
                            view!!.lv_current_porters!!.adapter!!.notifyDataSetChanged()
                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                    }
                    progressDialog.dismiss()
                }
                , com.android.volley.Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    progressDialog.dismiss()
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers =
                        HashMap<String, String>()
                    headers["Content-Type"] = "application/json; charset=utf-8"
                    headers["registration_id"] = registerationId!!
                    headers["user_id"] = passenger_id!!
                    return headers
                }
            }
        MyRequestQueue.getRequestInstance(context)!!.addRequest(jsonObjReq)
    }

    override fun onClickRecycler(view: View?, position: Int) {
        val id = view!!.id
        if (id == R.id.iv_btn_call_porter) {
            mobile = arrayList_current_Porters[position].mobile
            showCallSMSSelectionOptions()
        } else if (id == R.id.btn_cancel_porter_request) {
            val porter = arrayList_current_Porters[position]
            val porter_id = porter.porterId
            val request_user_id = porter.requestId
            cancelCurrentPorters(
                request_user_id,
                passenger_id,
                registerationId,
                porter_id,
                position
            )
        } else if (id == R.id.btn_track_porter) {

            val porter = arrayList_current_Porters[position]
            val porter_id = porter.porterId
            startActivity(Intent(activity, PorterNavigationActivty::class.java).apply {
                putExtra("porter_id", porter_id)
                putExtra("pic", porter.picture)
                putExtra("email", porter.email)
                putExtra("full_name", porter.firstName + " " + porter.last_name)
                putExtra("mobile", porter.mobile)
                putExtra("porter_request_id", porter.requestId)
            })


        }
    }

    private fun showCallSMSSelectionOptions() {
        val builder =
            AlertDialog.Builder(activity!!)
        builder.setView(R.layout.custom_dialog_call_sms_options)
        alertDialog = builder.create()
        alertDialog!!.show()

        alertDialog!!.rl_dialog_call!!.setOnClickListener(this)
        alertDialog!!.rl_dialog_sms!!.setOnClickListener(this)
        alertDialog!!.close_dialog!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_dialog_call -> {
                alertDialog!!.dismiss()
                Dial_Number(context!!, mobile!!)
            }
            R.id.rl_dialog_sms -> {
                alertDialog!!.dismiss()
                Show_Sms_intent(mobile!!, context!!)
            }
            R.id.close_dialog -> alertDialog!!.dismiss()
        }
    }
}