package com.mobdeve.s15.mco.foodpad

import android.net.Uri
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommentAdapter(options: FirestoreRecyclerOptions<Comment>): FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder>(options) {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private var userCommentUsernameTV : TextView = itemView.findViewById(R.id.userCommentUsernameTV)
        private var userCommentPhotoIV : ImageView = itemView.findViewById(R.id.userCommentPhotoIV)
        private var userCommentContentTV : TextView = itemView.findViewById(R.id.userCommentContentTV)

        fun bindData(model : Comment){
            CoroutineScope(Dispatchers.IO).launch {
                val user = FirestoreReferences.getUserByID(model.userID).await().toObject(User::class.java)
                withContext(Dispatchers.Main){
                    userCommentUsernameTV.text = user!!.username
                    Picasso.get().load(Uri.parse(user.imgUri)).into(userCommentPhotoIV)
                    userCommentContentTV.text = model.contents
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.comment_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comment) {
        holder.bindData(model)
    }
}