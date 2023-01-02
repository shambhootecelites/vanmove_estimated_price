package com.vanmove.passesger.activities.Dimension

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.DemensionAdaptor
import com.vanmove.passesger.interfaces.DeleteCheck
import com.vanmove.passesger.interfaces.DimensionSubmit
import com.vanmove.passesger.model.APIModel.DimensionsModel
import com.vanmove.passesger.model.DeminsionModel
import com.vanmove.passesger.utils.CONSTANTS
import com.vanmove.passesger.utils.ShowProgressDialog.closeDialog
import com.vanmove.passesger.utils.ShowProgressDialog.showDialog2
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_view_deminsion.*
import kotlinx.android.synthetic.main.titlebar.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ViewDeminsion : AppCompatActivity(), View.OnClickListener,
    DimensionSubmit, DeleteCheck {

    var requestid: String? = null
    var deminsionList: ArrayList<DeminsionModel>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_deminsion)
        linkViews()
    }

    private fun linkViews() {

        iv_back.setOnClickListener {
            finish()
        }
        Update_Deminsion.setOnClickListener(this)
        Another.setOnClickListener(this)
        Copy.setOnClickListener(this)
        requestid = intent.getStringExtra("requestid")
        Load_Deminsion()
    }

    private fun Load_Deminsion() {

        showDialog2(this)
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["request_id"] = requestid
        postParam["operation"] = "read_dimensions"
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.read_dimensions(body)!!.enqueue(object : Callback<DimensionsModel?> {
            override fun onResponse(
                call: Call<DimensionsModel?>,
                response: Response<DimensionsModel?>
            ) {
                if (response.body() != null) {
                    if (response.body()!!.status!!.code == "1000") {
                        deminsionList = response.body()!!.dimensions as ArrayList<DeminsionModel>?
                        if (deminsionList!!.size == 0) {
                            deminsionList!!.add(
                                DeminsionModel(
                                    "",
                                    "", "", "", "", ""
                                )
                            )
                        }
                        dimension_list!!.adapter = DemensionAdaptor(
                            this@ViewDeminsion,
                            deminsionList!!, this@ViewDeminsion, this@ViewDeminsion
                        )
                    } else {
                        Toast.makeText(
                            this@ViewDeminsion,
                            response.body()!!.status!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@ViewDeminsion, response.raw().message, Toast.LENGTH_SHORT)
                        .show()
                }
                closeDialog()

            }

            override fun onFailure(
                call: Call<DimensionsModel?>,
                e: Throwable
            ) {
                closeDialog()
                Utils.showToast(e.message)
            }
        })
    }

    override fun onClick(v: View) {
        if (v.id == R.id.iv_back) {
            finish()
        } else if (v.id == R.id.Update_Deminsion) {
            Upload_Deminsion()
        } else if (v.id == R.id.Another) {
            val last_position = deminsionList!!.size - 1
            if (deminsionList!![last_position].weight.isEmpty()) {
                Toast.makeText(
                    this, "Enter Weight of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].lenght.isEmpty()) {
                Toast.makeText(
                    this, "Enter Lenght of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].width.isEmpty()) {
                Toast.makeText(
                    this, "Enter Width of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].height.isEmpty()) {
                Toast.makeText(
                    this, "Enter Height of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].quantity.isEmpty()) {
                Toast.makeText(
                    this, "Enter Quantity of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else {
                deminsionList!!.add(DeminsionModel("", "", "", "", "", ""))
                dimension_list!!.adapter = DemensionAdaptor(
                    this@ViewDeminsion,
                    deminsionList!!, this@ViewDeminsion, this@ViewDeminsion
                )
            }
        } else if (v.id == R.id.Copy) {
            val last_position = deminsionList!!.size - 1
            if (deminsionList!![last_position].weight.isEmpty()) {
                Toast.makeText(
                    this, "Enter Weight of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].lenght.isEmpty()) {
                Toast.makeText(
                    this, "Enter Lenght of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].width.isEmpty()) {
                Toast.makeText(
                    this, "Enter Width of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].height.isEmpty()) {
                Toast.makeText(
                    this, "Enter Height of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else if (deminsionList!![last_position].quantity.isEmpty()) {
                Toast.makeText(
                    this, "Enter Quantity of " + deminsionList!![last_position]
                        .name, Toast.LENGTH_SHORT
                ).show()
            } else {
                deminsionList!!.add(
                    DeminsionModel(
                        deminsionList!![last_position].name
                        , deminsionList!![last_position].lenght,
                        deminsionList!![last_position].width,
                        deminsionList!![last_position].height,
                        deminsionList!![last_position].quantity,
                        deminsionList!![last_position].weight
                    )
                )
                dimension_list!!.adapter = DemensionAdaptor(
                    this@ViewDeminsion,
                    deminsionList!!, this@ViewDeminsion, this@ViewDeminsion
                )
            }
        }
    }

    override fun WeightSubmit(position: Int, Weight: String?) {
        val old_data = deminsionList!![position]
        old_data.weight = Weight!!
        deminsionList!![position] = old_data
    }

    override fun LenghtSubmit(position: Int, Lenght: String?) {
        val old_data = deminsionList!![position]
        old_data.lenght = Lenght!!
        deminsionList!![position] = old_data
    }

    override fun WidthSubmit(position: Int, Width: String?) {
        val old_data = deminsionList!![position]
        old_data.width = Width!!
        deminsionList!![position] = old_data
    }

    override fun HeightSubmit(position: Int, Height: String?) {
        val old_data = deminsionList!![position]
        old_data.height = Height!!
        deminsionList!![position] = old_data
    }

    override fun QuantitySubmit(position: Int, Quantity: String?) {
        val old_data = deminsionList!![position]
        old_data.quantity = Quantity!!
        deminsionList!![position] = old_data

    }

    override fun NameSubmit(position: Int, Name: String?) {
        val old_data = deminsionList!![position]
        old_data.name = Name!!
        deminsionList!![position] = old_data
    }

    private fun Upload_Deminsion() {

        showDialog2(this)
        var jsonArray: JSONArray? = JSONArray()
        val gson = Gson()
        val listString = gson.toJson(
            deminsionList,
            object : TypeToken<List<DeminsionModel?>?>() {}.type
        )
        try {
            jsonArray = JSONArray(listString)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val postParam: MutableMap<String?, Any?> =
            HashMap()
        postParam["request_id"] = requestid
        postParam["operation"] = "add_dimensions"
        postParam["dimensions"] = jsonArray
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            JSONObject(postParam).toString()
        )
        CONSTANTS.mApiService.dimensions(body)!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.body() != null) {
                    Toast.makeText(
                        this@ViewDeminsion,
                        "Update dimensions successful.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(this@ViewDeminsion, response.raw().message, Toast.LENGTH_SHORT)
                        .show()
                }
                closeDialog()
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                e: Throwable
            ) {
                Utils.showToast(e.message)
                closeDialog()
            }
        })
    }

    override fun OnDeleteCheck(is_delete: Boolean, position: Int) {}
}