package com.mobdeve.s15.mco.foodpad

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference

data class Recipe(val recipeName: String, val userID: String, val author: String, val likes: ArrayList<String>,
                  val servings: String, val prepTime: Int, val ingredients: ArrayList<Ingredient>,
                  val procedures : ArrayList<String>, val recipeImg : String, val classification : String,
                  val numLikes : Int,
                  @DocumentId val recipeID : String? = null){
    constructor() : this("", "","", ArrayList(), "", 0, ArrayList(), ArrayList(), "", "", 0)
}

