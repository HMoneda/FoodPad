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
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
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


        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill all the fields!", Toast.LENGTH_LONG).show()
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Please provide valid email!", Toast.LENGTH_LONG).show()
        }else if(password.length < PASSWORD_LENGTH){
            Toast.makeText(this, "Password must be 8 characters long!", Toast.LENGTH_LONG).show()
        } else{
            CoroutineScope(Dispatchers.IO).launch{
                try{
                    val userDoc = FirestoreReferences.getUserByUsername(username).await()
                    val profileUri = FirestoreReferences.getDefaultAvatar().await().toString()
                    val newUser = User(username, email, profileUri, "", 0, ArrayList(), ArrayList(), false)

                    if(!userDoc.isEmpty){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@RegisterActivity, "Username Taken", Toast.LENGTH_LONG).show()
                        }
                    }else{
                        auth.createUserWithEmailAndPassword(email, password).await()
                        val res = FirestoreReferences.addUser(newUser).await()
                        Log.d(TAG, "DocumentSnapshot added with ID: ${res.id}")
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@RegisterActivity, "Successfully Registered", Toast.LENGTH_LONG).show()
                            val i = Intent(this@RegisterActivity, HomeActivity::class.java)
                            i.apply {
                                putExtra(IntentKeys.EMAIL_KEY.name, email)
                                putExtra(IntentKeys.UID_KEY.name, res.id)
                                putExtra(IntentKeys.PROFILE_URI_KEY.name, profileUri)
                            }
                            startActivity(i)
                            finish()
                        }
                    }
                }catch (err : FirebaseAuthUserCollisionException){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity, "Email Already Registered", Toast.LENGTH_LONG).show()
                    }
                }catch (err : Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}