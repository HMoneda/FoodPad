package com.mobdeve.s15.mco.foodpad

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Comment(val userID: String, val recipeID: String, val contents: String, @ServerTimestamp val timestamp: Date? = null){
    constructor() : this ("", "", "")
}
