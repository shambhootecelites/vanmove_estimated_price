package com.vanmove.passesger.model

class DeminsionModel(
    var name: String, var weight: String, var lenght: String,
    var width: String, var height: String, var quantity: String
) {
    var isIs_delete = false
        private set
    fun setIs_delete(is_delete: Boolean) {
        isIs_delete = is_delete
    }

}