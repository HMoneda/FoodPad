package com.mobdeve.s15.mco.foodpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private var TAG = LogTags.LOGIN_ACTIVITY.name

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val db = Firebase.firestore
        auth = Firebase.auth
        /*
        var usernameEt :  = findViewById(R.id.usernameET)
        var passwordEt : EditText = findViewById(R.id.passwordET)
        var loginAccBtn : Button = findViewById(R.id.loginAccBtn)
        var tempUser = User("sonson_great","Hakdoq1234", "sonson@sample.com")
         */

//        fun loginUser(){
//            var username = usernameEt.text.toString()
//            var password = passwordEt.text.toString()
//
//            if(username.isEmpty()){
//                usernameEt.error = "Username is Required!"
//                usernameEt.requestFocus()
//                return
//            }
//
//            if(password.isEmpty()){
//                passwordEt.error = "Password is Required!"
//                passwordEt.requestFocus()
//                return
//            }
//
//            auth.signInWithEmailAndPassword(username,password).addOnCompleteListener{ task ->
//                if(task.isSuccessful){
//                    Log.d(TAG,"Login Successful")
//                }else{
//                    Log.d(TAG, "Invalid Credentials")
//                }
//            }
//        }

//        loginAccBtn.setOnClickListener {
//            loginUser()
//        }
//        Add User
//        db.collection("users")
//            .add(tempUser)
//            .addOnSuccessListener {documentReference ->
//                Log.d(LogTags.LOGIN_ACTIVITY.name, "DocumentSnapshot added with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener{e ->
//                Log.w(LogTags.LOGIN_ACTIVITY.name, "Error Adding Document", e)
//            }

//        Get Users
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d(TAG, "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting documents.", exception)
//            }
    }
}