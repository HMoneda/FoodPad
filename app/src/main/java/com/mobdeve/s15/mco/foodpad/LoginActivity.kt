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
import com.google.firebase.auth.ktx.auth
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
    private lateinit var loginTV : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        usernameEt = findViewById(R.id.usernameET)
        passwordEt = findViewById(R.id.passwordET)
        loginAccBtn = findViewById(R.id.loginAccBtn)
        loginTV = findViewById(R.id.loginTV)

        loginAccBtn.setOnClickListener {
            var email : String = usernameEt.text.toString()
            var password : String = passwordEt.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_LONG).show()
            }else{
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val i = Intent(this, HomeActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        loginTV.setOnClickListener{
            val i = Intent(this, RegisterActivity::class.java)
            startActivity(i)
            finish()
        }
    }
}