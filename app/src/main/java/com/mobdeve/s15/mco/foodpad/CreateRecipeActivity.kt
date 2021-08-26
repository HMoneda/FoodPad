package com.mobdeve.s15.mco.foodpad

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var addProcedureButton : Button
    private lateinit var addIngredientButton : Button
    private lateinit var ingredientLayout: LinearLayout
    private lateinit var procedureLayout: LinearLayout
    private lateinit var deleteIngredientButton : ImageButton
    private lateinit var deleteProcedureButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        addIngredientButton = findViewById(R.id.addIngredientBtn)
        addProcedureButton = findViewById(R.id.addProcedureBtn)
        ingredientLayout = findViewById(R.id.ingredientsListLL)
        procedureLayout = findViewById(R.id.proceduresListLL)
        deleteIngredientButton = findViewById(R.id.deleteIngredientBtn)
        deleteProcedureButton = findViewById(R.id.deleteProcedureBtn)

        addIngredientButton.setOnClickListener{
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row : View = inflater.inflate(R.layout.row_ingredient, null)
            ingredientLayout!!.addView(row, ingredientLayout!!.childCount - 1)
        }

        addProcedureButton.setOnClickListener{
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val row : View = inflater.inflate(R.layout.row_procedure, null)
            procedureLayout!!.addView(row, procedureLayout!!.childCount - 1)
        }

    }
}