package com.mobdeve.s15.mco.foodpad

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.forEach
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    var dataBinded = false

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
        totalTimeET = findViewById(R.id.bioET)
        recipeImg = findViewById(R.id.profileImage)
        editRecipeImgFAB = findViewById(R.id.editImageFAB)
        backBtn = findViewById(R.id.recipeBackBtn)
        deleteRecipeBtn = findViewById(R.id.deleteRecipeBtn)
        editClassificationSpinner = findViewById(R.id.editClassificationSpinner)

        val classifications = arrayOf("Appetizer", "Main Course", "Dessert", "Beverage")
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
            val i = Intent()
            i.apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
            }
            activityResultLauncher.launch(Intent.createChooser(i, "Select Picture"))
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
            Log.d(TAG, "Recipe Saved!")
            val recipeName = recipeNameET.text.toString()
            val numServings = numServingsET.text.toString()
            val prepTime = totalTimeET.text.toString()
            var recipeImg : Uri? = null

            val ingredients : ArrayList<Ingredient> = ArrayList()
            val procedures : ArrayList<String> = ArrayList()

            ingredientLayout.forEach { view ->
                val qty = view.findViewById<EditText>(R.id.qtyET).text.toString()
                val ingredient = view.findViewById<EditText>(R.id.bioET).text.toString()
                ingredients.add(Ingredient(Integer.parseInt(qty),ingredient))
            }

            procedureLayout.forEach { view ->
                val procedure = view.findViewById<EditText>(R.id.qtyET).text.toString()
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


                val updatedRecipe = Recipe(recipeName, uid!!, username!!,0,0,numServings,
                    Integer.parseInt(prepTime),ingredients,procedures,recipeImg.toString())

                FirestoreReferences.updateRecipe(recipeID!!, updatedRecipe)

                withContext(Dispatchers.Main){
                    Toast.makeText(this@EditRecipeActivity, "Recipe Updated", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        deleteRecipeBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                FirestoreReferences.deleteRecipe(recipeID!!)

                withContext(Dispatchers.Main){
                    Toast.makeText(this@EditRecipeActivity, "Recipe Deleted", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
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
            val recipe = FirestoreReferences.getRecipe(recipeID!!).await().toObject(Recipe::class.java)
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
        Picasso.get().load(Uri.parse(recipe.recipeImg)).into(recipeImg)
    }

    private fun getIngredientsAndProcedures(recipe: Recipe){
        for(ingredient in recipe.ingredients){
            val ingredientView = layoutInflater.inflate(R.layout.edittext_row_ingredient,null,false)
            val deleteIngredient : ImageButton = ingredientView.findViewById(R.id.deleteIngredientBtn)
            ingredientView.findViewById<EditText>(R.id.qtyET).setText(ingredient.quantity.toString())
            ingredientView.findViewById<EditText>(R.id.bioET).setText(ingredient.ingredient)
            deleteIngredient.setOnClickListener {
                ingredientLayout.removeView(ingredientView)
            }
            ingredientLayout.addView(ingredientView)
        }

        for(procedure in recipe.procedures){
            val procedureView = layoutInflater.inflate(R.layout.edittext_row_procedure, null, false)
            val deleteProcedureBtn : ImageView = procedureView.findViewById(R.id.deleteProcedureBtn)
            procedureView.findViewById<EditText>(R.id.qtyET).setText(procedure)
            deleteProcedureBtn.setOnClickListener{
                procedureLayout.removeView(procedureView)
            }
            procedureLayout.addView(procedureView)
        }
    }

    override fun onStop() {
        super.onStop()
        ingredientLayout.removeAllViews()
        procedureLayout.removeAllViews()
    }

}