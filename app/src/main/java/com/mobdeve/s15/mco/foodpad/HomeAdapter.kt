package com.mobdeve.s15.mco.foodpad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HomeAdapter(options: FirestoreRecyclerOptions<Recipe>): FirestoreRecyclerAdapter<Recipe, HomeAdapter.ViewHolder>(options){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var recipeName: TextView = itemView.findViewById(R.id.recipeNameTV)
        var recipeAuthor: TextView = itemView.findViewById(R.id.recipeCreatorTV)
        var numLikes: TextView = itemView.findViewById(R.id.numLikesTV)
        var numComments: TextView = itemView.findViewById(R.id.numCommentsTV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recipe_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Recipe) {
        holder.recipeName.text = model.name
        holder.recipeAuthor.text = model.author
        holder.numLikes.text = model.likes.toString()
        holder.numComments.text = model.comments.toString()
    }


}