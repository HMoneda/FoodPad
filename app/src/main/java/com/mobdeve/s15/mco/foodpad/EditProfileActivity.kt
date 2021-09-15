package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception

class EditProfileActivity : AppCompatActivity() {
    private lateinit var profileImg : ImageView
    private lateinit var backBtn : ImageButton
    private lateinit var saveBtn : Button
    private lateinit var bioET : EditText
    private lateinit var newPasswordET : EditText
    private lateinit var confirmPasswordET : EditText
    private lateinit var usernameTV : TextView
    private lateinit var selectImgFAB : FloatingActionButton
    private lateinit var auth : FirebaseAuth
    private lateinit var changePasswordHeaderTV : TextView
    private lateinit var changePasswordV : View

    private val TAG = "EDIT_PROFILE_ACTIVITY"

    private var imageUri : Uri? = null

    private val activityResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                if (result.data != null) {
                    imageUri = result.data!!.data!!
                    Picasso.get().load(imageUri).into(profileImg)
                    Log.d(TAG, "Image Selected Complete")
                }
            } catch (err: Exception) {
                Log.d(TAG, err.localizedMessage!!)
            }
        }
    }

    private val cameraResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                if (result.data != null) {
                    imageUri = getImageUri(this,result.data!!.extras!!.get("data") as Bitmap)
                    Picasso.get().load(imageUri).into(profileImg)
                    Log.d(TAG, "Image Capture Complete")
                }
            } catch (err: Exception) {
                Log.d(TAG, err.localizedMessage!!)
            }
        }
    }

    private fun getImageUri(context : Context, bitmap : Bitmap) : Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            bitmap,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val profileUri = intent.getStringExtra(IntentKeys.PROFILE_URI_KEY.name)
        val username = intent.getStringExtra(IntentKeys.USERNAME_KEY.name)
        val email = intent.getStringExtra(IntentKeys.EMAIL_KEY.name)
        val bio = intent.getStringExtra(IntentKeys.BIO_KEY.name)
        val isGoogleSignIn = intent.getBooleanExtra(IntentKeys.IS_GOOGLE_SIGNIN_KEY.name, false)

        auth = FirebaseAuth.getInstance()

        profileImg = findViewById(R.id.profileImage)
        backBtn = findViewById(R.id.backBtn)
        saveBtn = findViewById(R.id.saveBtn)
        bioET = findViewById(R.id.bioET)
        newPasswordET = findViewById(R.id.newPasswordET)
        confirmPasswordET = findViewById(R.id.confirmPasswordET)
        usernameTV = findViewById(R.id.usernameTV)
        selectImgFAB = findViewById(R.id.editImageFAB)
        changePasswordHeaderTV = findViewById(R.id.changePasswordHeaderTV)
        changePasswordV = findViewById(R.id.changePasswordV)

        loadProfile(username!!, bio!!,profileUri!!, isGoogleSignIn)

        selectImgFAB.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Image")
            builder.setMessage("Choose your option")
            builder.setPositiveButton("Gallery"){ dialog, which ->
                dialog.dismiss()
                val i = Intent()
                i.apply {
                    type = "image/*"
                    action = Intent.ACTION_OPEN_DOCUMENT
                }
                activityResultLauncher.launch(Intent.createChooser(i, "Select Picture"))
            }
            builder.setNegativeButton("Camera"){dialog, which ->
                dialog.dismiss()
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePhotoIntent ->
                    takePhotoIntent.resolveActivity(packageManager)?.also {
                        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                        val storagePerm = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        if(permission != PackageManager.PERMISSION_GRANTED || storagePerm != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        }else{
                            cameraResultLauncher.launch(takePhotoIntent)
                        }
                    }
                }
            }
            val dialog : AlertDialog = builder.create()
            dialog.show()
        }

        saveBtn.setOnClickListener {
            val newBio = bioET.text.toString()
            val newPassword = newPasswordET.text.toString()
            val confirmedNewPassword = confirmPasswordET.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val uid = FirestoreReferences.getUserByEmail(email!!).await().documents[0].id

                    if(imageUri != null){
                        FirestoreReferences.updateUserPhoto(uid, imageUri)
                    }

                    FirestoreReferences.updateUserBio(uid, newBio).await()

                    if(newPassword.isNotEmpty() && newPassword == confirmedNewPassword){
                        val user = auth.currentUser
                        user?.updatePassword(newPassword)?.await()
                        Log.d(TAG, "Password Updated!")
                    }else if(newPassword != confirmedNewPassword){
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@EditProfileActivity, "Passwords Do Not Match!", Toast.LENGTH_LONG).show()
                        }
                    }

                    withContext(Dispatchers.Main){
                        Toast.makeText(this@EditProfileActivity, "Profile Updated", Toast.LENGTH_LONG).show()
                        finish()
                    }

                }catch(err : Exception){
                    Log.w("EDIT_PROFILE_ACTIVITY", "Error Updating Profile",err)
                }
            }
        }

        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadProfile(username : String, bio : String, uri: String, isGoogleSignIn : Boolean){
        Picasso.get().load(Uri.parse(uri)).into(profileImg)
        usernameTV.text = username
        bioET.setText(bio)
        Log.d(TAG,isGoogleSignIn.toString())
        if(isGoogleSignIn){
            newPasswordET.isVisible = false
            confirmPasswordET.isVisible = false
            changePasswordHeaderTV.isVisible = false
            changePasswordV.isVisible = false
        }
    }
}