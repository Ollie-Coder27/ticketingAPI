package com.example.homework4

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider

class viewEvent : AppCompatActivity() {

    private lateinit var viewModel: eventViewModel
    private lateinit var notificationArray: Array<String>

    @RequiresApi(value = 31)//Build.VERSION_CODES.O
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[eventViewModel::class.java]
        getData()
        //write to a file the info of a selected venue
        //clearFile
        //writeToFile
        viewModel.writeToFile(this)
        //viewModel.notificationCheck(this) // view that the file was cleared
        viewModel.setData(findViewById(android.R.id.content))

        notificationArray = viewModel.generateNotification(this)
        notificationService(this).createChannel()
        notificationService(this).showNotification(notificationArray[0], notificationArray[1])


    }

    private fun getData() {
        viewModel.eventName = intent.getStringExtra("name").toString()
        viewModel.eventADD = intent.getStringExtra("address").toString()
        viewModel.eventCity = intent.getStringExtra("city").toString()
        viewModel.eventState = intent.getStringExtra("state").toString()
        viewModel.eventDate = intent.getStringExtra("date").toString()
        viewModel.eventImage = intent.getStringExtra("image").toString()
        viewModel.eventTickets = intent.getStringExtra("tickets").toString()
        viewModel.eventPrice = intent.getStringExtra("price").toString()
        viewModel.eventVenue = intent.getStringExtra("venue").toString()
        val notice = "Data couldn't be reached, please try again or go to previous screen"
        if (viewModel.eventName.isEmpty()){
            viewModel.eventName = notice
        }
        if (viewModel.eventADD.isEmpty()){
            viewModel.eventName = notice
        }
        if (viewModel.eventCity.isEmpty()){
            viewModel.eventCity = notice
        }
        if (viewModel.eventState.isEmpty()){
            viewModel.eventState = notice
        }
        if (viewModel.eventDate.isEmpty()){
            viewModel.eventDate = notice
        }
        if (viewModel.eventImage.isEmpty()){
            viewModel.eventImage = notice
        }
        if (viewModel.eventTickets.isEmpty()){
            viewModel.eventTickets = notice
        }
        if (viewModel.eventPrice.isEmpty()){
            viewModel.eventPrice = notice
        }
        if (viewModel.eventVenue.isEmpty()){
            viewModel.eventVenue = notice
        }
    }

    //Clickable
    fun buyTickets(view: View) {
        viewModel.buyTicketsVM(view)
    }

    fun openAddress(ignoredView: View) {
        viewModel.openAddressVM(this)
    }

    fun returnToResults(ignoredView: View) {
        finish()
    //onDestroy()
        //super.onResume()
    }
}

