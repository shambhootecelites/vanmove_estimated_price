package com.vanmove.passesger.activities.ApplyPromoCode

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vanmove.passesger.BuildConfig
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.PromoCode.PromoAdaptor
import com.vanmove.passesger.fragments.PromoCode.PromoCodeRespone
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.CONSTANTS.headers
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_apply_promo_code.*
import kotlinx.android.synthetic.main.titlebar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ApplyPromoCodeActivity : AppCompatActivity(), OnItemClickRecycler {

    var respone: PromoCodeRespone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_promo_code)

        apply_code.setOnClickListener(View.OnClickListener {
            val promo_code_ = promo_code.getText().toString()
            if (promo_code_.isEmpty()) {
                Toast.makeText(this, "Enter promo code", Toast.LENGTH_SHORT).show()
            } else {
                promo_code_check(promo_code_)
            }
        })

        iv_back.setOnClickListener {
            finish()
        }
        promo_codes()
    }

    private fun promo_code_check(promo_code: String) {
        Horizontal_Progress!!.visibility = View.VISIBLE
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["user_type"] = CONSTANTS.user_type
        postParam["promo_code"] = promo_code
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.promo_code_check(body, headers)!!
            .enqueue(object : Callback<PromoCodeValidRespone?> {
                override fun onResponse(
                    call: Call<PromoCodeValidRespone?>,
                    response: Response<PromoCodeValidRespone?>
                ) {
                    if (response.body() != null) {
                        val respone = response.body()
                        if (respone!!.status!!.code == "1000") {
                            Toast.makeText(
                                this@ApplyPromoCodeActivity,
                                respone.status!!.message,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            val data = Gson().toJson(respone.promo_code)
                            val returnIntent = Intent()
                            returnIntent.putExtra("data", data)
                            returnIntent.putExtra("message", respone.status!!.message)
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()
                        } else {
                            result!!.text = respone.status!!.message
                            result!!.setTextColor(Color.parseColor("#FF0000"))
                        }
                    } else {
                        Toast.makeText(
                            this@ApplyPromoCodeActivity,
                            response.raw().message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Horizontal_Progress!!.visibility = View.GONE
                }

                override fun onFailure(
                    call: Call<PromoCodeValidRespone?>,
                    e: Throwable
                ) {
                    e.printStackTrace()
                    Horizontal_Progress!!.visibility = View.GONE
                    Utils.showToast(e.message)

                }
            })
    }

    private fun promo_codes() {
        progress_bar!!.visibility = View.VISIBLE
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["user_type"] = CONSTANTS.user_type
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.promo_codes(body, headers)!!
            .enqueue(object : Callback<PromoCodeRespone?> {
                override fun onResponse(
                    call: Call<PromoCodeRespone?>,
                    response: Response<PromoCodeRespone?>
                ) {
                    if (response.body() != null) {
                        respone = response.body()
                        if (respone!!.status!!.code == "1000") {
                            if (respone!!.promo_codes!!.size > 0) {
                                notification_rcv!!.adapter = PromoAdaptor(
                                    this@ApplyPromoCodeActivity,
                                    respone!!.promo_codes!!, this@ApplyPromoCodeActivity
                                )
                            } else {
                                no_item!!.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(
                                this@ApplyPromoCodeActivity,
                                response.body()!!.status!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@ApplyPromoCodeActivity,
                            response.raw().message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progress_bar!!.visibility = View.GONE
                }

                override fun onFailure(
                    call: Call<PromoCodeRespone?>,
                    e: Throwable
                ) {
                    e.printStackTrace()
                    progress_bar!!.visibility = View.GONE
                    Utils.showToast(e.message)

                }
            })
    }

    override fun onClickRecycler(view: View?, position: Int) {

        val id = view!!.getId()
        if (id == R.id.share_code) {

            val msg =
                "You can now use the '${respone!!.promo_codes!![position].code}' code to avail " +
                        "${respone!!.promo_codes!![position].percentage}" +
                        "% discount on your bookings only on Vanmove\n" +
                        "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            ShareCode(msg)
        } else {
            promo_code!!.setText(respone!!.promo_codes!![position].code)

        }


    }

    fun ShareCode(msg: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, msg)
            startActivity(Intent.createChooser(shareIntent, "Share Application"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
