package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: SearchAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.followedUserRecipesRV)
//        db = FirebaseFirestore.getInstance()

//        val query : Query = db.collection(FirestoreReferences.RECIPES_COLLECTION).orderBy(FirestoreReferences.RECIPE_NAME_FIELD)
//
//        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()
//
//        adapter = HomeAdapter(firestoreRecyclerOptions)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this.context)

        adapter = SearchAdapter()
        layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onStart(){
        super.onStart()
        updateData()
//        adapter.startListening()
    }

    override fun onStop(){
        super.onStop()
//        adapter.stopListening()
    }

    fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            val UID = activity?.intent?.getStringExtra(IntentKeys.UID_KEY.name)
            val recipes = FirestoreReferences.getRecipes().await()
            val user = FirestoreReferences.getUserByID(UID!!).await().toObject(User::class.java)
            val posts = ArrayList<Recipe>()
            for(recipe in recipes){
                if(recipe.toObject(Recipe::class.java).userID in user!!.following){
                    posts.add(recipe.toObject(Recipe::class.java))
                }
            }
            withContext(Dispatchers.Main){
                adapter.setData(posts)
                adapter.notifyDataSetChanged()
            }
        }
    }
}