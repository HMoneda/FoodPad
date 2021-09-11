package com.mobdeve.s15.mco.foodpad

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ViewOtherProfileActivity : AppCompatActivity() {
    private lateinit var viewProfileImgIV : ImageView
    private lateinit var viewProfileUsernameTV : TextView
    private lateinit var viewProfileBioTV : TextView
    private lateinit var viewProfileFollowersTV : TextView
    private lateinit var followUserBtn : Button
    private lateinit var backBtn : ImageButton
    private lateinit var viewUserRecipesRV : RecyclerView
    private lateinit var recipeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_other_profile)

        viewUserRecipesRV = findViewById(R.id.viewUserRecipesRV)
        viewProfileImgIV = findViewById(R.id.viewProfileImageIV)
        viewProfileUsernameTV = findViewById(R.id.viewProfileUsernameTV)
        viewProfileBioTV = findViewById(R.id.viewProfileBioTV)
        viewProfileFollowersTV = findViewById(R.id.viewProfileNumFollowersTV)
        followUserBtn = findViewById(R.id.followUserBtn)
        backBtn = findViewById(R.id.viewProfileBackBtn)

        val followable = intent.getBooleanExtra(IntentKeys.FOLLOW_USER_KEY.name, false)
        val viewUserUID = intent.getStringExtra(IntentKeys.VIEW_USER_KEY.name)
        val loggedInUID = intent.getStringExtra(IntentKeys.UID_KEY.name)

        followUserBtn.isVisible = followable

        followUserBtn.setOnClickListener {
            followUser(viewUserUID!!, loggedInUID!!)
        }

        backBtn.setOnClickListener {
            finish()
        }

        val query = FirestoreReferences.getUserRecipesQuery(viewUserUID)
        val fireStoreRecipeRecyclerOptions : FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()

        recipeAdapter = HomeAdapter(fireStoreRecipeRecyclerOptions)
        viewUserRecipesRV.adapter = recipeAdapter
        viewUserRecipesRV.layoutManager = LinearLayoutManager(this)
    }

    private fun getData(userID : String){
        CoroutineScope(Dispatchers.IO).launch {
            val user = FirestoreReferences.getUserByID(userID).await().toObject(User::class.java)
            withContext(Dispatchers.Main){
                bindData(user!!)
            }
        }
    }

    private fun bindData(user : User){
        val loggedInUID = intent.getStringExtra(IntentKeys.UID_KEY.name)
        val followers = user.followers.size

        viewProfileUsernameTV.text = user.username
        if(user.bio != ""){
            viewProfileBioTV.text = user.bio
        }else{
            viewProfileBioTV.text = "This user does not have a bio yet."
        }
        viewProfileFollowersTV.text = "${followers} follower${if(followers <= 1) "" else "s"}"
        if(loggedInUID in user.followers){
            followUserBtn.text = "Unfollow"
        }else{
            followUserBtn.text = "Follow"
        }
        Picasso.get().load(Uri.parse(user.imgUri)).into(viewProfileImgIV)
    }

    private fun followUser(followedUserUID : String, loggedInUID : String){
        CoroutineScope(Dispatchers.IO).launch {
            val user = FirestoreReferences.getUserByID(followedUserUID).await().toObject(User::class.java)
            val loggedInUser = FirestoreReferences.getUserByID(loggedInUID).await().toObject(User::class.java)
            if(loggedInUID in user!!.followers){
                user.followers.remove(loggedInUID)
            }else{
                user.followers.add(loggedInUID)
            }
            if(followedUserUID in loggedInUser!!.following){
                loggedInUser.following.remove(followedUserUID)
            }else{
                loggedInUser.following.add(followedUserUID)
            }
            FirestoreReferences.followUser(followedUserUID, user.followers)
            FirestoreReferences.addFollowedUser(loggedInUID, loggedInUser.following)
            withContext(Dispatchers.Main){
                val followers = user.followers.size
                viewProfileFollowersTV.text = "${followers} follower${if(followers <= 1) "" else "s"}"
                if(loggedInUID in user.followers){
                    followUserBtn.text = "Unfollow"
                }else{
                    followUserBtn.text = "Follow"
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val viewUserUID = intent.getStringExtra(IntentKeys.VIEW_USER_KEY.name)
        getData(viewUserUID!!)
        recipeAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        recipeAdapter.stopListening()
        recipeAdapter.notifyDataSetChanged()
    }
}