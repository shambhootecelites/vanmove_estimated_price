package com.vanmove.passesger.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kaopiz.kprogresshud.KProgressHUD
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog2
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.DialogUtils
import com.vanmove.passesger.utils.ShowAlertMessage.showAlertMessageTwoButtons
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.fragment_credit_card_screen.*
import kotlinx.android.synthetic.main.titlebar.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class Month(var name: String, var number: String)


class CreditCardScreen : AppCompatActivity(), OnClickTwoButtonsAlertDialog2 {
    var context: Context? = null

    var passenger_id: String? = null
    var RegistrationID: String? = null
    private var progressDialog: KProgressHUD? = null

    var selected_month = ""
    val Month = arrayOf(
        Month("January", "01"),
        Month("February", "02"),
        Month("March", "03"),
        Month("April", "04"),
        Month("May", "05"),
        Month("June", "06"),
        Month("July", "07"),
        Month("August", "08"),
        Month("September", "09"),
        Month("October", "10"),
        Month("November", "11"),
        Month("December", "12")

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_credit_card_screen)
        context = this@CreditCardScreen
        progressDialog = DialogUtils.showProgressDialog(this, cancelable = false)

        iv_back.setOnClickListener {
            finish()
        }

        passenger_id =
            getPreferences(CONSTANTS.passenger_id, context!!)
        RegistrationID =
            getPreferences(CONSTANTS.RegistrationID, context!!)
        buy.setOnClickListener {


            val card_str = card_number!!.text.toString()
            val year_str = year!!.text.toString()
            val cvv_str = cvv!!.text.toString()
            val email =
                getPreferences(CONSTANTS.username, context!!)
            if (card_str.isEmpty()) {
                Toast.makeText(context, "Enter card number.", Toast.LENGTH_LONG).show()
            } else if (card_str.length < 10) {
                Toast.makeText(context, "Card Number is Invalid", Toast.LENGTH_LONG).show()
            } else if (cvv_str.isEmpty()) {
                Toast.makeText(context, "Enter cvc.", Toast.LENGTH_LONG).show()
            } else if (selected_month.isEmpty()) {
                Toast.makeText(context, "Select Expire month.", Toast.LENGTH_LONG).show()
            } else if (year_str.isEmpty()) {
                Toast.makeText(context, "Enter year.", Toast.LENGTH_LONG).show()
            } else {
                stripe_customer(card_str, selected_month, year_str, cvv_str, email)
            }
        }



        month.setOnClickListener {
            month_popmenu(it)
        }
        year.setOnClickListener {
            year_popmenu(it)
        }


    }

    private fun month_popmenu(view: View) {
        val popup = PopupMenu(context, view)
        for (title in Month) {
            popup.menu.add(title.name)
        }
        popup.setOnMenuItemClickListener { item ->
            month!!.text = item.title.toString()

            for (title in Month) {
                if (title.name.equals(item.title.toString())) {
                    selected_month = title.number
                }
            }

            true
        }
        popup.show()
    }


    private fun year_popmenu(view: View) {
        val popup = PopupMenu(context, view)
        popup.menu.add("2020")
        popup.menu.add("2021")
        popup.menu.add("2022")
        popup.menu.add("2023")
        popup.menu.add("2024")
        popup.menu.add("2025")
        popup.menu.add("2026")
        popup.menu.add("2027")
        popup.menu.add("2028")
        popup.menu.add("2029")
        popup.menu.add("2030")
        popup.setOnMenuItemClickListener { item ->
            year!!.text = item.title.toString()
            true
        }
        popup.show()
    }

    private fun stripe_customer(
        number: String,
        exp_month: String,
        exp_year: String,
        cvc: String,
        email: String?
    ) {

        progressDialog!!.show()

        val url = Utils.stripe_customer
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(context)
        }
        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["number"] = number
        postParam["exp_month"] = exp_month
        postParam["exp_year"] = exp_year
        postParam["cvc"] = cvc
        postParam["email"] = email
        postParam["user_id"] = passenger_id
        val jsonObjReq: JsonObjectRequest =
            object : JsonObjectRequest(
                Method.POST,
                url, JSONObject(postParam as Map<*, *>),
                Response.Listener { jsonObject ->
                    Log.d("TAG", jsonObject.toString())
                    try {
                        val jsonStatus = jsonObject.getJSONObject("status")
                        if (jsonStatus.getString("code").equals("1000", ignoreCase = true)) {
                            val stripe_customer_id = jsonObject.getString("data")
                            savePreferences(
                                CONSTANTS.stripe_customer_id,
                                stripe_customer_id,
                                context!!
                            )
                            savePreferences(
                                CONSTANTS.PAY_BY,
                                CONSTANTS.CARD,
                                context!!
                            )
                            val last_four_number =
                                number.substring(number.length - 4)
                            savePreferences(
                                CONSTANTS.card_number,
                                last_four_number,
                                context!!
                            )
                            showAlertMessageTwoButtons(
                                context,
                                "Card Add",
                                "Card information is added successfully. ",
                                "Continue",
                                "Cancel",
                                "Continue_Card",
                                this@CreditCardScreen
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
                    progressDialog!!.dismiss()

                }
                , Response.ErrorListener { error ->
                    VolleyLog.d("TAG", "Error: " + error.message)
                    progressDialog!!.dismiss()

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
        Volley.newRequestQueue(context).add(jsonObjReq)
    }

    override fun clickPositiveDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "Continue_Card") {
            dialogInterface!!.dismiss()
            finish()
        }
    }

    override fun clickNegativeDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
        if (dialog_name == "Continue_Card") {
            dialogInterface!!.dismiss()
        }
    }

    override fun clickNeutralButtonDialogButton(
        dialog_name: String?,
        dialogInterface: DialogInterface?
    ) {
    }

}