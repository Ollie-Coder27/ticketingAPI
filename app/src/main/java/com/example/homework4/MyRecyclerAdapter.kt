/** NOTE
 * The recycler view with swipe to refresh was used as reference
 */

package com.example.homework4


import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

//import kotlinx.coroutines.flow.internal.NoOpContinuation.context
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context
//import android.net.Uri

internal const val TAG = "MyRecyclerAdapter"

//abstract is temp, get rid of - 03.19.2024 - DONE
//, clickListener: ClickListener
class MyRecyclerAdapter(private val events: ArrayList<Event>) :
    RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>() {

    private var count = 0
    private var dateTime = ""
    private var imSend: String = ""
    private var priceSend: String = ""


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleName: TextView = itemView.findViewById(R.id.eventTitle)
        val address: TextView = itemView.findViewById(R.id.eventAddress)
        val city: TextView = itemView.findViewById(R.id.eventCity)
        val state: TextView = itemView.findViewById(R.id.eventState)
        val date: TextView = itemView.findViewById(R.id.dateAndTime)
        val image: ImageView = itemView.findViewById(R.id.eventImage)
        val price: TextView = itemView.findViewById(R.id.priceRange)
        val venName: TextView = itemView.findViewById(R.id.venueName)


        init {
            itemView.setOnClickListener {
                val selected = adapterPosition
                //Toast.makeText(itemView.context, "You clicked on $selected", Toast.LENGTH_SHORT).show()

                //Toast.makeText(itemView.context, "Context : ${events[adapterPosition].name}", Toast.LENGTH_LONG).show()
                val myIntent = Intent(itemView.context, viewEvent::class.java)

                myIntent.putExtra("name", events[selected].name.toString())
                myIntent.putExtra(
                    "address",
                    events[selected]._embedded?.venues!![0].address?.line1.toString()
                )
                myIntent.putExtra("city", "${events[selected]._embedded?.venues!![0].city?.name}")
                myIntent.putExtra(
                    "state",
                    "${events[selected]._embedded?.venues?.get(0)?.state?.stateCode}"
                )
                myIntent.putExtra("tickets", "${events[selected].url}")
                myIntent.putExtra("venue", "${events[selected]._embedded?.venues?.get(0)?.name}")

                //Get current event date and time
                var dateAndTime = ""
                if (events[selected].dates?.start?.localDate?.isNotEmpty() == true) {
                    val date =
                        events[selected].dates?.start?.localDate?.let { it1 -> convertDate(it1) }
                    dateAndTime += date
                }
                if (events[selected].dates?.start?.localTime?.isNotEmpty() == true) {
                        val time =
                            events[selected].dates?.start?.localTime?.let { it1 -> convertTime(it1) }

                        dateAndTime += " @: $time"
                }
                if (dateAndTime.length != 0){
                    myIntent.putExtra("date", dateAndTime)
                }else{
                    myIntent.putExtra("date", "Date and Time")

                }



                //Get current event pricing
                val max = events[selected].priceRanges?.get(0)?.max?.toInt()
                val min = events[selected].priceRanges?.get(0)?.min?.toInt()
                if (min != null) {
                    if (max != null) {
                        priceSend = ticketPrice(min, max)
                    }
                }else{
                    priceSend = "TBD"
                }

                if (priceSend.length != 0){
                    myIntent.putExtra("price", priceSend)
                }else{
                    myIntent.putExtra("price", "Price")

                }

                //Get current event image
                if (events[selected].images != null) {
                    val imageMax = events[selected].images?.maxByOrNull {
                        it.width!!.toInt() * it.height!!.toInt()
                    }
                    if (imageMax != null) {
                        imSend = imageMax.url.toString()
                    }
                }
                myIntent.putExtra("image", imSend)

                if (imSend.length != 0){
                    myIntent.putExtra("image", imSend)
                }else{
                    myIntent.putExtra("image", "")

                }

                itemView.context.startActivity(myIntent)

            }
        }


    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        Log.d(TAG, "onCreateViewHolder: ${count++}")

        // create a new view
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentEvent = events[position]
        val temp = "TBD"
        i("Log event view", currentEvent.toString())
        if (!currentEvent.name.isNullOrEmpty()) {
            holder.titleName.text = currentEvent.name
        } else {
            holder.titleName.text = temp
        }

        val context = holder.itemView.context
        if (currentEvent.images != null) {
            val imageMax = currentEvent.images.maxByOrNull {
                it.width!!.toInt() * it.height!!.toInt()
            }

            if (imageMax != null) {
                Glide.with(context)
                    .load(imageMax.url)
                    .placeholder(R.drawable.logo)
                    .centerCrop()
                    .circleCrop()
                    .into(holder.image)
            } else {
                Glide.with(context)
                    .load(R.drawable.logo)
                    .placeholder(R.drawable.logo)
                    .circleCrop()
                    .into(holder.image)
            }
            if (imageMax != null) {
            }
        }

        if (!currentEvent._embedded?.venues?.get(0)?.address?.line1.isNullOrEmpty()) {
            holder.address.text =
                "Address : \n${currentEvent._embedded!!.venues!![0].address!!.line1}"
        } else {
            holder.address.text = "Address : $temp"
        }
        if (!currentEvent._embedded?.venues?.get(0)?.city?.name.isNullOrEmpty()) {
            holder.city.text = "${currentEvent._embedded!!.venues!![0].city!!.name},"
        } else {
            holder.city.text = temp
        }
        if (!currentEvent._embedded?.venues?.get(0)?.state?.stateCode.isNullOrEmpty()) {
            holder.state.text = currentEvent._embedded!!.venues!![0].state!!.stateCode
        } else {
            holder.state.text = temp
        }
        if (!currentEvent._embedded?.venues?.get(0)?.name.isNullOrEmpty()) {
            holder.venName.text = "Venue : ${currentEvent._embedded!!.venues!![0].name}"
        } else {
            holder.venName.text = "Venue : $temp"
        }

        //Date and Time

        try {
            if (currentEvent.dates?.start?.localDate?.isNotEmpty() == true) {
                if (currentEvent.dates.start.localTime?.isNotEmpty() == true) {
                    val time = convertTime(currentEvent.dates.start.localTime)
                    val date = convertDate(currentEvent.dates.start.localDate)
                    dateTime = "$date @: $time"
                }
            } else {
                dateTime = "0:00"
            }
        } catch (error1: Exception) {
            i("Error", "Perception check : 131")
            error1.printStackTrace()
            dateTime = "TBD"
        }
        holder.date.text = dateTime

        var priceTogether = ""

        if (!currentEvent.priceRanges.isNullOrEmpty()) {
            val max = currentEvent.priceRanges[0].max?.toInt()
            val min = currentEvent.priceRanges[0].min?.toInt()
            if (min != null) {
                if (max != null) {
                    priceTogether = ticketPrice(min, max)
                }
            }
            holder.price.text = priceTogether
        } else {
            holder.price.text = temp
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }

    private fun ticketPrice(min: Int, max: Int): String {
        //val max = currentEvent.priceRanges.get(0).max?.toInt()
        //val min = currentEvent.priceRanges.get(0).min?.toInt()
        var priceTogether: String = "TBD"
        if (min == max) {
            if (min == 0) {
                priceTogether = "Free"
            } else {
                priceTogether = "$${max}"
            }
        }
        if (min != null) {
            if (max != null) {
                if ((min != max) && (min < max)) {
                    priceTogether = "$${min}.00 - $${max}.00"
                }
            }
        }
        return priceTogether
    }

    private fun convertDate(date: String): String {
        val dateFinal: String
        val year = "${date[0]}${date[1]}${date[2]}${date[3]}"
        val day = "${date[5]}${date[6]}"
        val month = "${date[8]}${date[9]}"
        dateFinal = "${day}/${month}/${year}"
        return dateFinal
    }

    private fun convertTime(time: String): String {
        //get individual time variables
        val h1 = time[0]
        val h2 = time[1]
        val m1 = time[3]
        val m2 = time[4]

        //convert from chars to ints
        val hour1 = Integer.parseInt(h1.toString())
        val hour2 = Integer.parseInt(h2.toString())
        val min1 = Integer.parseInt(m1.toString())
        val min2 = Integer.parseInt(m2.toString())

        //combine times
        var hours = (hour1 * 10) + hour2
        //val mins = (min1*10) + min2

        // i("hours", "${hours}")
        // i("mins", "${min1}${min2}")

        var mornEve = "AM"
        if (hours > 12) {
            mornEve = "PM"
            hours -= 12
        }

        //i("Time", timeTogether)
        return "${hours}:${min1}${min2} $mornEve"
    }

}

