package com.mobdeve.s15.mco.foodpad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var usernameEt : EditText
    private lateinit var emailEt : EditText
    private lateinit var passwordEt : EditText
    private lateinit var signupBtn : Button
    private lateinit var loginTv : TextView

    private val TAG = LogTags.REGISTER_ACTIVITY.name
    private val PASSWORD_LENGTH = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameEt = findViewById(R.id.signupUsernameEt)
        emailEt = findViewById(R.id.signupEmailEt)
        passwordEt = findViewById(R.id.signupPasswordEt)
        signupBtn = findViewById(R.id.loginAccBtn)
        loginTv = findViewById(R.id.loginTV)

        signupBtn.setOnClickListener {
            signupUser()
        }

        loginTv.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupUser(){
        val username : String = usernameEt.text.toString()
        val email : String = emailEt.text.toString()
        val password : String = passwordEt.text.toString()

        val newUser = User(username, email, "", 0, ArrayList())

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_LONG).show()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please provide valid email!", Toast.LENGTH_LONG).show()
        }else if(password.length < PASSWORD_LENGTH){
            Toast.makeText(this, "Password must be 8 characters long!", Toast.LENGTH_LONG).show()
        } else{
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    db.collection(FirestoreReferences.USERS_COLLECTION).add(newUser).addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
                    }.addOnFailureListener{
                        Log.w(LogTags.LOGIN_ACTIVITY.name, "Error Adding Document", it)
                    }
                    val i = Intent(this, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                }else{
                    Log.w(TAG, task.exception)
                    if(task.exception is FirebaseAuthUserCollisionException){
                        Toast.makeText(this, "Email Already Registered", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}