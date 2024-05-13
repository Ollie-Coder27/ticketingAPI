package com.example.homework4

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.util.Log.i
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter

// Reference for view model : https://www.youtube.com/watch?v=5d7U3DUxsDo&t=413s
class eventViewModel: ViewModel() {

    var eventName = ""
    var eventADD = ""
    var eventCity = ""
    var eventState = ""
    var eventDate = ""
    var eventImage = ""
    var eventTickets = ""
    var eventPrice = ""
    var eventVenue = ""

    private val notificationItems: MutableList<String> = ArrayList()
    private val notificationItem: MutableList<String> = ArrayList()
    private val token = '|'
    private val FILE_NAME = "events.txt"

    fun setData(view: View){

        view.findViewById<TextView>(R.id.eventNameView).text = eventName

        Glide.with(view)
            .load(eventImage)
            .placeholder(R.drawable.logo)
            .override(300,400)
            .centerCrop()
            .circleCrop()
            .into(view.findViewById(R.id.eventImageView))

        view.findViewById<TextView>(R.id.eventTimeView).text = eventDate
        view.findViewById<TextView>(R.id.priceView).text = eventPrice
        view.findViewById<Button>(R.id.ticketsView).contentDescription = eventTickets

        i("Log", "Name: $eventName")
        i("Log", "Address: $eventADD")
        i("Log", "City: $eventCity")
        i("Log", "State: $eventState")
        i("Log", "Date: $eventDate")
        i("Log", "Image: $eventImage")
        i("Log", "Tickets: $eventTickets")
        i("Log", "Price: $eventPrice")
        i("Log", "Venue: $eventVenue")
        //i("Log", "Tickets 2: ${view.findViewById<Button>(R.id.ticketsView).contentDescription}")
    }

    private fun openTickets(url: String, v: View){
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        v.context.startActivity(browserIntent)
    }

    private fun dialogBuild(title: String, message: String, context: Context, google: Int){

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        //go to google maps activity
        if(google == 0){
            builder.setPositiveButton("Open Maps"){ dialog, _ ->

                val myIntent = Intent(context, MapsActivity::class.java)
                val addressWhole = "$eventADD, $eventCity $eventState"
                myIntent.putExtra("Address",addressWhole)
                context.startActivity(myIntent)

            }
            builder.setNeutralButton("Dismiss"){dialog, _ ->
                dialog.dismiss()
            }
        }else{
            builder.setPositiveButton("Dismiss"){dialog, _ ->
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun dialogBuild(title: String, message: String, url: String, v: View){
        val builder = AlertDialog.Builder(v.context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton("Dismiss"){ dialog, _ ->
            openTickets(url, v.findViewById(android.R.id.content))
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    //Clickable
    fun buyTicketsVM(view: View){
        val checkURL = "https://www.google.com/"
        val url = view.findViewById<Button>(R.id.ticketsView).contentDescription.toString()
        i("URL Log", url)
        if(url == checkURL){
            //call dialog acknowledging that the tickets are unavailable
            val title = "Acknowledgement"
            val mess = "The current website you are trying to reach is unavailable, an alternate has been provided, good luck!"
            dialogBuild(title, mess, url, view)
        }
        openTickets(url, view)
    }

    fun openAddressVM(context: Context){
        val title = "Venue information:"
        val venueInfo = "$eventVenue\n$eventADD, $eventCity, $eventState\n"
        val permissionLocal = android.Manifest.permission.ACCESS_FINE_LOCATION
        if(ContextCompat.checkSelfPermission(context, permissionLocal) == PackageManager.PERMISSION_GRANTED){

            dialogBuild(title, venueInfo, context,0)
        }else{
            Toast.makeText(context, "Please allow Location Permission", Toast.LENGTH_LONG).show()
            dialogBuild(title, venueInfo, context,1)

        }
    }

    //gather data and get a notification prepared
    fun generateNotification(context: Context): Array<String> {
        //prep
        val dataRead = getFileData(context)
        i("Data Read", dataRead)
        var count = 0
        val delim = '~'
        var tempRead = ""
        var temp = ""

        while (count <= dataRead.length-1) {
            if(dataRead[count] == token){
                notificationItems.add(tempRead)
                tempRead = ""
                count++
            }else{
                tempRead += dataRead[count]
                count++
            }

        }

        //i("readWordTEMP", tempRead)
        count = 0

        /*
        while (count <= notificationItems.size - 1) {
            i("ReadArray", "${notificationItems[count]}\n")
            count++
        }
        */

        val item = notificationItems[notificationItems.size-1]

        while(count <= item.length-1){
            if(item[count]!= delim){
                temp += item[count]
            }else{
                notificationItem.add(temp)
                temp = ""
            }
            count++
        }

        count = 0

        while (count <= notificationItem.size - 1) {
            i("ReadArray", "${notificationItem[count]}\n")
            count++
        }

        //set up notification here
        val name = "TicketMaster"
        val content = "${notificationItem[0]} \nLocation : ${notificationItem[2]}, ${notificationItem[3]}"
        i("Look", content)
        return arrayOf(name, content)

    }

    //Clears 500 lines of the spaces of the file
    fun clearFile(context: Context) {
        val write = OutputStreamWriter(context.openFileOutput(FILE_NAME, AppCompatActivity.MODE_PRIVATE))

        try {
            i("NOTIFICATION", "Erase file data Begun")
            for (i in 1..5000) {
                write.write("")
            }
            write.close()
        } catch (e: FileNotFoundException) {
            i("Error", e.stackTrace.toString())
            i("FileNotFoundException", "Perception : 74")
        } catch (e: Error) {
            i("Error", e.stackTrace.toString())
            i("SecurityException", "Sneak : 90")
        }

    }

    fun notificationCheck(context: Context) {
        val dataCheck = getFileData(context)
        i("View File", dataCheck)
    }

    //Reference : https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android
    fun writeToFile(context: Context) {
        try {
            val write = OutputStreamWriter(context.openFileOutput(FILE_NAME, AppCompatActivity.MODE_APPEND))
            i("NOTIFICATION", "Write to file Begun")
            write.append("${eventName}~")
            write.append("${eventADD}~")
            write.append("${eventCity}~")
            write.append("${eventState}~")
            write.append("${eventDate}~")
            write.append("${eventImage}~")
            write.append("${eventTickets}~")
            write.append("${eventPrice}~")
            write.append("${eventVenue}~" + token)
            write.close()

        } catch (e: FileNotFoundException) {
            i("Error", e.stackTrace.toString())
            i("FileNotFoundException", "Perception : 87")
        } catch (e: SecurityException) {
            i("Error", e.stackTrace.toString())
            i("SecurityException", "Athletics : 90")
        } catch (e: Error) {
            i("Error", e.stackTrace.toString())
            i("SecurityException", "Sneak : 90")
        }
    }

    private fun getFileData(context: Context): String {
        var data = ""
        try {
            val input = InputStreamReader(context.openFileInput(FILE_NAME))
            val bufferedReader = BufferedReader(input)
            var temp = bufferedReader.readLine()

            if (temp != null) {
                val strbuild: StringBuilder = java.lang.StringBuilder()
                while (temp != null) {
                    strbuild.append("\n").append(temp)
                    temp = bufferedReader.readLine()
                }
                data = strbuild.toString()
            }
            input.close()

        } catch (e: FileNotFoundException) {
            i("Error", e.stackTrace.toString())
            i("FileNotFoundException", "Perception : 128")
        } catch (e: IOException) {
            i("Error", e.stackTrace.toString())
            i("IOException", "Slight of Hand : 128")
        } catch (e: Error) {
            i("Error", e.stackTrace.toString())
            i("Error", "Sneak : 128")

        }

        i("DATA CHECK", data)

        return data
    }


}