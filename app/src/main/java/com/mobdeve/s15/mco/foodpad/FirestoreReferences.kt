package com.mobdeve.s15.mco.foodpad

import android.net.Uri
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

class FirestoreReferences {
    companion object{
        private var db : FirebaseFirestore? = null
        private var storage : StorageReference? = null
        private var usersRef : CollectionReference? = null
        private var recipeRef : CollectionReference? = null

        const val USERS_COLLECTION = "users"
        const val RECIPES_COLLECTION = "recipes"
        const val EMAIL_FIELD = "email"
        const val USERNAME_FIELD = "username"
        const val FOLLOWER_COUNT_FIELD = "followerCount"
        const val BIO_FIELD = "bio"
        const val FOLLOWING_FIELD = "following"
        const val FOLLOWERS_FIELD = "followers"
        const val RECIPE_NAME_FIELD = "name"
        const val RECIPE_AUTHOR_FIELD = "author"
        const val NUM_COMMENTS_FIELD = "comments"
        const val NUM_LIKES_FIELD = "likes"
        const val USER_FIELD = "user"

        fun getFirestoreInstance() : FirebaseFirestore{
            if(db == null){
                db = FirebaseFirestore.getInstance()
            }
            return db as FirebaseFirestore
        }

        fun getStorageReferenceInstance() : StorageReference{
            if(storage == null){
                storage = FirebaseStorage.getInstance().reference
            }
            return storage as StorageReference
        }

        fun getUserCollectionReference() : CollectionReference{
            if(usersRef == null){
                usersRef = getFirestoreInstance().collection(USERS_COLLECTION)
            }
            return usersRef as CollectionReference
        }

        fun getRecipeCollectionReference() : CollectionReference{
            if(recipeRef == null){
                recipeRef = getFirestoreInstance().collection(RECIPES_COLLECTION)
            }
            return  recipeRef as CollectionReference
        }

        fun getUserByEmail(email : String) : Task<QuerySnapshot> {
            return getUserCollectionReference().whereEqualTo(EMAIL_FIELD, email).get()
        }

        fun getUserByUsername(username : String): Task<QuerySnapshot>{
            return getUserCollectionReference().whereEqualTo(USERNAME_FIELD, username).get()
        }

        fun getUserByEmailQuery(email : String?) : Query{
            return getUserCollectionReference().whereEqualTo(EMAIL_FIELD, email)
        }

        fun addUser(newUser : User): Task<DocumentReference> {
            return getUserCollectionReference().add(newUser)
        }

        fun getUserRecipesQuery(uid : String?) : Query{
            return  getRecipeCollectionReference().whereEqualTo(USER_FIELD, uid)
        }

        fun generateUserPhotoPath(uid: String, imageUri : Uri) : String{
            return "images/users/${uid}-${imageUri.lastPathSegment}"
        }

        fun getDefaultAvatar(): Task<Uri> {
            val path = "images/users/DefaultAvatar.png"
            return getStorageReferenceInstance().child(path).downloadUrl
        }
    }
}