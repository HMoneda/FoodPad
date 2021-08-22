package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var loginBtn : Button
    private lateinit var  signUpBtn : Button

    private val TAG = LogTags.MAIN_ACTIVITY.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser != null){
            val email = auth.currentUser!!.email
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra(IntentKeys.EMAIL_KEY.name, email)
            startActivity(intent)
            finish()
        }

        setContentView(R.layout.activity_main)

        loginBtn  = findViewById(R.id.logInBtn)
        signUpBtn = findViewById(R.id.signUpBtn)

        loginBtn.setOnClickListener {
            Log.d(TAG, "Login Clicked!")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpBtn.setOnClickListener {
            Log.d(TAG, "Sign Up Clicked!")
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}