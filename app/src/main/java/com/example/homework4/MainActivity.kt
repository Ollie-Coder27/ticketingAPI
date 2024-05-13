/**
 * Oliver Calandra | Ticket Master Final Project | 05.09.2024 | CS 414-02
 */

package com.example.homework4

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log.i
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val sort1 = "date,asc"
    private val sort2 = "date,desc"
    private val sort3 = "name,asc"
    private val sort4 = "name,desc"
    private val sort5 = "relevance,asc"
    private val sort6 = "relevance,desc"
    private lateinit var sortSpinner:Spinner

    private val FILE_NAME = "searchData"

    private var sortSend = sort1

    private var keyWord = ""
    private var city = ""
    private var returnKey = "sending"
    private var returnKeyWant = "returning"
    private lateinit var local: Location

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Size Spinner
        val sortList = listOf(sort1, sort2, sort3, sort4, sort5,sort6)
        val sizeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortList)
        sortSpinner = findViewById(R.id.sortSpinner)
        sortSpinner.adapter = sizeAdapter
        sortSpinner.onItemSelectedListener = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission(this)
        }
        //create Notification Channel
        notificationService(this).createChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //this.OnClickLogin(findViewById(R.id.button_id))
            locationPermission(this)
        }
    }



// Check if notification permision is present, if not it will enter the if statement and request it
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun notificationPermission(context: Context){
    val permission = android.Manifest.permission.POST_NOTIFICATIONS
    if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this, arrayOf(permission),100)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
// Check if location permision is present, if not it will enter the if statement and request it
private fun locationPermission(context: Context){
    val permissionLocal = android.Manifest.permission.ACCESS_FINE_LOCATION
    if(ContextCompat.checkSelfPermission(context, permissionLocal) != PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this, arrayOf(permissionLocal), 100)
    }else{
        Toast.makeText(context, "Location is on", Toast.LENGTH_LONG).show()
    }
}
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
// Check if location permision is present, if not it will enter the if statement and request it
fun locationPermissionBtn(view: View){
    locationPermission(this)
}
    fun search(ignoredView: View){
        keyWord = findViewById<EditText>(R.id.KeywordSearch).text.toString()
        city = findViewById<EditText>(R.id.LocationSearch).text.toString()

        if ((keyWord.isNotEmpty()) && (city.isNotEmpty())){
            i("Check", keyWord)
            i("Check", city)
            val myIntent = Intent(this, results::class.java)

            saveData()

            /*
            Homework 4 implementation
            myIntent.putExtra("keyword", keyWord).toString()
            myIntent.putExtra("location", city).toString()
            myIntent.putExtra("sort", sortSend).toString()
            */

            startActivity(myIntent)
        }else{
            dialogBuild(keyWord, city)
        }


    }

    private fun saveData(){
        val sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("keyword", keyWord)
        editor.putString("location", city)
        editor.putString("sort", sortSend)
        editor.putString("returnKey", returnKey)
        editor.apply()
    }

    private fun getData(){
        val sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE)
        val returnKey = sharedPreferences.getString("returnKey","")
        if(returnKey == returnKeyWant){
            val tempKey = sharedPreferences.getString("keyword", "")
            val tempCity = sharedPreferences.getString("location", "")
            val tempSort = sharedPreferences.getString("sort", "")

            findViewById<EditText>(R.id.KeywordSearch).setText(tempKey)
            findViewById<EditText>(R.id.LocationSearch).setText(tempCity)
            if (tempSort != null) {
                sortSend = tempSort
            }
        }
    }

    fun newSearch(ignoredView: View){
        findViewById<EditText>(R.id.KeywordSearch).setText("")
        findViewById<EditText>(R.id.LocationSearch).setText("")
        sortSend = sort1
    }


    private fun dialogBuild(term1: String, term2: String){
        var messagePrint = ""

        if(term1.isEmpty()){
            messagePrint = "Please enter a keyword"
        }
        if(term2.isEmpty()){
            messagePrint = "Please enter a location"
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Missing data!")
        builder.setMessage(messagePrint)

        builder.setPositiveButton("OK"){ dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (view != null) {
            sortSend = parent?.getItemAtPosition(position).toString()
            //i("sort", sortSend)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "Default sort set", Toast.LENGTH_SHORT).show()
    }

}