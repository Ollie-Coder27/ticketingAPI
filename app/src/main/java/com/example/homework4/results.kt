package com.example.homework4

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log.*
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class results : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private val BASE_URL = "https://app.ticketmaster.com/"
    val eventList = ArrayList<Event>()
    private lateinit var key: String
    private lateinit var local:String
    private lateinit var  sort:String
    private val FILE_NAME = "searchData"
    private val returnKey = "returning"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_results)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*key = intent.getStringExtra("keyword").toString()
        local = intent.getStringExtra("location").toString()
        sort = intent.getStringExtra("sort").toString()*/

        getData()

        i("Key", key)
        i("Key", local)
        recycler = findViewById(R.id.viewResults)
        if ((key != "null") && (local != "null")) {
            i("Notification", "Get events entered")
                getEvents(key, local, sort)
        }
    }

    private fun getEvents(keyWord: String, location: String, sortSend: String){
        i("Method Entered", "getEvents method entered")
        val adapter = MyRecyclerAdapter(eventList)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val eventAPI = retrofit.create(EventService::class.java)

        eventAPI.getEvents(sortSend, keyWord, location).enqueue(object : Callback<TicketData>{
        //eventAPI.getEventsOG(keyWord, location).enqueue(object : Callback<TicketData>{

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<TicketData>, response: Response<TicketData>) {
               i("Method Entered", "onResponse method entered")
                // Get access to the body with response.body().
                val body = response.body()
                //check if response is valid
                if ((body == null)) {
                    w(TAG, "Valid response was not received")
                    val title = "Error in Loading Data"
                    val mess = "Let's return to the search and try again, \nremember to only enter a city with your event"
                    dialogBuild(title,mess, 0)
                    return
                }else if((body.page.totalElements!! <= 0)){
                    w(TAG, "No data Available")
                    val title = "No Available Data"
                    val mess = "There are no events with that description, sorry"
                    dialogBuild(title,mess, 0)
                    return
                }else{
                    i("Log", "name : ${body._embedded.events[0].name}")
                    i("Log", "image URL : ${body._embedded.events[0].images?.get(0)?.url}")
                    i("Log", "time : ${body._embedded.events[0].dates?.start?.localTime}")
                    i("Log", "date : ${body._embedded.events[0].dates?.start?.localDate}")
                    i("Log", "venue name : ${body._embedded.events[0]._embedded?.venues?.get(0)?.name}")
                    i("Log", "venue city : ${body._embedded.events[0]._embedded?.venues?.get(0)?.city?.name}")
                    i("Log", "venue state : ${body._embedded.events[0]._embedded?.venues?.get(0)?.state?.stateCode}")
                    i("Log", "venue address : ${body._embedded.events[0]._embedded?.venues?.get(0)?.address?.line1}")
                    i("Log", "url : ${body._embedded.events.get(0).url}")
                    i("Log", "price : ${body._embedded.events[0].priceRanges?.get(0)?.min} to ${body._embedded.events[0].priceRanges?.get(0)?.max}")

                    eventList.addAll(body._embedded.events)
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<TicketData>, t: Throwable) {
                d(TAG, "onFailure : $t")
            }

        })
    }

    private fun getData(){
        val sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE)

        key = sharedPreferences.getString("keyword", "").toString()
        local = sharedPreferences.getString("location", "").toString()
        sort = sharedPreferences.getString("sort", "").toString()

    }

    private fun saveData(){
        val sharedPreferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("keyword", key)
        editor.putString("location", local)
        editor.putString("sort", sort)
        editor.putString("returnKey", returnKey)
        editor.apply()
    }

    fun returnToSearch(ignoredView: View) {
        //add data to the shared preferences
        saveData()
        i("NOTICE", "Return To Search Entered")
        //Close our this activity and restart the previous
        finish()
        super.onResume()
    }

    //restart main activity on failure search
    private fun returnRestart(){
        val myIntent = Intent(this, MainActivity::class.java)
        i("NOTICE", "Return Restart Entered - error")
        startActivity(myIntent)
        finish()
    }

    fun returnRestart(ignoredView: View){
        val myIntent = Intent(this, MainActivity::class.java)
        i("NOTICE", "Return Restart Entered - New Search")
        startActivity(myIntent)
        finish()
    }

    fun viewInfo(ignoredView: View){
        val title = "FAQs"
        val mess = "1. Prices are subject to change and can vary." +
                "\n2. Some websites are not active, so a link to google is provided instead." +
                "\n3. TBD could means that those event details have not yet been provided."
        dialogBuild(title,mess,1)
    }

    fun dialogBuild(title: String, message: String, type: Int){
    // 0 = leave activity | 1 = just dismiss dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton("Dismiss"){ dialog, _ ->
            if(type == 0){returnRestart()}
            dialog.dismiss()

        }
        val dialog = builder.create()
        dialog.show()
    }
}

