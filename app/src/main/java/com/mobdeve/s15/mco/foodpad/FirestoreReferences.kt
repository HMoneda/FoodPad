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
        private var commentRef : CollectionReference? = null

        const val USERS_COLLECTION = "users"
        const val RECIPES_COLLECTION = "recipes"
        const val COMMENT_COLLECTION = "comments"
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
        const val LIKES_FIELD = "likes"
        const val NUM_LIKES_FIELD = "numLikes"
        const val USER_FIELD = "userID"
        const val RECIPE_CLASSIFICATION_FIELD = "classification"
        const val COMMENT_RECIPE_ID_FIELD = "recipeID"
        const val COMMENT_TIMESTAMP_FIELD = "timestamp"


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

        fun getCommentCollectionReference() : CollectionReference{
            if(commentRef == null){
                commentRef = getFirestoreInstance().collection(COMMENT_COLLECTION)
            }
            return commentRef as CollectionReference
        }

        fun getUserByEmail(email : String) : Task<QuerySnapshot> {
            return getUserCollectionReference().whereEqualTo(EMAIL_FIELD, email).get()
        }

        fun getUserByUsername(username : String): Task<QuerySnapshot>{
            return getUserCollectionReference().whereEqualTo(USERNAME_FIELD, username).get()
        }

        fun getUserByID(userID : String) : Task<DocumentSnapshot>{
            return getUserCollectionReference().document(userID).get()
        }

        fun followUser(userID : String, followers : ArrayList<String>){
            getUserCollectionReference().document(userID).update(FOLLOWERS_FIELD, followers)
        }

        fun addFollowedUser(userID: String, followed : ArrayList<String>){
            getUserCollectionReference().document(userID).update(FOLLOWING_FIELD, followed)
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

        fun findUser(username : String) : Query{
            Log.d("TEST", username)
            return getUserCollectionReference().orderBy(USERNAME_FIELD).startAt(username).endAt(username + "\uf8ff")
        }

        fun addRecipe(newRecipe: Recipe) : Task<DocumentReference>{
            return getRecipeCollectionReference().add(newRecipe)
        }

        fun getRecipes(): Task<QuerySnapshot> {
            return getRecipeCollectionReference().get()
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

        fun findRecipe(recipeName : String) : Query {
            Log.d("TEST", recipeName)
            return getRecipeCollectionReference().orderBy(RECIPE_NAME_FIELD).startAt(recipeName).endAt(recipeName + "\uf8ff")
        }

        fun likeRecipe(recipeID: String, likes : ArrayList<String>){
            getRecipeCollectionReference().document(recipeID).update(LIKES_FIELD, likes)
            getRecipeCollectionReference().document(recipeID).update(NUM_LIKES_FIELD, likes.size)
        }

        fun getPopularRecipesQuery() : Query{
            return getRecipeCollectionReference().orderBy(NUM_LIKES_FIELD, Query.Direction.DESCENDING).limit(5)
        }

        fun addComment(newComment : Comment) : Task<DocumentReference>{
            return getCommentCollectionReference().add(newComment)
        }

        fun getCommentQuery(recipeID : String) : Query{
            return getCommentCollectionReference().whereEqualTo(COMMENT_RECIPE_ID_FIELD, recipeID).orderBy(COMMENT_TIMESTAMP_FIELD)
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

        fun getDefaultAvatar(): Task<Uri> {
            val path = "images/users/DefaultAvatar.png"
            return getStorageReferenceInstance().child(path).downloadUrl
        }

    }
}