package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso

class UserCardAdapter(options: FirestoreRecyclerOptions<User>): FirestoreRecyclerAdapter<User, UserCardAdapter.ViewHolder>(options){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var userCardNameTV: TextView = itemView.findViewById(R.id.userCardNameTV)
        var userCardFollowersTV: TextView = itemView.findViewById(R.id.userCardFollowersTV)
        var userCardPhotoIV : ImageView = itemView.findViewById(R.id.userCardPhotoIV)

        fun viewUser(model : User){
            itemView.setOnClickListener {
                val loggedIn = (itemView.context as Activity).intent.getStringExtra(IntentKeys.UID_KEY.name)
                Log.d("TEST", loggedIn!!)
                val i = Intent(itemView.context, ViewOtherProfileActivity::class.java)

                i.putExtra(IntentKeys.UID_KEY.name, loggedIn)
                i.putExtra(IntentKeys.VIEW_USER_KEY.name, model.userID)
                if(loggedIn == model.userID){
                    i.putExtra(IntentKeys.FOLLOW_USER_KEY.name, false)
                }else{
                    i.putExtra(IntentKeys.FOLLOW_USER_KEY.name, true)
                }
                itemView.context.startActivity(i)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_card_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        val followers = model.followers.size
        holder.userCardNameTV.text = model.username
        holder.userCardFollowersTV.text = "Follower${if(followers <= 1) "" else "s"}: ${followers}"
        Picasso.get().load(Uri.parse(model.imgUri)).into(holder.userCardPhotoIV)
        holder.viewUser(model)
    }


}