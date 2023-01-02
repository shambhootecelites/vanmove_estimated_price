package com.vanmove.passesger.interfaces

interface DimensionSubmit {
    fun WeightSubmit(position: Int, Weight: String?)
    fun LenghtSubmit(position: Int, Lenght: String?)
    fun WidthSubmit(position: Int, Width: String?)
    fun HeightSubmit(position: Int, Height: String?)
    fun QuantitySubmit(position: Int, Quantity: String?)
    fun NameSubmit(position: Int, Name: String?)
}