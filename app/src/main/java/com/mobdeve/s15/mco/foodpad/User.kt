package com.mobdeve.s15.mco.foodpad

import android.net.Uri
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class User(
    val username:String, val email:String, val imgUri:String, val bio:String, val followers: ArrayList<String>, val following: ArrayList<String>, val googleSignIn: Boolean, @DocumentId val userID : String? = null){
    constructor() : this("","", "", "", ArrayList(), ArrayList(), false)
}