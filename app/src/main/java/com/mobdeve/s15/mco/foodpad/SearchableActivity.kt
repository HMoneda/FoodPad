package com.mobdeve.s15.mco.foodpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class SearchableActivity : AppCompatActivity() {
    private lateinit var filterClassificationSpinner : Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchable)

        filterClassificationSpinner = findViewById(R.id.filterClassificationSpinner)
        val filters = arrayOf("All", "Appetizer", "Main Course", "Dessert", "Beverage", "User")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filters)
        filterClassificationSpinner.adapter = adapter

        filterClassificationSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }
}