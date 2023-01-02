package com.vanmove.passesger.services

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import android.util.Log
import com.vanmove.passesger.R
import com.vanmove.passesger.utils.CONSTANTS
import java.io.IOException
import java.util.*

class AppFetchAddressIntentService : IntentService("AppFetchAddressIntentService") {
    protected var mReceiver: ResultReceiver? = null
    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        mReceiver = intent.getParcelableExtra(CONSTANTS.RECEIVER)
        var errorMessage = ""

        // Get the location passed to this service through an extra.
        val location =
            intent.getParcelableExtra<Location>(
                CONSTANTS.LOCATION_DATA_EXTRA
            )
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(
                location!!.latitude,
                location!!.longitude,
                1
            )
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available)
            Log.d("check_exception", errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(
                "check Errot", errorMessage + ". " +
                        "Latitude = " + location!!.latitude +
                        ", Longitude = " +
                        location!!.longitude, illegalArgumentException
            )
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found)
                Log.e("check Errot", errorMessage)
            }
            deliverResultToReceiver(CONSTANTS.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            val addressFragments =
                ArrayList<String?>()

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (i in 0..address.maxAddressLineIndex) {
                addressFragments.add(address.getAddressLine(i))
            }
            Log.i("check Errot", getString(R.string.address_found))
            deliverResultToReceiver(
                CONSTANTS.SUCCESS_RESULT,
                TextUtils.join(
                    System.getProperty("line.separator")!!,
                    addressFragments
                )
            )
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString(CONSTANTS.RESULT_DATA_KEY, message)
        mReceiver!!.send(resultCode, bundle)
    }
}