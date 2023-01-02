package com.vanmove.passesger.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.CreditCardScreen
import com.vanmove.passesger.universal.MyRequestQueue
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.payment_methods_fragment.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PaymentMethodFragment : AppCompatActivity(), View.OnClickListener {

    var curent_type = ""
    private var passenger_id: String? = null
    private var RegistrationID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_methods_fragment)
        linkViews()
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, this)
        passenger_id =
            getPreferences(CONSTANTS.passenger_id, this)

        iv_back.setOnClickListener {
            finish()
        }
    }

    private fun linkViews() {

        rel_cash.setOnClickListener(this)
        rel_credit.setOnClickListener(this)
        add_new_card!!.setOnClickListener(this)
        btn_continue!!.setOnClickListener(this)
        try {
            val pay_by =
                getPreferences(CONSTANTS.PAY_BY, this)
            if (pay_by.equals(CONSTANTS.CASH, ignoreCase = true)) {
                tv_cash!!.setTextColor(Color.WHITE)
                tv_credit!!.setTextColor(Color.BLACK)
                rel_credit.setBackgroundColor(Color.parseColor("#ffffff"))
                rel_cash.setBackgroundColor(Color.parseColor("#000000"))
                curent_type = CONSTANTS.CASH
            } else if (pay_by.equals(CONSTANTS.CARD, ignoreCase = true)) {
                savePreferences(
                    CONSTANTS.PAY_BY,
                    CONSTANTS.CARD,
                    this
                )
                tv_cash!!.setTextColor(Color.BLACK)
                tv_credit!!.setTextColor(Color.WHITE)
                rel_credit.setBackgroundColor(Color.parseColor("#000000"))
                rel_cash.setBackgroundColor(Color.parseColor("#ffffff"))
                curent_type = CONSTANTS.CARD
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rel_cash -> {
                tv_cash!!.setTextColor(Color.WHITE)
                tv_credit!!.setTextColor(Color.BLACK)
                rel_cash!!.setBackgroundColor(Color.parseColor("#000000"))
                rel_credit!!.setBackgroundColor(Color.parseColor("#ffffff"))
                curent_type = CONSTANTS.CASH
            }
            R.id.rel_credit -> {
                tv_cash!!.setTextColor(Color.BLACK)
                tv_credit!!.setTextColor(Color.WHITE)
                rel_cash!!.setBackgroundColor(Color.parseColor("#ffffff"))
                rel_credit!!.setBackgroundColor(Color.parseColor("#000000"))
                curent_type = CONSTANTS.CARD
            }
            R.id.add_new_card -> startActivity(Intent(this, CreditCardScreen::class.java))
            R.id.btn_continue -> if (curent_type == CONSTANTS.CARD) {
                val stripe_customer_id =
                    getPreferences(
                        CONSTANTS.stripe_customer_id,
                        this
                    )
                if (stripe_customer_id!!.isEmpty()) {
                    startActivity(Intent(this, CreditCardScreen::class.java))
                } else {
                    update_passenger_payment_method(CONSTANTS.CARD)
                }
            } else if (curent_type == CONSTANTS.CASH) {
                update_passenger_payment_method(CONSTANTS.CASH)
            }

        }
    }

    private fun update_passenger_payment_method(payment_method: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait")
        progressDialog.setTitle("Wait...")
        progressDialog.show()
        val url = Utils.update_passenger_payment_method
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(this)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["payment_method"] = payment_method
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    progressDialog.dismiss()
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            savePreferences(
                                CONSTANTS.PAY_BY,
                                payment_method,
                                this!!
                            )
                            Toast.makeText(
                                this,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                            progressDialog.dismiss()
                        } else {
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                jsonStatus.getString("message"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        progressDialog.dismiss()
                    }
                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    Toast.makeText(this, "Error$error", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
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
        MyRequestQueue.getRequestInstance(this)!!.addRequest(jsonObjReq)
    }
}