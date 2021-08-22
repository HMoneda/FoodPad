package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class NewGoogleLoginActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var usernameEt : EditText
    private lateinit var proceedBtn : Button

    private val TAG = "GOOGLE_LOGIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_google_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameEt = findViewById(R.id.usernameET)
        proceedBtn = findViewById(R.id.loginAccBtn)

        val accountID = intent.getStringExtra(IntentKeys.ACCOUNT_KEY.name)
        val email = intent.getStringExtra(IntentKeys.EMAIL_KEY.name)

        proceedBtn.setOnClickListener {
            val username = usernameEt.text.toString()
            val credential = GoogleAuthProvider.getCredential(accountID, null)

            val newUser = User(username,email!!,"",0, ArrayList(),ArrayList())

            auth.signInWithCredential(credential)
                .addOnCompleteListener(this){ task ->
                    if(task.isSuccessful){
                        Log.d(TAG, "signInWithCredential:success")
                        db.collection(FirestoreReferences.USERS_COLLECTION).add(newUser).addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
                        }.addOnFailureListener{
                            Log.w(TAG, "Error Adding Document", it)
                        }
                        val i = Intent(this, HomeActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
        }
    }
}