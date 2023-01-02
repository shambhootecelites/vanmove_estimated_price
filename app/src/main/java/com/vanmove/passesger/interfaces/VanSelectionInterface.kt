package com.vanmove.passesger.interfaces

import com.vanmove.passesger.adapters.Holder
import com.vanmove.passesger.model.GetVehicles


interface VanSelectionInterface {
    fun onVanImageClicking(
        vehicle_class_id: String?,
        vehicles: GetVehicles?,
        holder: Holder?, position: Int
    )
}