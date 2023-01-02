package com.vanmove.passesger.interfaces

import com.vanmove.passesger.model.Route


interface DirectionFinderListener {
    fun onDirectionFinderStart(operationName: String?)
    fun onDirectionFinderSuccess(
        route: List<Route?>?,
        operationName: String?
    )
}