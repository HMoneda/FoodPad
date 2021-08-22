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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var usernameEt : EditText
    private lateinit var emailEt : EditText
    private lateinit var passwordEt : EditText
    private lateinit var signupBtn : Button
    private lateinit var loginTv : TextView

    private var TAG = LogTags.REGISTER_ACTIVITY.name

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
            var username : String = usernameEt.text.toString()
            var email : String = emailEt.text.toString()
            var password : String = passwordEt.text.toString()

            var newUser = User(username, email, "", 0, ArrayList())

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        db.collection("users").add(newUser).addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot added with ID: ${it.id}")
                        }.addOnFailureListener{
                            Log.w(LogTags.LOGIN_ACTIVITY.name, "Error Adding Document", it)
                        }
                        val i = Intent(this, HomeActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    }
}