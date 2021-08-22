package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

class HomeActivity : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        var bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.menu.getItem(0).isCheckable = true
        setFragment(HomeFragment())

        bottomNavigationView.setOnNavigationItemSelectedListener {menu ->

            when(menu.itemId){

                R.id.home_item -> {
                    setFragment(HomeFragment())
                    true
                }

                R.id.search_item -> {
                    setFragment(SearchFragment())
                    true
                }

                R.id.create_item -> {
                    setFragment(CreateFragment())
                    true
                }

                R.id.notification_item -> {
                    setFragment(NotificationFragment())
                    true
                }

                R.id.profile_item -> {
                    setFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

    }

    fun setFragment(fr : Fragment){
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.fragmentContainer,fr)
        frag.commit()
    }

}