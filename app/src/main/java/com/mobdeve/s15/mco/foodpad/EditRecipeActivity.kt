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
import androidx.core.view.forEach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.lang.Exception

class EditRecipeActivity : AppCompatActivity() {
    private lateinit var addProcedureButton : Button
    private lateinit var addIngredientButton : Button
    private lateinit var deleteRecipeBtn : Button
    private lateinit var saveBtn : Button
    private lateinit var backBtn : ImageButton
    private lateinit var ingredientLayout: LinearLayout
    private lateinit var procedureLayout: LinearLayout
    private lateinit var recipeNameET : EditText
    private lateinit var numServingsET : EditText
    private lateinit var totalTimeET : EditText
    private lateinit var recipeImg : ImageView
    private lateinit var editRecipeImgFAB : FloatingActionButton
    private lateinit var editClassificationSpinner : Spinner

    private val TAG = LogTags.CREATE_RECIPE_ACTIVITY.name

    private var imageUri : Uri? = null
    private var dataBinded = false
    private val classifications = arrayOf("Appetizer", "Main Course", "Dessert", "Beverage")

    private val activityResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                if (result.data != null) {
                    imageUri = result.data!!.data!!
                    Picasso.get().load(imageUri).into(recipeImg)
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
                    Picasso.get().load(imageUri).into(recipeImg)
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
        setContentView(R.layout.activity_edit_recipe)

        addIngredientButton = findViewById(R.id.addIngredientBtn)
        addProcedureButton = findViewById(R.id.addProcedureBtn)
        ingredientLayout = findViewById(R.id.ingredientsListLL)
        procedureLayout = findViewById(R.id.proceduresListLL)
        saveBtn = findViewById(R.id.saveBtn)
        recipeNameET = findViewById(R.id.usernameTV)
        numServingsET = findViewById(R.id.numServingsET)
        totalTimeET = findViewById(R.id.totalTimeET)
        recipeImg = findViewById(R.id.profileImage)
        editRecipeImgFAB = findViewById(R.id.editImageFAB)
        backBtn = findViewById(R.id.recipeBackBtn)
        deleteRecipeBtn = findViewById(R.id.deleteRecipeBtn)
        editClassificationSpinner = findViewById(R.id.editClassificationSpinner)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classifications)
        editClassificationSpinner.adapter = adapter

        editClassificationSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val recipeID = intent.getStringExtra(IntentKeys.RECIPE_ID_KEY.name)
        val uid = intent.getStringExtra(IntentKeys.RECIPE_AUTHOR_UID_KEY.name)
        val username = intent.getStringExtra(IntentKeys.USERNAME_KEY.name)
        val recipeImgUri = intent.getStringExtra(IntentKeys.RECIPE_IMG_URI_KEY.name)

        editRecipeImgFAB.setOnClickListener {
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

        addIngredientButton.setOnClickListener{
            val ingredientView = layoutInflater.inflate(R.layout.edittext_row_ingredient,null,false)
            val deleteIngredient : ImageButton = ingredientView.findViewById(R.id.deleteIngredientBtn)
            deleteIngredient.setOnClickListener {
                ingredientLayout.removeView(ingredientView)
            }
            ingredientLayout.addView(ingredientView)
        }

        addProcedureButton.setOnClickListener{
            val procedureView = layoutInflater.inflate(R.layout.edittext_row_procedure, null, false)
            val deleteProcedureBtn : ImageView = procedureView.findViewById(R.id.deleteProcedureBtn)

            deleteProcedureBtn.setOnClickListener{
                procedureLayout.removeView(procedureView)
            }

            procedureLayout.addView(procedureView)
        }

        saveBtn.setOnClickListener {
            if(!checkFields()){
                Log.d(TAG, "Recipe Saved!")
                val recipeName = recipeNameET.text.toString()
                val numServings = numServingsET.text.toString()
                val prepTime = totalTimeET.text.toString()
                var recipeImg : Uri? = null
                val classification = editClassificationSpinner.selectedItem.toString()

                val ingredients : ArrayList<Ingredient> = ArrayList()
                val procedures : ArrayList<String> = ArrayList()

                ingredientLayout.forEach { view ->
                    val qty = view.findViewById<EditText>(R.id.qtyET).text.toString()
                    val ingredient = view.findViewById<EditText>(R.id.bioET).text.toString()
                    val measurement = view.findViewById<EditText>(R.id.measurementET).text.toString()
                    ingredients.add(Ingredient(Integer.parseInt(qty),ingredient, measurement))
                }

                procedureLayout.forEach { view ->
                    val procedure = view.findViewById<EditText>(R.id.procedureET).text.toString()
                    procedures.add(procedure)
                }

                CoroutineScope(Dispatchers.IO).launch{
                    if(imageUri == null){
                        recipeImg = Uri.parse(recipeImgUri)
                    }else{
                        recipeImg = FirestoreReferences.getRecipePhotoUri(imageUri!!,uid!!)
                    }

                    Log.d(TAG, recipeName)
                    Log.d(TAG, numServings)
                    Log.d(TAG, prepTime)
                    Log.d(TAG, recipeImg.toString())
                    Log.d(TAG, ingredients.toString())
                    Log.d(TAG, procedures.toString())

                    val oldRecipe = FirestoreReferences.getRecipe(recipeID!!).await().toObject(Recipe::class.java)
                    val updatedRecipe = Recipe(recipeName, uid!!, username!!,
                        oldRecipe!!.likes,numServings,
                        Integer.parseInt(prepTime),ingredients,procedures,recipeImg.toString(),classification,
                        oldRecipe.likes.size)

                    FirestoreReferences.updateRecipe(recipeID, updatedRecipe)

                    withContext(Dispatchers.Main){
                        Toast.makeText(this@EditRecipeActivity, "Recipe Updated", Toast.LENGTH_LONG).show()
                        setResult(4)
                        finish()
                    }
                }
            }else{
                Toast.makeText(this,"Please fill up all the fields", Toast.LENGTH_LONG).show()
            }

        }

        deleteRecipeBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Recipe")
            builder.setMessage("Do you wish to delete your recipe?")
            builder.setPositiveButton("Delete"){ dialog, which ->
                dialog.dismiss()
                CoroutineScope(Dispatchers.IO).launch{
                    FirestoreReferences.deleteRecipe(recipeID!!)

                    withContext(Dispatchers.Main){
                        Toast.makeText(this@EditRecipeActivity, "Recipe Deleted", Toast.LENGTH_LONG).show()
                        setResult(3)
                        finish()

                    }
                }
            }

            builder.setNegativeButton("Cancel"){ dialog, which ->
                dialog.dismiss()
            }

            val dialog : AlertDialog = builder.create()
            dialog.show()
        }

        backBtn.setOnClickListener {
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
        val recipeID = intent.getStringExtra(IntentKeys.RECIPE_ID_KEY.name)
        getData(recipeID!!)
    }

    private fun getData(recipeID : String){
        CoroutineScope(Dispatchers.IO).launch {
            val recipe = FirestoreReferences.getRecipe(recipeID).await().toObject(Recipe::class.java)
            withContext(Dispatchers.Main){
                if(!dataBinded){
                    bindData(recipe!!)
                    dataBinded = true
                }
                getIngredientsAndProcedures(recipe!!)
            }
        }
    }

    private fun bindData(recipe: Recipe){
        recipeNameET.setText(recipe.recipeName)
        numServingsET.setText(recipe.servings)
        totalTimeET.setText(recipe.prepTime.toString())
        editClassificationSpinner.setSelection(classifications.indexOf(recipe.classification))
        Picasso.get().load(Uri.parse(recipe.recipeImg)).into(recipeImg)
    }

    private fun getIngredientsAndProcedures(recipe: Recipe){
        for(ingredient in recipe.ingredients){
            val ingredientView = layoutInflater.inflate(R.layout.edittext_row_ingredient,null,false)
            val deleteIngredient : ImageButton = ingredientView.findViewById(R.id.deleteIngredientBtn)
            ingredientView.findViewById<EditText>(R.id.qtyET).setText(ingredient.quantity.toString())
            ingredientView.findViewById<EditText>(R.id.bioET).setText(ingredient.ingredient)
            ingredientView.findViewById<EditText>(R.id.measurementET).setText(ingredient.measurement)
            deleteIngredient.setOnClickListener {
                if(ingredientLayout.childCount == 1){
                    Toast.makeText(this,"Recipe must at least have 1 ingredient", Toast.LENGTH_LONG).show()
                }else{
                    ingredientLayout.removeView(ingredientView)
                }
            }
            ingredientLayout.addView(ingredientView)
        }

        for(procedure in recipe.procedures){
            val procedureView = layoutInflater.inflate(R.layout.edittext_row_procedure, null, false)
            val deleteProcedureBtn : ImageView = procedureView.findViewById(R.id.deleteProcedureBtn)
            procedureView.findViewById<EditText>(R.id.procedureET).setText(procedure)
            deleteProcedureBtn.setOnClickListener{
                if(procedureLayout.childCount == 1){
                    Toast.makeText(this,"Recipe must at least have 1 procedure", Toast.LENGTH_LONG).show()
                }else{
                    procedureLayout.removeView(procedureView)
                }
            }
            procedureLayout.addView(procedureView)
        }
    }

    private fun checkFields() : Boolean{
        val servingsETEmpty = numServingsET.text.isEmpty()
        val totTimeETEmpty = totalTimeET.text.isEmpty()
        val recipeNameETEmpty = recipeNameET.text.isEmpty()
        val ingredientEmpty = checkIngredients()
        val procedureEmpty = checkProcedure()

        Log.d("CHECK_FIELDS", "servings: $servingsETEmpty")
        Log.d("CHECK_FIELDS", "total time: $totTimeETEmpty")
        Log.d("CHECK_FIELDS", "recipename: $recipeNameETEmpty")
        Log.d("CHECK_FIELDS", "ingredients: $ingredientEmpty")
        Log.d("CHECK_FIELDS", "procedure $procedureEmpty")

        val isEmpty = servingsETEmpty || totTimeETEmpty || recipeNameETEmpty || ingredientEmpty|| procedureEmpty

        Log.d("CHECK_FIELDS", "isEmpty: $isEmpty")

        return isEmpty
    }

    private fun checkIngredients() : Boolean{
        var notFilled = false
        ingredientLayout.forEach { view ->
            val qtyETEmpty = view.findViewById<EditText>(R.id.qtyET).text.isEmpty()
            val measurementETEmpty = view.findViewById<EditText>(R.id.measurementET).text.isEmpty()
            val ingredientETEmpty = view.findViewById<EditText>(R.id.bioET).text.isEmpty()

            if(qtyETEmpty || measurementETEmpty || ingredientETEmpty){
                notFilled = true
            }
        }
        return notFilled
    }

    private fun checkProcedure() : Boolean{
        var notFilled = false
        procedureLayout.forEach { view ->
            val procedureETEmpty = view.findViewById<EditText>(R.id.procedureET).text.isEmpty()
            if(procedureETEmpty){
                notFilled = true
            }
        }
        return notFilled
    }

    override fun onStop() {
        super.onStop()
        ingredientLayout.removeAllViews()
        procedureLayout.removeAllViews()
    }

}