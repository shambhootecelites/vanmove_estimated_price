package com.vanmove.passesger.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.vanmove.passesger.model.APIModel.Status

data class AllOnlineDrivers (

	@SerializedName("status")
	@Expose
	val status : Status,
	@SerializedName("drivers")
	@Expose
	val drivers : List<Drivers>
)