package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private lateinit var uploadBtn : Button
    private lateinit var selectBtn : Button
    private lateinit var selectedImgIV : ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: HomeAdapter
    private lateinit var db: FirebaseFirestore

    private var imageUri : Uri? = null

    private val activityResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                if (result.data != null) {
                    imageUri = result.data!!.data!!
                    Picasso.get().load(imageUri).into(selectedImgIV)
                    Log.d("HOME_FRAGMENT", "Image Selected Complete")
                }
            } catch (err: Exception) {
                Log.d("HOME_FRAGMENT", err.localizedMessage!!)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home , container , false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val UID = activity?.intent?.getStringExtra(IntentKeys.UID_KEY.name)
        recyclerView = view.findViewById(R.id.followedUserRecipesRV)
        db = FirebaseFirestore.getInstance()

//        val query : Query = db.collection(FirestoreReferences.RECIPES_COLLECTION).orderBy(FirestoreReferences.RECIPE_NAME_FIELD)
//
//        val firestoreRecyclerOptions: FirestoreRecyclerOptions<Recipe> = FirestoreRecyclerOptions.Builder<Recipe>().setQuery(query, Recipe::class.java).build()
//
//        adapter = HomeAdapter(firestoreRecyclerOptions)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this.context)

        selectBtn = view.findViewById(R.id.selectBtn)
        uploadBtn = view.findViewById(R.id.uploadBtn)
        selectedImgIV = view.findViewById(R.id.selectedImgIV)

        selectBtn.setOnClickListener {
            val i = Intent()
            i.apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
            }
            activityResultLauncher.launch(Intent.createChooser(i, "Select Picture"))
        }

        uploadBtn.setOnClickListener {
            if(imageUri != null){
                val progressDialog = ProgressDialog(view.context)
                progressDialog.setTitle("Uploading");
                progressDialog.show()

                val imgRef = FirestoreReferences.getStorageReferenceInstance()
                    .child(FirestoreReferences.generateUserPhotoPath(UID!!, imageUri!!))

                imgRef.putFile(imageUri!!).addOnProgressListener { taskSnapshot ->
                    var progress = (100.0 * taskSnapshot.bytesTransferred)/ taskSnapshot.totalByteCount
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.setMessage("Uploaded ${progress}%")

                }.addOnSuccessListener {
                    progressDialog.setCanceledOnTouchOutside(true)
                    progressDialog.setMessage("Success!")
                }.addOnFailureListener{
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.setMessage("Error occurred. Please try again.")
                }
            }else{
                Toast.makeText(view.context,"Please supply an image to post", Toast.LENGTH_LONG).show()
            }
        }

//        layoutManager = LinearLayoutManager(this.context)
//        recyclerView.layoutManager = layoutManager
//        recyclerView.adapter = adapter
    }

    override fun onStart(){
        super.onStart()
//        adapter.startListening()
    }

    override fun onStop(){
        super.onStop()
//        adapter.stopListening()
    }
}