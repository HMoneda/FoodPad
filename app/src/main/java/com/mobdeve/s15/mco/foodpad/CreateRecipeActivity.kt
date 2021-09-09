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

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var addProcedureButton : Button
    private lateinit var addIngredientButton : Button
    private lateinit var saveBtn : Button
    private lateinit var backBtn : ImageButton
    private lateinit var ingredientLayout: LinearLayout
    private lateinit var procedureLayout: LinearLayout
    private lateinit var recipeNameET : EditText
    private lateinit var numServingsET : EditText
    private lateinit var totalTimeET : EditText
    private lateinit var recipeImg : ImageView
    private lateinit var editRecipeImgFAB : FloatingActionButton
    private lateinit var classificationSpinner: Spinner

    private val TAG = LogTags.CREATE_RECIPE_ACTIVITY.name

    private var imageUri : Uri? = null

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
        setContentView(R.layout.activity_create_recipe)

        addIngredientButton = findViewById(R.id.addIngredientBtn)
        addProcedureButton = findViewById(R.id.addProcedureBtn)
        ingredientLayout = findViewById(R.id.ingredientsListLL)
        procedureLayout = findViewById(R.id.proceduresListLL)
        saveBtn = findViewById(R.id.saveBtn)
        recipeNameET = findViewById(R.id.createRecipeUsernameTV)
        numServingsET = findViewById(R.id.numServingsET)
        totalTimeET = findViewById(R.id.totalTimeET)
        recipeImg = findViewById(R.id.profileImage)
        editRecipeImgFAB = findViewById(R.id.editImageFAB)
        backBtn = findViewById(R.id.recipeBackBtn)
        classificationSpinner = findViewById(R.id.classificationSpinner)

        val classifications = arrayOf("Appetizer", "Main Course", "Dessert", "Beverage")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, classifications)
        classificationSpinner.adapter = adapter

        classificationSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        val uid = intent.getStringExtra(IntentKeys.RECIPE_AUTHOR_UID_KEY.name)
        val username = intent.getStringExtra(IntentKeys.USERNAME_KEY.name)
        Log.d(TAG, uid!!)
        Log.d(TAG, username!!)
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

        editRecipeImgFAB.setOnClickListener {
            val i = Intent()
            i.apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
            }
            activityResultLauncher.launch(Intent.createChooser(i, "Select Picture"))
        }

        saveBtn.setOnClickListener {
            Log.d(TAG, "Recipe Saved!")
            val recipeName = recipeNameET.text.toString()
            val numServings = numServingsET.text.toString()
            val prepTime = totalTimeET.text.toString()
            var recipeImg : Uri? = null
            val classification = classificationSpinner.selectedItem.toString()

            val ingredients : ArrayList<Ingredient> = ArrayList()
            val procedures : ArrayList<String> = ArrayList()

            ingredientLayout.forEach { view ->
                val qty = view.findViewById<EditText>(R.id.qtyET).text.toString()
                val ingredient = view.findViewById<EditText>(R.id.bioET).text.toString()
                val measurement = view.findViewById<EditText>(R.id.measurementET).text.toString()
                ingredients.add(Ingredient(Integer.parseInt(qty),ingredient, measurement))
            }

            procedureLayout.forEach { view ->
                val procedure = view.findViewById<EditText>(R.id.qtyET).text.toString()
                procedures.add(procedure)
            }

            CoroutineScope(Dispatchers.IO).launch{
                if(imageUri == null){
                    recipeImg = FirestoreReferences.getDefaultAvatar().await()
                }else{
                    recipeImg = FirestoreReferences.getRecipePhotoUri(imageUri!!, uid)
                }

                Log.d(TAG, recipeName)
                Log.d(TAG, numServings)
                Log.d(TAG, prepTime)
                Log.d(TAG, recipeImg.toString())
                Log.d(TAG, ingredients.toString())
                Log.d(TAG, procedures.toString())


                val newRecipe = Recipe(recipeName, uid, username,0,0,numServings,
                    Integer.parseInt(prepTime),ingredients,procedures,recipeImg.toString(), classification)

                FirestoreReferences.addRecipe(newRecipe)

                withContext(Dispatchers.Main){
                    Toast.makeText(this@CreateRecipeActivity, "Recipe Created", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }

        backBtn.setOnClickListener {
            finish()
        }
    }
}