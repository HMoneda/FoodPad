package com.mobdeve.s15.mco.foodpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SearchableActivity : AppCompatActivity() {
    private lateinit var filterClassificationSpinner : Spinner
    private lateinit var searchItemTV : TextView
    private lateinit var backBtn : ImageButton
    private lateinit var resultsRV : RecyclerView
    private lateinit var recipeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        val searchItem = intent.getStringExtra(IntentKeys.SEARCH_ITEM_KEY.name)
        Log.d("SEARCHABLE_ACTIVITY", "Search Results for ${searchItem}")

        resultsRV = findViewById(R.id.searchResultsRV)
        searchItemTV = findViewById(R.id.searchItemTV)
        backBtn = findViewById(R.id.searchableBackBtn)
        filterClassificationSpinner = findViewById(R.id.filterClassificationSpinner)

        val filters = arrayOf("All", "Appetizer", "Main Course", "Dessert", "Beverage", "User")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        filterClassificationSpinner.adapter = adapter

        searchItemTV.text = "Search Results for: ${searchItem}"

        filterClassificationSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                Log.d("SEARCHABLE_ACTIVITY", position.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        backBtn.setOnClickListener {
            finish()
        }

        val query = FirestoreReferences.findRecipe(searchItem!!)
        val fireStoreRecipeRecyclerOptions : FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()

        recipeAdapter = HomeAdapter(fireStoreRecipeRecyclerOptions)
        resultsRV.adapter = recipeAdapter
        resultsRV.layoutManager = LinearLayoutManager(this)

    }


    override fun onStart() {
        super.onStart()
        Log.d("SEARCHABLE_ACTIVITY", "onStart Called")
        recipeAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        recipeAdapter.stopListening()
        recipeAdapter.notifyDataSetChanged()
    }
}