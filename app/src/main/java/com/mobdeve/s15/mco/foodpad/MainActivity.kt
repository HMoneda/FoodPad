package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

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

            if (email != null) {
                CoroutineScope(Dispatchers.IO).launch{
                    val res = FirestoreReferences.getUserByEmail(email).await()
                    val UID = res.documents[0].id
                    val profileUri = res.documents[0].toObject(User::class.java)!!.imgUri
                    withContext(Dispatchers.Main){
                        intent.putExtra(IntentKeys.UID_KEY.name, UID)
                        intent.putExtra(IntentKeys.EMAIL_KEY.name, email)
                        intent.putExtra(IntentKeys.PROFILE_URI_KEY.name, profileUri)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }else{
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
}