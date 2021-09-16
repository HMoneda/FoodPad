package com.mobdeve.s15.mco.foodpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CommentActivity : AppCompatActivity() {
    private lateinit var commentET : EditText
    private lateinit var sendCommentBtn : Button
    private lateinit var commentsRV : RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var backBtn : ImageButton

    private val TAG = "COMMENT_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val recipeID = intent.getStringExtra(IntentKeys.RECIPE_ID_KEY.name)

        commentET = findViewById(R.id.commentET)
        sendCommentBtn = findViewById(R.id.sendCommentBtn)
        backBtn = findViewById(R.id.commentBackBtn)
        commentsRV = findViewById(R.id.commentsRV)

        sendCommentBtn.setOnClickListener {
            sendComment()
        }

        backBtn.setOnClickListener {
            setResult(4)
            finish()
        }

        val query = FirestoreReferences.getCommentQuery(recipeID!!)
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Comment> = FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java).build()

        commentAdapter = CommentAdapter(firestoreRecyclerOptions)
        commentsRV.adapter = commentAdapter
        commentsRV.layoutManager = LinearLayoutManager(this)
    }

    private fun sendComment(){
        val recipeID = intent.getStringExtra(IntentKeys.RECIPE_ID_KEY.name)
        val uid = intent.getStringExtra(IntentKeys.UID_KEY.name)
        val content =  commentET.text.toString()

        if(content.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                val newComment = Comment(uid!!,recipeID!!,content)
                val res = FirestoreReferences.addComment(newComment).await()
                Log.d(TAG, "DocumentSnapshot added with ID: ${res.id}")
                withContext(Dispatchers.Main){
                    commentET.setText("")
                    Toast.makeText(this@CommentActivity, "Comment Sent", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        commentAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentAdapter.stopListening()
        commentAdapter.notifyDataSetChanged()
    }
}