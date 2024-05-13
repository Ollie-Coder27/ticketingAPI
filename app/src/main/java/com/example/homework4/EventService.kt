package com.example.homework4

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventService {

    //https://app.ticketmaster.com/discovery/v2/events.json?apikey=HQTzfG9hTjizZaCixmG1aPYVQkzY9fWw

    //fun getMultipleEvent(@Query("events") amount: Int) : Call<Event>
    //events.json?apikey=HQTzfG9hTjizZaCixmG1aPYVQkzY9fWw
    //@Header("user-key: 9900a9720d31dfd5fdb4352700c")
    @GET("discovery/v2/events.json?apikey=HQTzfG9hTjizZaCixmG1aPYVQkzY9fWw")
    fun getEvents(@Query("sort") sort: String,
                  @Query("keyword") key: String,
                  @Query("city") city: String) : Call<TicketData>

    @GET("discovery/v2/events.json?apikey=HQTzfG9hTjizZaCixmG1aPYVQkzY9fWw")
    fun getEventsOG(@Query("keyword") key: String,
                  @Query("city") city: String) : Call<TicketData>
}
