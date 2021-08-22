package com.mobdeve.s15.mco.foodpad

import com.google.firebase.firestore.DocumentReference

data class Recipe(val name:String, val user:String, val author:String, val likes:Int, val comments:Int){
    constructor() : this("", "","", 0, 0)
}

