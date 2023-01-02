package com.vanmove.passesger.activities

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId
import com.livechatinc.inappchat.ChatWindowActivity
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.vanmove.passesger.R
import com.vanmove.passesger.activities.AdvancedPayment.AdvancedPaymentActivity
import com.vanmove.passesger.adapters.NavDrawerAdapter
import com.vanmove.passesger.fragments.HomeFrragment
import com.vanmove.passesger.fragments.MoveHistory.BookedMovesMain
import com.vanmove.passesger.fragments.MoveNavigationScreen.ConnectingFragment
import com.vanmove.passesger.fragments.OfferHistory.BookedOffersMain
import com.vanmove.passesger.fragments.PaymentMethodFragment
import com.vanmove.passesger.fragments.PorterHistory.PorterHistory_Tab_Fragment
import com.vanmove.passesger.fragments.PromoCode.PromoFragment
import com.vanmove.passesger.interfaces.OnClickTwoButtonsAlertDialog
import com.vanmove.passesger.model.NavDrawerItem
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.CONSTANTS.move_to_move_upcoming
import com.vanmove.passesger.utils.Utils
import com.vanmove.passesger.utils.Utils.getPreferences
import com.vanmove.passesger.utils.Utils.savePreferences
import com.vanmove.passesger.utils.Utils.updateTLS
import kotlinx.android.synthetic.main.activity_main_screen.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainScreenActivity : AppCompatActivity(), OnItemClickListener, OnClickTwoButtonsAlertDialog {
    private var drawer_lists: ArrayList<NavDrawerItem>? = null
    private var toggle: ActionBarDrawerToggle? = null
    private var RegistrationID: String? = null
    private var passenger_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.app_bar_main)
        RegistrationID = getPreferences(CONSTANTS.RegistrationID, this)
        passenger_id = getPreferences(CONSTANTS.passenger_id, this)
        CONSTANTS.headers.clear()
        CONSTANTS.headers["Content-Type"] = "application/json; charset=utf-8"
        CONSTANTS.headers["passenger_id"] = passenger_id!!
        CONSTANTS.headers["registration_id"] = RegistrationID!!
        CONSTANTS.headers["user_id"] = passenger_id!!

        val toolbar =
            findViewById<Toolbar>(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        linkViews()

        lv_drawer_items!!.adapter = NavDrawerAdapter(drawerItems, this)
        lv_drawer_items!!.onItemClickListener = this
        toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout!!.addDrawerListener(toggle!!)
        toggle!!.syncState()
        if (intent.hasExtra(CONSTANTS.move_to_offer_upcoming)) {
            val transaction = supportFragmentManager.beginTransaction()
            val fragment: Fragment
            fragment = BookedOffersMain()
            val bundle = Bundle()
            bundle.putString(CONSTANTS.move_to_offer_upcoming, CONSTANTS.move_to_offer_upcoming)
            fragment.setArguments(bundle)
            transaction.replace(R.id.container_fragments, fragment)
            transaction.commit()
        } else if (intent.hasExtra(CONSTANTS.move_to_connecting_screen)) {
            ChangeFragmence(ConnectingFragment())
        } else if (intent.hasExtra(move_to_move_upcoming)) {
            MoveUpcoming()
        } else if (intent.hasExtra(CONSTANTS.INTENT_EXTRA_IS_PUSH_NOTIFICATION_AVAILABLE)) {
            val notification_name =
                intent!!.getStringExtra(CONSTANTS.INTENT_EXTRA_NOTIFICATION_NAME)
            if (notification_name == CONSTANTS.Offer_accepted) {
                startActivity(Intent(this, AdvancedPaymentActivity::class.java).apply {
                    putExtra("request_id", intent!!.getStringExtra(CONSTANTS.offer_request_id))
                    putExtra(CONSTANTS.from_home, CONSTANTS.from_home)
                })

            } else {
                Home()
            }
        } else {
            Home()

        }

    }

    private fun Home() {
        val transaction = supportFragmentManager.beginTransaction()
        val home: Fragment = HomeFrragment()
        transaction.replace(R.id.container_fragments, home)
        transaction.commit()
    }

    private fun MoveUpcoming() {
        val transaction =
            supportFragmentManager.beginTransaction()
        val fragment = BookedMovesMain()
        val bundle = Bundle()
        bundle.putString(move_to_move_upcoming, move_to_move_upcoming)
        fragment.arguments = bundle
        transaction.replace(R.id.container_fragments, fragment)
        transaction.commit()
    }

    private fun ChangeFragmence(fragment: Fragment) {
        val transaction =
            supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_fragments, fragment)
        transaction.commit()
    }

    private fun linkViews() {
        chat!!.setOnClickListener({
            val intent = Intent(this@MainScreenActivity, ChatWindowActivity::class.java)
            val config = chatWindowConfiguration
            intent.putExtras(config.asBundle())
            startActivity(intent)
        })
    }

    val chatWindowConfiguration: ChatWindowConfiguration
        get() {
            val email =
                getPreferences(CONSTANTS.username, this)
            val first_name =
                getPreferences(CONSTANTS.first_name, this)
            val last_name =
                getPreferences(CONSTANTS.last_name, this)
            val full_name = "$first_name $last_name"
            return ChatWindowConfiguration(
                CONSTANTS.KEY_LICENCE_NUMBER, null, full_name, email,
                null
            )
        }

    val drawerItems: ArrayList<NavDrawerItem>
        get() {
            drawer_lists = ArrayList()
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_book_move, "Book a new move"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_moves, "My Moves"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_offers, "My Offers"))
            // drawer_lists.add(new NavDrawerItem(R.drawable.ic_call_porter_, "Request a Porter"));
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_helper_history, "Porters History"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_nav_profile, "Profile"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_payment, "Payment"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_promotions, "Promotions"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_contact, "Contact"))
            drawer_lists!!.add(NavDrawerItem(R.drawable.ic_logout, "Logout"))
            return drawer_lists as ArrayList<NavDrawerItem>
        }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        showFragmentScreens(drawer_lists!![position].title_name, "")
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
    }

    private fun showFragmentScreens(title: String, bundle: String) {
        val transaction = supportFragmentManager.beginTransaction()
        when (title) {
            "Book a new move" -> {
                savePreferences(
                    CONSTANTS.PREFERENCE_DESTINATION_LOCATION_NAME_EXTRA,
                    "",
                    this
                )
                savePreferences(
                    CONSTANTS.PREFERENCE_PICK_UP_LOCATION_NAME_EXTRA,
                    "",
                    this
                )
                startActivity(Intent(this, MainScreenActivity::class.java))
                finish()

            }
            "My Moves" -> {
                transaction.replace(R.id.container_fragments, BookedMovesMain())
                transaction.commit()
            }
            "My Offers" -> {
                transaction.replace(R.id.container_fragments, BookedOffersMain())
                transaction.commit()
            }
            "Request a Porter" -> startActivity(Intent(this, PorterActivity::class.java))
            "Porters History" -> {
                transaction.replace(R.id.container_fragments, PorterHistory_Tab_Fragment())
                transaction.commit()
            }
            "Profile" -> {
                transaction.replace(R.id.container_fragments, UpdateProfile())
                transaction.commit()
            }
            "Payment" -> startActivity(Intent(this, PaymentMethodFragment::class.java))
            "Promotions" -> {
                transaction.replace(R.id.container_fragments, PromoFragment())
                transaction.commit()
            }
            "Contact" -> startActivity(Intent(this, ContactFragment::class.java))
            "Logout" -> showSignOutDialogue()
        }
    }

    override fun onBackPressed() {
        showDialogBackpress()
    }

    fun showDialogBackpress() {
        val alertDialog =
            AlertDialog.Builder(this@MainScreenActivity)
        alertDialog.setTitle(getString(R.string.app_name))
        alertDialog.setMessage("Are you sure you want to quit ${getString(R.string.app_name)}?")
        alertDialog.setPositiveButton("Yes") { dialog, which -> finishAffinity() }
        alertDialog.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
        alertDialog.show()
    }

    fun showSignOutDialogue() {
        val alertDialog =
            AlertDialog.Builder(this@MainScreenActivity)
        alertDialog.setTitle(getString(R.string.app_name))
        alertDialog.setMessage("Are you sure you want to logout ?")
        alertDialog.setPositiveButton("YES") { dialog, which ->

            signOutDriver(
                passenger_id,
                RegistrationID
            )
        }
        alertDialog.setNegativeButton("NO") { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    override fun onStop() {
        super.onStop()
        savePreferences(
            CONSTANTS.onResume,
            "false",
            this@MainScreenActivity
        )
    }

    override fun onResume() {
        super.onResume()
        Utils.closeKeyboard(this)



        savePreferences(
            CONSTANTS.onResume,
            "true", this@MainScreenActivity


        )

    }

    private fun signOutDriver(
        passenger_id: String?,
        registration_id: String?
    ) {

        // Delete FCM Token
        GlobalScope.async {
            FirebaseInstanceId.getInstance().deleteInstanceId()

        }

        val postParam: MutableMap<String?, String?> =
            HashMap()
        postParam["type"] = Utils.userType
        val progress_dialog = ProgressDialog.show(this, "", "Loading...", true)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            updateTLS(this)
        }
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Utils.Logout_Url,
            JSONObject(postParam as Map<*, *>),
            Response.Listener { jsonObject ->
                Log.d("TAG", jsonObject.toString())
                try {
                    val jsonStatus = jsonObject.getJSONObject("status")
                    if (jsonStatus.getString("code") == "1000") {
                        progress_dialog.dismiss()
                        savePreferences(
                            CONSTANTS.login,
                            "false",
                            this@MainScreenActivity
                        )
                        val intent = Intent(this@MainScreenActivity, Ask::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } else {
                        savePreferences(
                            CONSTANTS.login,
                            "false",
                            this@MainScreenActivity
                        )
                        val intent = Intent(this@MainScreenActivity, Ask::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                        progress_dialog.dismiss()
                        Toast.makeText(
                            this@MainScreenActivity,
                            jsonStatus.getString("message"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: JSONException) {
                    progress_dialog.dismiss()
                }
            }
            , Response.ErrorListener { error ->
                progress_dialog.dismiss()
                VolleyLog.d("hantash_check", "Error: " + error.message)
                Toast.makeText(this@MainScreenActivity, error.message + "", Toast.LENGTH_LONG)
                    .show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers =
                    HashMap<String, String>()
                headers["Content-Type"] = "application/json; charset=utf-8"
                headers["user_id"] = passenger_id!!
                headers["registration_id"] = registration_id!!
                return headers
            }
        }
        Volley.newRequestQueue(this).add(jsonObjReq)
    }


    override fun clickPositiveDialogButton(dialog_name: String?) {

    }

    override fun clickNegativeDialogButton(dialog_name: String?) {

    }
}