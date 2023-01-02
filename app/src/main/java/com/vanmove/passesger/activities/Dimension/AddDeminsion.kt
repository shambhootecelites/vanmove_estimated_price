package com.vanmove.passesger.activities.Dimension

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vanmove.passesger.R
import com.vanmove.passesger.adapters.DemensionAdaptor
import com.vanmove.passesger.interfaces.DeleteCheck
import com.vanmove.passesger.interfaces.DimensionSubmit
import com.vanmove.passesger.model.DeminsionModel
import com.vanmove.passesger.utils.Utils
import kotlinx.android.synthetic.main.activity_deminsion.*
import kotlinx.android.synthetic.main.titlebar.*

class AddDeminsion : AppCompatActivity(), View.OnClickListener,
    DimensionSubmit, DeleteCheck {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deminsion)
        linkViews()
        dimension_list!!.adapter = DemensionAdaptor(
            this@AddDeminsion,
            Utils.deminsionList,
            this@AddDeminsion,
            this@AddDeminsion
        )


        iv_back.setOnClickListener {
            finish()
        }
    }

    private fun linkViews() {

        Another!!.setOnClickListener(this)
        Copy!!.setOnClickListener(this)
        Delete!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.Delete) {
            // Make Tempory List
            val tempory_list =
                Utils.deminsionList
            for (i in tempory_list.indices) {
                if (tempory_list[i].isIs_delete) {
                    Utils.deminsionList.removeAt(i)
                }
            }
            dimension_list!!.adapter!!.notifyDataSetChanged()
        } else if (v.id == R.id.Another) {
            val last_position = Utils.deminsionList.size - 1
            if (Utils.deminsionList.get(last_position).name.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Weight of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].lenght.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Lenght of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].width.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Width of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].height.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Height of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].quantity.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Quantity of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Utils.deminsionList.add(
                    DeminsionModel(
                        "", "", "",
                        "", "", ""
                    )
                )
                dimension_list!!.adapter = DemensionAdaptor(
                    this@AddDeminsion,
                    Utils.deminsionList,
                    this@AddDeminsion,
                    this@AddDeminsion
                )
            }
        } else if (v.id == R.id.Copy) {
            val last_position = Utils.deminsionList.size - 1
            if (Utils.deminsionList[last_position].weight.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Weight of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].lenght.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Lenght of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].width.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Width of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].height.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Height of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Utils.deminsionList[last_position].quantity.isEmpty()) {
                Toast.makeText(
                    this,
                    "Enter Quantity of " + Utils.deminsionList[last_position]
                        .name,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Utils.deminsionList.add(
                    DeminsionModel(
                        Utils.deminsionList[last_position].name
                        , Utils.deminsionList[last_position].lenght,
                        Utils.deminsionList[last_position].width,
                        Utils.deminsionList[last_position].height,
                        Utils.deminsionList[last_position].quantity,
                        Utils.deminsionList[last_position].weight
                    )
                )
                dimension_list!!.adapter = DemensionAdaptor(
                    this@AddDeminsion,
                    Utils.deminsionList, this@AddDeminsion,
                    this@AddDeminsion
                )
            }
        }
    }

    override fun WeightSubmit(position: Int, Weight: String?) {
        val old_data = Utils.deminsionList[position]
        old_data.weight = Weight!!
        Utils.deminsionList[position] = old_data
    }

    override fun LenghtSubmit(position: Int, Lenght: String?) {
        val old_data = Utils.deminsionList[position]
        old_data.lenght = Lenght!!
        Utils.deminsionList[position] = old_data
    }

    override fun WidthSubmit(position: Int, Width: String?) {
        val old_data = Utils.deminsionList[position]
        old_data.width = Width!!
        Utils.deminsionList[position] = old_data
    }

    override fun HeightSubmit(position: Int, Height: String?) {
        val old_data = Utils.deminsionList[position]
        old_data.height = Height!!
        Utils.deminsionList[position] = old_data
    }

    override fun QuantitySubmit(position: Int, Quantity: String?) {
        val old_data = Utils.deminsionList[position]
        old_data.quantity = Quantity!!
        Utils.deminsionList[position] = old_data
    }

    override fun NameSubmit(position: Int, Name: String?) {
        val old_data = Utils.deminsionList[position]
        old_data.name = Name!!
        Utils.deminsionList[position] = old_data
    }

    override fun OnDeleteCheck(is_delete: Boolean, position: Int) {
        val deminsionModel =
            Utils.deminsionList[position]
        deminsionModel.setIs_delete(is_delete)
        Utils.deminsionList[position] = deminsionModel
    }
}