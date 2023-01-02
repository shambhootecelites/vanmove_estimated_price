package com.vanmove.passesger.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.vanmove.passesger.R
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.Open_Email_Intent
import kotlinx.android.synthetic.main.titlebar.*

class ContactFragment : AppCompatActivity() {
    private var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_us_fragment)
        context = this@ContactFragment


        iv_back.setOnClickListener{
            finish()
        }
    }


    fun help_centre(view: View?) {
        startActivity(Intent(context, WebView::class.java))

    }

    fun contact_Number(view: View?) {
        startActivity(Intent(context, Contact_Number::class.java))
    }

    fun email_support(view: View?) {
        val email_list = arrayOf<String?>(CONSTANTS.Support_Email)
        Open_Email_Intent(context!!, email_list)
    }

    fun liveChat(view: View?){
        val intent = Intent(this@ContactFragment, ChatWindowActivity::class.java)
        val config = chatWindowConfiguration
        intent.putExtras(config.asBundle())
        startActivity(intent)
    }

    val chatWindowConfiguration: ChatWindowConfiguration
        get() {
            val email =
                Utils.getPreferences(CONSTANTS.username, this)
            val first_name =
                Utils.getPreferences(CONSTANTS.first_name, this)
            val last_name =
                Utils.getPreferences(CONSTANTS.last_name, this)
            val full_name = "$first_name $last_name"
            return ChatWindowConfiguration(
                CONSTANTS.KEY_LICENCE_NUMBER, null, full_name, email,
                null
            )
        }

}