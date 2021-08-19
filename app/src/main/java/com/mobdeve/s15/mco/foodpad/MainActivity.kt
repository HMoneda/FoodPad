package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn : Button = findViewById(R.id.logInBtn)
        val signUpBtn: Button = findViewById(R.id.signUpBtn)

        loginBtn.setOnClickListener {
            Log.d(LogTags.MAIN_ACTIVITY.name, "Login Clicked!")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpBtn.setOnClickListener {
            Log.d(LogTags.MAIN_ACTIVITY.name, "Sign Up Clicked!")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}