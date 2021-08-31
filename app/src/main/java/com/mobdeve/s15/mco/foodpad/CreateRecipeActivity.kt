package com.mobdeve.s15.mco.foodpad

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.view.children

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var addProcedureButton : Button
    private lateinit var addIngredientButton : Button
    private lateinit var saveBtn : Button
    private lateinit var ingredientLayout: LinearLayout
    private lateinit var procedureLayout: LinearLayout
    private lateinit var recipeNameET : EditText
    private lateinit var numServingsET : EditText
    private lateinit var totalTimeET : EditText
    private lateinit var deleteIngredientButton : ImageButton
    private lateinit var deleteProcedureButton : ImageButton

    private val TAG = LogTags.CREATE_RECIPE_ACTIVITY.name

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


        addIngredientButton.setOnClickListener{
            val ingredientView = layoutInflater.inflate(R.layout.row_ingredient,null,false)
            val deleteIngredient : ImageButton = ingredientView.findViewById(R.id.deleteIngredientBtn)
            deleteIngredient.setOnClickListener {
                ingredientLayout.removeView(ingredientView)
            }
            ingredientLayout.addView(ingredientView)
        }

        addProcedureButton.setOnClickListener{
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row : View = inflater.inflate(R.layout.row_procedure, null)
            procedureLayout.addView(row, procedureLayout.childCount - 1)
        }

        saveBtn.setOnClickListener {
            Log.d(TAG, "RECIPE NAME: ${recipeNameET.text}")
            Log.d(TAG, "NUM OF SERVINGS: ${numServingsET.text}")
            Log.d(TAG, "TOTAL TIME: ${totalTimeET.text}")
            Log.d(TAG, ingredientLayout.getChildAt(0).findViewById<EditText>(R.id.qtyET).text.toString())
        }

    }
}