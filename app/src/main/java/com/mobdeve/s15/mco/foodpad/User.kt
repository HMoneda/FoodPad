package com.mobdeve.s15.mco.foodpad

data class User(val username:String, val email:String, val bio:String, val followerCount:Int, val followers : ArrayList<String>, val following : ArrayList<String>){
    constructor() : this("","", "", 0, ArrayList(), ArrayList())
}