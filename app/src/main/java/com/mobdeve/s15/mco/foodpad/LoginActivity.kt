package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private var TAG = LogTags.LOGIN_ACTIVITY.name

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var usernameEt : EditText
    private lateinit var passwordEt : EditText
    private lateinit var loginAccBtn : Button
    private lateinit var googleLoginBtn : Button
    private lateinit var loginTV : TextView
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("168829935170-k0vas3b76kqj273c5nqhu7v257mn8h3s.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        usernameEt = findViewById(R.id.usernameET)
        passwordEt = findViewById(R.id.passwordET)
        loginAccBtn = findViewById(R.id.loginAccBtn)
        googleLoginBtn = findViewById(R.id.loginWithGoogleBtn)
        loginTV = findViewById(R.id.loginTV)

        loginAccBtn.setOnClickListener {
            val email : String = usernameEt.text.toString()
            val password : String = passwordEt.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()

                        FirestoreReferences.getUserByEmail(email).addOnSuccessListener {
                            if(it.documents.size != 0){
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.putExtra(IntentKeys.UID_KEY.name, it.documents[0].id)
                                intent.putExtra(IntentKeys.EMAIL_KEY.name, email)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }else{
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        googleLoginBtn.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, 100)
        }

        loginTV.setOnClickListener{
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account)
            }catch (e : ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account : GoogleSignInAccount){
        FirestoreReferences.getUserByEmail(account.email)
            .addOnSuccessListener {
                var found = false
                if(it.documents.size != 0){
                    found = true
                }
                if(!found){
                    val i = Intent(this, NewGoogleLoginActivity::class.java)
                    i.apply {
                        putExtra(IntentKeys.ACCOUNT_KEY.name, account.idToken!!)
                        putExtra(IntentKeys.EMAIL_KEY.name, account.email)
                        putExtra(IntentKeys.UID_KEY.name, it.documents[0].id)
                    }
                    startActivity(i)
                    finish()
                }else{
                    val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener(this){ task ->
                            if(task.isSuccessful){
                                Log.d(TAG, "signInWithCredential:success")

                                val i = Intent(this, HomeActivity::class.java)
                                i.apply{
                                    putExtra(IntentKeys.EMAIL_KEY.name, account.email)
                                    putExtra(IntentKeys.UID_KEY.name, it.documents[0].id)
                                }

                                startActivity(i)
                                finish()
                            }else{
                                Log.w(TAG, "signInWithCredential:failure", task.exception)
                            }
                        }
                }
            }

    }

}