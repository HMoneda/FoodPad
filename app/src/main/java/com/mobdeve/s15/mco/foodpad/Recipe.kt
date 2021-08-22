package com.mobdeve.s15.mco.foodpad

data class Recipe(val name:String, val author:String, val likes:Int, val comments:Int){
    constructor() : this("", "", -1, -1)
}

