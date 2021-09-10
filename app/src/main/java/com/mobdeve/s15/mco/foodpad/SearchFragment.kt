package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SearchFragment : Fragment() {

    private lateinit var popularRecipesRV : RecyclerView
    private lateinit var recipeAdapter: HomeAdapter
    private lateinit var searchView : SearchView


    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        popularRecipesRV = view.findViewById(R.id.popularRecipesRV)
        searchView = view.findViewById(R.id.recipeSV)

        val UID = activity?.intent?.getStringExtra(IntentKeys.UID_KEY.name)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val intent = Intent(this@SearchFragment.context, SearchableActivity::class.java)
                intent.putExtra(IntentKeys.SEARCH_ITEM_KEY.name, p0)
                intent.putExtra(IntentKeys.UID_KEY.name, UID)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                // on text change
                return true
            }
        })

        val query = FirestoreReferences.getPopularRecipesQuery()

        val fireStoreRecipeRecyclerOptions : FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()

        recipeAdapter = HomeAdapter(fireStoreRecipeRecyclerOptions)
        popularRecipesRV.adapter = recipeAdapter
        popularRecipesRV.layoutManager = LinearLayoutManager(this.context)
    }

    override fun onStart(){
        super.onStart()
        recipeAdapter.startListening()
    }

    override fun onStop(){
        super.onStop()
        recipeAdapter.stopListening()
        recipeAdapter.notifyDataSetChanged()
    }

}