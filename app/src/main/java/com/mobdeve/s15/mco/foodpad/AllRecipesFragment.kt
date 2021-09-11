package com.mobdeve.s15.mco.foodpad

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllRecipesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllRecipesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter : HomeAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var allRecipesRV : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_all_recipes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        allRecipesRV = view.findViewById(R.id.filterRecipesRV)

//        val query = FirestoreReferences.findRecipe(searchItem!!)
//        val fireStoreRecipeRecyclerOptions : FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()
//        adapter = HomeAdapter(fireStoreRecipeRecyclerOptions)
        searchAdapter = SearchAdapter()
        allRecipesRV.adapter = searchAdapter
        allRecipesRV.layoutManager = LinearLayoutManager(this.context)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllRecipesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                AllRecipesFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }

        fun newInstance() =
            AllRecipesFragment()
    }

    override fun onStart(){
        super.onStart()
//        adapter.startListening()
        updateData()
    }

    override fun onStop(){
        super.onStop()
//        adapter.stopListening()
//        adapter.notifyDataSetChanged()
    }

    fun updateData(){
        val searchItem = activity?.intent?.getStringExtra(IntentKeys.SEARCH_ITEM_KEY.name)
        CoroutineScope(Dispatchers.IO).launch {
            val recipes = FirestoreReferences.findRecipe(searchItem!!).get().await()
            val posts = ArrayList<Recipe>()
            for(recipe in recipes){
                posts.add(recipe.toObject(Recipe::class.java))
            }
            withContext(Dispatchers.Main){
                searchAdapter.setData(posts)
                searchAdapter.notifyDataSetChanged()
            }
        }
    }
}