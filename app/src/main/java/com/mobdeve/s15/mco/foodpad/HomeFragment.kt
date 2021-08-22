package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var logoutBtn : Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: HomeAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.followedUserRecipesRV)
        db = FirebaseFirestore.getInstance()

        val query : Query = db.collection(FirestoreReferences.RECIPES_COLLECTION).orderBy(FirestoreReferences.RECIPE_NAME_FIELD)

        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()

        adapter = HomeAdapter(firestoreRecyclerOptions)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)



        logoutBtn = view.findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(activity, "Logged Out Successfully", Toast.LENGTH_LONG).show()
            val i = Intent(activity, MainActivity::class.java)
            activity?.startActivity(i)
            activity?.finish()
        }

        layoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onStart(){
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop(){
        super.onStop()
        adapter!!.stopListening()
    }
}