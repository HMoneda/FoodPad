package com.mobdeve.s15.mco.foodpad

data class Comment(val userImg : String, val username : String, val contents : String){
    constructor() : this ("", "", "")
}
