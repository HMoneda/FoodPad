package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso

class ProfileAdapter(options: FirestoreRecyclerOptions<User>): FirestoreRecyclerAdapter<User, ProfileAdapter.ViewHolder>(options){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var usernameTV : TextView = itemView.findViewById(R.id.usernameTV)
        private var bioTV : TextView = itemView.findViewById(R.id.bioTV)
        private var followerCount : TextView = itemView.findViewById(R.id.numFollowersTV)
        private var profilePhoto : ImageView = itemView.findViewById(R.id.profileImage)
        private var editProfileBtn : Button = itemView.findViewById(R.id.editProfileBtn)

        fun bindData(model: User){
            val followers = model.followers.size
            usernameTV.text = model.username
            if(model.bio != ""){
                bioTV.text = model.bio
            }else{
                bioTV.text = "Tell the world a little about yourself!"
            }
            followerCount.text = "${followers} follower${if(followers <= 1) "" else "s"}"
            Picasso.get().load(Uri.parse(model.imgUri)).into(profilePhoto)
        }

        fun editProfile(model: User){
            editProfileBtn.setOnClickListener {
                val i = Intent(itemView.context, EditProfileActivity::class.java)
                i.apply {
                    putExtra(IntentKeys.EMAIL_KEY.name, model.email)
                    putExtra(IntentKeys.PROFILE_URI_KEY.name, model.imgUri)
                    putExtra(IntentKeys.USERNAME_KEY.name, model.username)
                    putExtra(IntentKeys.BIO_KEY.name, model.bio)
                    putExtra(IntentKeys.IS_GOOGLE_SIGNIN_KEY.name, model.googleSignIn)
                }
                itemView.context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.profile_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        holder.bindData(model)
        holder.editProfile(model)
    }



}