package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.math.log

class HomeActivity : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private lateinit var createRecipeButton : FloatingActionButton
    private lateinit var logoutButton : ImageButton
    private val TAG = "HOME_ACTIVTIY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()
        createRecipeButton = findViewById(R.id.createRecipeFAB)
        logoutButton = findViewById(R.id.logoutBtn)
        logoutButton.isVisible = false

        val uid = intent.getStringExtra(IntentKeys.UID_KEY.name)
        val username = intent.getStringExtra(IntentKeys.USERNAME_KEY.name)
        Log.d(TAG, uid!!)
        Log.d(TAG, username!!)
        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.menu.getItem(0).isCheckable = true
        setFragment(HomeFragment())

        bottomNavigationView.setOnItemSelectedListener {menu ->

            when(menu.itemId){

                R.id.home_item -> {
                    setFragment(HomeFragment())
                    logoutButton.isVisible = false
                    true
                }

                R.id.search_item -> {
                    setFragment(SearchFragment())
                    logoutButton.isVisible = false
                    true
                }

                R.id.profile_item -> {
                    setFragment(ProfileFragment())
                    logoutButton.isVisible = true
                    true
                }

                else -> false
            }
        }

        createRecipeButton.setOnClickListener {
            Log.d(TAG, "Create Recipe Clicked!")
            val intent = Intent(this, CreateRecipeActivity::class.java)
            intent.apply {
                putExtra(IntentKeys.UID_KEY.name, uid)
                putExtra(IntentKeys.USERNAME_KEY.name, username)
            }
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_LONG).show()
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragmentContainer,fr)
        frag.commit()
    }

}