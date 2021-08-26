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
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: HomeAdapter
    private lateinit var db: FirebaseFirestore

    private var imageUri : Uri? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.followedUserRecipesRV)
        db = FirebaseFirestore.getInstance()

//        val query : Query = db.collection(FirestoreReferences.RECIPES_COLLECTION).orderBy(FirestoreReferences.RECIPE_NAME_FIELD)
//
//        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()
//
//        adapter = HomeAdapter(firestoreRecyclerOptions)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this.context)


//        layoutManager = LinearLayoutManager(this.context)
//        recyclerView.layoutManager = layoutManager
//        recyclerView.adapter = adapter
    }

    override fun onStart(){
        super.onStart()
//        adapter.startListening()
    }

    override fun onStop(){
        super.onStop()
//        adapter.stopListening()
    }
}