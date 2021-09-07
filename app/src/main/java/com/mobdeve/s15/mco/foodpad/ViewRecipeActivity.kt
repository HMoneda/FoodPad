package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewRecipeActivity : AppCompatActivity() {

    private lateinit var recipeImg : ImageView
    private lateinit var recipeName: TextView
    private lateinit var classification : ImageView
    private lateinit var numLikesTV : TextView
    private lateinit var numCommentsTV : TextView
    private lateinit var editRecipeBtn : Button
    private lateinit var numServingsTV : TextView
    private lateinit var prepTimeTV : TextView
    private lateinit var backBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_recipe)

        recipeImg = findViewById(R.id.recipeImage)
        recipeName = findViewById(R.id.viewRecipeNameTV)
        classification = findViewById(R.id.classificationIV)
        numLikesTV = findViewById(R.id.numLikesTV)
        numCommentsTV = findViewById(R.id.numCommentsTV)
        editRecipeBtn = findViewById(R.id.editRecipeBtn)
        numServingsTV = findViewById(R.id.numServingsTV)
        prepTimeTV = findViewById(R.id.prepTimeTV)
        backBtn = findViewById(R.id.recipeBackBtn)

        val uid = intent.getStringExtra(IntentKeys.RECIPE_AUTHOR_UID_KEY.name)
        val recipeID = intent.getStringExtra(IntentKeys.RECIPE_ID_KEY.name)
        val username = intent.getStringExtra(IntentKeys.USERNAME_KEY.name)
        val recipeImgUri = intent.getStringExtra(IntentKeys.RECIPE_IMG_URI_KEY.name)
        val userRecipeOwned = intent.getBooleanExtra(IntentKeys.RECIPE_EDITABLE_KEY.name,false)

        editRecipeBtn.isVisible = userRecipeOwned

        editRecipeBtn.setOnClickListener {
            val i = Intent(this@ViewRecipeActivity, EditRecipeActivity::class.java)
            i.putExtra(IntentKeys.RECIPE_ID_KEY.name, recipeID)
            i.putExtra(IntentKeys.RECIPE_AUTHOR_UID_KEY.name, uid)
            i.putExtra(IntentKeys.USERNAME_KEY.name, username)
            i.putExtra(IntentKeys.RECIPE_IMG_URI_KEY.name, recipeImgUri)
            startActivity(i)
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val recipeID = intent.getStringExtra(IntentKeys.RECIPE_ID_KEY.name)
        getData(recipeID!!)
    }

    private fun getData(recipeID : String){
        CoroutineScope(Dispatchers.IO).launch {
            val recipe = FirestoreReferences.getRecipe(recipeID).await().toObject(Recipe::class.java)
            withContext(Dispatchers.Main){
                bindData(recipe!!)
            }
        }
    }

    private fun bindData(recipe : Recipe){
        recipeName.text = recipe.recipeName
        numLikesTV.text = recipe.likes.toString()
        numCommentsTV.text = recipe.comments.toString()
        numServingsTV.text = "Servings: ${recipe.servings}"
        prepTimeTV.text = "Preparation time: ${recipe.prepTime}"
        Picasso.get().load(Uri.parse(recipe.recipeImg)).into(recipeImg)
    }
}