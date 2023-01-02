package com.vanmove.passesger.utils

import com.google.android.gms.maps.model.LatLng
import com.vanmove.passesger.interfaces.DirectionFinderListener
import com.vanmove.passesger.model.Distance
import com.vanmove.passesger.model.Duration
import com.vanmove.passesger.model.Route
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DirectionFinder(
    private val listener: DirectionFinderListener,
    pick_up_location: LatLng,
    destination_location: LatLng,
    private val operatioNames: String
) {
    private val postParam =
        HashMap<String?, Any?>()

    fun showDirection() {
        listener.onDirectionFinderStart(operatioNames)
        get_directions()
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng) {
        postParam["pickup_latitude"] = origin.latitude
        postParam["pickup_longitude"] = origin.longitude
        postParam["destnation_latitude"] = dest.latitude
        postParam["destnation_longitude"] = dest.longitude
    }

    private fun get_directions() {
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.get_directions(body)!!
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.body() != null) {
                        try {
                            val jsonStatus = JSONObject(response.body()!!.string())
                            parseJSon(jsonStatus)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                }
            })
    }

    @Throws(JSONException::class)
    private fun parseJSon(jsonData: JSONObject?) {
        if (jsonData == null) return
        val routes: MutableList<Route?> =
            ArrayList()
        val jsonRoutes = jsonData.getJSONArray("routes")
        for (i in 0 until jsonRoutes.length()) {
            val jsonRoute = jsonRoutes.getJSONObject(i)
            val route = Route()
            val overview_polylineJson = jsonRoute.getJSONObject("overview_polyline")
            val jsonLegs = jsonRoute.getJSONArray("legs")
            val jsonLeg = jsonLegs.getJSONObject(0)
            val jsonDistance = jsonLeg.getJSONObject("distance")
            val jsonDuration = jsonLeg.getJSONObject("duration")
            val jsonEndLocation = jsonLeg.getJSONObject("end_location")
            val jsonStartLocation = jsonLeg.getJSONObject("start_location")
            route.distance = Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"))
            route.duration = Duration(
                jsonDuration.getString("text"),
                jsonDuration.getInt("value")
            )
            route.endAddress = jsonLeg.getString("end_address")
            route.startAddress = jsonLeg.getString("start_address")
            route.startLocation =
                LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"))
            route.endLocation =
                LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"))
            route.points = decodePolyLine(overview_polylineJson.getString("points"))
            routes.add(route)
        }
        listener.onDirectionFinderSuccess(routes, operatioNames)
    }

    private fun decodePolyLine(poly: String): List<LatLng> {
        val len = poly.length
        var index = 0
        val decoded: MutableList<LatLng> = ArrayList()
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            decoded.add(
                LatLng(
                    lat / 100000.0, lng / 100000.0
                )
            )
        }
        return decoded
    }

    init {
        getDirectionsUrl(pick_up_location, destination_location)
    }
}