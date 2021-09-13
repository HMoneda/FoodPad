package com.mobdeve.s15.mco.foodpad

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
class ProfileFragment : Fragment() {

    private lateinit var profileRecyclerView: RecyclerView
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var profileAdapter: ProfileAdapter
    private lateinit var recipeAdapter: HomeAdapter


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileRecyclerView = view.findViewById(R.id.profileRV)
        recipeRecyclerView = view.findViewById(R.id.userRecipesRV)

        val email = activity?.intent?.getStringExtra(IntentKeys.EMAIL_KEY.name)
        val UID = activity?.intent?.getStringExtra(IntentKeys.UID_KEY.name)
        val query = FirestoreReferences.getUserByEmailQuery(email)
        val recipeQuery = FirestoreReferences.getUserRecipesQuery(UID)

        val firestoreProfileRecyclerOptions: FirestoreRecyclerOptions<User> = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()
        val fireStoreRecipeRecyclerOptions : FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(recipeQuery, Recipe::class.java).build()

        profileAdapter = ProfileAdapter(firestoreProfileRecyclerOptions)
        profileRecyclerView.adapter = profileAdapter
        profileRecyclerView.layoutManager = LinearLayoutManager(this.context)
        profileRecyclerView.itemAnimator = null

        recipeAdapter = HomeAdapter(fireStoreRecipeRecyclerOptions)
        recipeRecyclerView.adapter = recipeAdapter
        recipeRecyclerView.layoutManager = LinearLayoutManager(this.context)

    }

    override fun onStart(){
        super.onStart()
        profileAdapter.startListening()
        recipeAdapter.startListening()
    }

    override fun onStop(){
        super.onStop()
        profileAdapter.stopListening()
        recipeAdapter.stopListening()
        profileAdapter.notifyDataSetChanged()
        recipeAdapter.notifyDataSetChanged()
    }
}