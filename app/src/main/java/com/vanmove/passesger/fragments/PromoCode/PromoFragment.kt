package com.vanmove.passesger.fragments.PromoCode

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vanmove.passesger.BuildConfig
import com.vanmove.passesger.R
import com.vanmove.passesger.fragments.HomeFrragment
import com.vanmove.passesger.interfaces.OnItemClickRecycler
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.CONSTANTS.headers
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_apply_promo_code.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PromoFragment : Fragment(R.layout.promo), OnItemClickRecycler {

    var respone: PromoCodeRespone? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        promo_codes()

    }

    override fun onResume() {
        super.onResume()
        goBackMethod()

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
                                    context!!,
                                    respone!!.promo_codes!!, this@PromoFragment
                                )
                            } else {
                                no_item!!.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(
                                context,
                                response.body()!!.status!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(context, response.raw().message, Toast.LENGTH_SHORT).show()
                    }
                    progress_bar!!.visibility = View.GONE
                }

                override fun onFailure(
                    call: Call<PromoCodeRespone?>,
                    e: Throwable
                ) {
                    Utils.showToast(e.message)
                    progress_bar!!.visibility = View.GONE
                }
            })
    }


    private fun goBackMethod() {
        view!!.isFocusableInTouchMode = true
        view!!.requestFocus()
        view!!.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container_fragments, HomeFrragment())
                transaction.commit()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onClickRecycler(view: View?, position: Int) {

        val msg =
            "You can now use the '${respone!!.promo_codes!![position].code}' code to avail " +
                    "${respone!!.promo_codes!![position].percentage}" +
                    "% discount on your bookings only on Vanmove\n" +
                    "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        ShareCode(msg)

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