package com.mobdeve.s15.mco.foodpad

import android.net.Uri
import android.service.autofill.FieldClassification
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Document

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
        const val PROFILE_URI_FIELD = "imgUri"
        const val FOLLOWING_FIELD = "following"
        const val FOLLOWERS_FIELD = "followers"
        const val RECIPE_NAME_FIELD = "recipeName"
        const val RECIPE_AUTHOR_FIELD = "author"
        const val NUM_COMMENTS_FIELD = "comments"
        const val NUM_LIKES_FIELD = "likes"
        const val USER_FIELD = "userID"
        const val RECIPE_CLASSIFICATION_FIELD = "classification"

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

        fun updateUserBio(uid : String, bio : String): Task<Void> {
            return getUserCollectionReference().document(uid).update(BIO_FIELD, bio)
        }

        suspend fun updateUserPhoto(uid: String, uri: Uri?){
            val path = generateUserProfilePath(uid)
            getStorageReferenceInstance().child(path).putFile(uri!!).await()

            val newUri = getStorageReferenceInstance().child(path).downloadUrl.await()
            getUserCollectionReference().document(uid).update(PROFILE_URI_FIELD, newUri.toString()).await()
        }

        fun getUserRecipesQuery(uid : String?) : Query{
            return getRecipeCollectionReference().whereEqualTo(USER_FIELD, uid)
        }

        fun addRecipe(newRecipe: Recipe) : Task<DocumentReference>{
            return getRecipeCollectionReference().add(newRecipe)
        }

        fun getRecipe(recipeID : String) : Task<DocumentSnapshot> {
            return getRecipeCollectionReference().document(recipeID).get()
        }

        fun updateRecipe(recipeID: String, recipe: Recipe) {
            getRecipeCollectionReference().document(recipeID).set(recipe)
        }

        fun deleteRecipe(recipeID: String){
            getRecipeCollectionReference().document(recipeID).delete()
        }

        fun findRecipe(recipeName : String) : Query{
            Log.d("TEST", recipeName)
            return getRecipeCollectionReference().orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
        }

        fun findRecipeByClassification(recipeName : String, classification : String) : Query {
            Log.d("TEST", recipeName)
            when (classification) {
                "Appetizer" -> return getRecipeCollectionReference().whereEqualTo(
                    RECIPE_CLASSIFICATION_FIELD, "Appetizer").orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
                "Main Course" -> return getRecipeCollectionReference().whereEqualTo(
                    RECIPE_CLASSIFICATION_FIELD, "Main Course").orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
                "Dessert" -> return getRecipeCollectionReference().whereEqualTo(
                    RECIPE_CLASSIFICATION_FIELD, "Dessert").orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
                "Beverage" -> return getRecipeCollectionReference().whereEqualTo(
                    RECIPE_CLASSIFICATION_FIELD, "Beverage").orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
            }
            return getRecipeCollectionReference().orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
        }


        fun likeRecipe(recipeID: String, likes : ArrayList<String>){
            getRecipeCollectionReference().document(recipeID).update(NUM_LIKES_FIELD, likes)
        }

        fun getPopularRecipesQuery() : Query{
            return getRecipeCollectionReference().orderBy(NUM_LIKES_FIELD, Query.Direction.DESCENDING).limit(5)
        }

        fun generateUserProfilePath(uid: String) : String{
            return "images/users/${uid}-ProfileAvatar"
        }

        fun generateRecipePhotoPath(uid : String, imageUri : Uri) : String{
            return "images/recipes/${uid}-${imageUri.lastPathSegment}"
        }

        suspend fun getRecipePhotoUri(imageUri : Uri, uid : String) : Uri{
            val path = generateRecipePhotoPath(uid, imageUri)
            getStorageReferenceInstance().child(path).putFile(imageUri).await()

            return getStorageReferenceInstance().child(path).downloadUrl.await()
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