package com.vanmove.passesger.interfaces

import com.vanmove.passesger.activities.ApplyPromoCode.PromoCodeValidRespone
import com.vanmove.passesger.fragments.PromoCode.PromoCodeRespone
import com.vanmove.passesger.model.APIModel.*
import com.vanmove.passesger.model.PorterDetailHistory
import com.vanmove.passesger.utils.Utils.porter_get_booking_detail
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import java.util.*

interface BaseApiService {
    @Headers("Content-Type:application/json")
    @POST("move_edit.php")
    fun move_edit(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<GenenalModel?>?

    @Headers("Content-Type:application/json")
    @POST("get_directions.php")
    fun get_directions(@Body body: RequestBody?): Call<ResponseBody?>?


    @POST("new_offer_request.php")
    fun new_offer_request(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<BookingDetailRespone?>?

    @POST("get_passenger_upcomimg_moves.php")
    fun get_passenger_upcomimg_moves(@HeaderMap headers: HashMap<String, String>): Call<UpcomingBookingsListModel?>?

    @POST("get_passenger_upcomimg_offer.php")
    fun get_passenger_upcomimg_offer(@HeaderMap headers: HashMap<String, String>): Call<UpcomingBookingsListModel?>?

    @POST("update_request_inventory_passenger.php")
    fun update_request_inventory_passenger(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<ResponseBody?>?

    @POST("current_porter_requests.php")
    fun current_porter_requests(
        @Body body: RequestBody?,
        @HeaderMap headers: Map<String?, String?>?
    ): Call<PorterHistory?>?

    @POST(porter_get_booking_detail)
    fun currentPorterDetail(
        @Body body: RequestBody?,
        @HeaderMap headers: Map<String?, String?>?
    ): Call<PorterDetailHistory?>?

    @POST("get_booking_inventory_detail_passesger.php")
    fun read_inventory(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<InventoryModel?>?

    @POST("dimensions.php")
    fun dimensions(@Body body: RequestBody?): Call<ResponseBody?>?

    @POST("promo_codes.php")
    fun promo_codes(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<PromoCodeRespone?>?

    @POST("promo_code_check.php")
    fun promo_code_check(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<PromoCodeValidRespone?>?

    @POST("dimensions.php")
    fun read_dimensions(@Body body: RequestBody?): Call<DimensionsModel?>?

    @POST("porter_new_booking_request.php")
    fun porter_new_booking_request(
        @Body body: RequestBody?,
        @HeaderMap headers: Map<String?, String?>?
    ): Call<ResponseBody?>?

    @POST("get_booking_detail.php")
    fun get_booking_detail(
        @Body body: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<BookingDetailRespone?>?
}