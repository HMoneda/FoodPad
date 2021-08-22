package com.mobdeve.s15.mco.foodpad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProfileAdapter(options: FirestoreRecyclerOptions<User>): FirestoreRecyclerAdapter<User, ProfileAdapter.ViewHolder>(options){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var usernameTV : TextView = itemView.findViewById(R.id.usernameTV)
        private var bioTV : TextView = itemView.findViewById(R.id.bioTV)
        private var followerCount : TextView = itemView.findViewById(R.id.numFollowersTV)

        fun bindData(model: User){
            usernameTV.text = model.username
            if(model.bio != ""){
                bioTV.text = model.bio
            }else{
                bioTV.text = "This user does not have a bio yet."
            }
            followerCount.text = "${model.followerCount} follower${if(model.followerCount <= 1) "" else "s"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.profile_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: User) {
        holder.bindData(model)
    }



}