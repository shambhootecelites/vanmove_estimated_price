package com.vanmove.passesger.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vanmove.passesger.R
import com.vanmove.passesger.interfaces.DeleteCheck
import com.vanmove.passesger.interfaces.DimensionSubmit
import com.vanmove.passesger.model.DeminsionModel
import kotlinx.android.synthetic.main.dimension_item.view.*

class DemensionAdaptor(
    var context: Context, var deminsionList: List<DeminsionModel>,
    var dimensionSubmit: DimensionSubmit,
    var deleteCheck: DeleteCheck
) : RecyclerView.Adapter<Holder>() {
    var inflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.dimension_item, null)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        holder.view!!.run {
            deminsionList[position].run {
                val new_position = position + 1
                item_name.text = "Item $new_position"
                dimensionSubmit.NameSubmit(position, "Item $new_position")
                if (!weight.isEmpty()) {
                    item_weight.setText(weight)
                }
                if (!lenght.isEmpty()) {
                    item_Lenght.setText(lenght)
                }
                if (!width.isEmpty()) {
                    item_Width.setText(width)
                }
                if (!height.isEmpty()) {
                    item_Height.setText(height)
                }
                if (!quantity.isEmpty()) {
                    item_Quantity.setText(quantity)
                }
                item_weight.setText(weight)
                item_weight.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        dimensionSubmit.WeightSubmit(position, s.toString())
                    }
                })
                item_Lenght.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        dimensionSubmit.LenghtSubmit(position, s.toString())
                    }
                })
                item_Width.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        dimensionSubmit.WidthSubmit(position, s.toString())
                    }
                })
                item_Height.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        dimensionSubmit.HeightSubmit(position, s.toString())
                    }
                })
                item_Quantity.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: Editable) {
                        dimensionSubmit.QuantitySubmit(position, s.toString())
                    }
                })
                delete_check.setOnCheckedChangeListener { compoundButton, b ->
                    deleteCheck.OnDeleteCheck(
                        b,
                        position
                    )
                }

            }

        }

    }

    override fun getItemCount(): Int {
        return deminsionList.size
    }


}