package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SearchAdapter(private var data : ArrayList<Recipe> = ArrayList()) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeName: TextView = itemView.findViewById(R.id.recipeNameTV)
        var recipeAuthor: TextView = itemView.findViewById(R.id.recipeCreatorTV)
        var numLikes: TextView = itemView.findViewById(R.id.numLikesTV)
        var numComments: TextView = itemView.findViewById(R.id.numCommentsTV)
        var recipeImg : ImageView = itemView.findViewById(R.id.recipeImageIV)
        var classificationIV : ImageView = itemView.findViewById(R.id.classificationIV)

        fun viewRecipe(model : Recipe){
            itemView.setOnClickListener {
                val loggedIn = (itemView.context as Activity).intent.getStringExtra(IntentKeys.UID_KEY.name)
                Log.d("TEST", loggedIn!!)
                val i = Intent(itemView.context, ViewRecipeActivity::class.java)
                i.putExtra(IntentKeys.RECIPE_ID_KEY.name, model.recipeID)
                i.putExtra(IntentKeys.RECIPE_AUTHOR_UID_KEY.name, model.userID)
                i.putExtra(IntentKeys.USERNAME_KEY.name, model.author)
                i.putExtra(IntentKeys.RECIPE_IMG_URI_KEY.name, model.recipeImg)
                i.putExtra(IntentKeys.UID_KEY.name, loggedIn)
                if(loggedIn == model.userID){
                    i.putExtra(IntentKeys.RECIPE_EDITABLE_KEY.name, true)
                }else{
                    i.putExtra(IntentKeys.RECIPE_EDITABLE_KEY.name, false)
                }
                itemView.context.startActivity(i)
            }
        }

        fun setClassification(classification : String){
            when (classification) {
                "Appetizer" -> {
                    classificationIV.setImageResource(R.drawable.ic_appetizer)
                }
                "Dessert" -> {
                    classificationIV.setImageResource(R.drawable.ic_dessert)
                }
                "Main Course" -> {
                    classificationIV.setImageResource(R.drawable.ic_main_course)
                }
                "Beverage" -> {
                    classificationIV.setImageResource(R.drawable.ic_beverage)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: SearchAdapter.ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val numComments = FirestoreReferences.getCommentQuery(data[position].recipeID!!).get().await().size()
            withContext(Dispatchers.Main){
                holder.recipeName.text = data[position].recipeName
                holder.recipeAuthor.text = data[position].author
                holder.numLikes.text = data[position].likes.size.toString()
                holder.numComments.text = numComments.toString()
                Picasso.get().load(Uri.parse(data[position].recipeImg)).into(holder.recipeImg)
                holder.setClassification(data[position].classification)
                holder.viewRecipe(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(newData : ArrayList<Recipe>){
        data = newData
    }
}