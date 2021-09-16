package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.net.URI

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
        val profileUri = intent.getStringExtra(IntentKeys.PROFILE_URI_KEY.name)

        proceedBtn.setOnClickListener {
            val username = usernameEt.text.toString()
            val credential = GoogleAuthProvider.getCredential(accountID, null)

            val newUser = User(username,email!!,profileUri!!,"", ArrayList(),ArrayList(), true)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userDoc = FirestoreReferences.getUserByUsername(username).await()
                    if(!userDoc.isEmpty) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@NewGoogleLoginActivity, "Username Taken", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                    auth.signInWithCredential(credential).await()
                    Log.d(TAG, "signInWithCredential:success")
                    val res = FirestoreReferences.addUser(newUser).await()
                    Log.d(TAG, "DocumentSnapshot added with ID: ${res.id}")
                    withContext(Dispatchers.Main) {
                        val i = Intent(this@NewGoogleLoginActivity, HomeActivity::class.java)
                        i.apply {
                            putExtra(IntentKeys.EMAIL_KEY.name, email)
                            putExtra(IntentKeys.UID_KEY.name, res.id)
                            putExtra(IntentKeys.PROFILE_URI_KEY.name, profileUri)
                            putExtra(IntentKeys.USERNAME_KEY.name, username)

                        }
                        startActivity(i)
                        finishAffinity()
                    }
                } catch (err: FirebaseAuthInvalidUserException) {
                    Log.w(TAG, "signInWithCredential:failure", err)
                } catch (err: Exception){
                    Log.w(TAG, "Error Adding Document", err)
                }
            }
        }
    }
}