package com.mobdeve.s15.mco.foodpad

data class Ingredient(val quantity : Int, val ingredient : String){
    constructor() : this(0, "")
}
