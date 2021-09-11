package com.mobdeve.s15.mco.foodpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.lang.reflect.Array.newInstance

class SearchableActivity : AppCompatActivity() {
    private lateinit var searchItemTV : TextView
    private lateinit var backBtn : ImageButton
    private lateinit var filterTL : TabLayout
    private lateinit var searchVP : ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        val filters = arrayOf("Recipes", "Appetizers", "Main Courses", "Desserts", "Beverages", "Users")

        val searchItem = intent.getStringExtra(IntentKeys.SEARCH_ITEM_KEY.name)
        Log.d("SEARCHABLE_ACTIVITY", "Search Results for ${searchItem}")

        searchItemTV = findViewById(R.id.searchItemTV)
        backBtn = findViewById(R.id.searchableBackBtn)
        filterTL = findViewById(R.id.filterTL)
        searchVP = findViewById(R.id.searchVP)

        searchVP.adapter = VPAdapter(this)

        TabLayoutMediator(filterTL, searchVP){tab, position ->
            tab.text = filters[position]
        }.attach()

        searchItemTV.text = "Search Results for: ${searchItem}"

        backBtn.setOnClickListener {
            finish()
        }

    }


    override fun onStart() {
        super.onStart()
        Log.d("SEARCHABLE_ACTIVITY", "onStart Called")
    }

    override fun onPause() {
        super.onPause()
    }

    class VPAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity){
        override fun getItemCount(): Int {
            return 6
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> AllRecipesFragment.newInstance()
                1 -> AppetizersFragment.newInstance()
                2 -> MainCoursesFragment.newInstance()
                3 -> DessertsFragment.newInstance()
                4 -> BeveragesFragment.newInstance()
                5 -> UsersFragment.newInstance()
                else -> AllRecipesFragment.newInstance()
            }
        }
    }
}