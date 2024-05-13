package com.example.homework4

import com.google.gson.annotations.SerializedName

data class TicketData(
    val _embedded: EmbeddedData,
    val page: Page
)

data class EmbeddedData(
    val events: List<Event>
)

data class Event (val name: String?,
                  val images: List<image>?,
                  val dates: dates?,
                  val _embedded: _embedded?,
                  val url: String?,
                  val priceRanges: List<prices>?,

)

//Image data classes
data class image(
    @SerializedName("url")
    val url: String?,
    @SerializedName("width")
    val width: Int?,
    @SerializedName("height")
    val height: Int?)


//Time & Date data classes
data class dates(
    @SerializedName("start")
    val start: timeDate?
)
data class timeDate(
    @SerializedName("localTime")
    val localTime: String?,
    @SerializedName("localDate")
    val localDate: String?)

//Venue data classes
data class _embedded(
    @SerializedName("venues")
    val venues: List<venue>?
)

data class venue(
    @SerializedName("name")
    val name: String?,
    @SerializedName("city")
    val city: city?,
    @SerializedName("state")
    val state: state?,
    @SerializedName("address")
    val address: address?
)
data class city(
    @SerializedName("name")
    val name: String?)

data class state(
    @SerializedName("name")
    val name: String?,
    @SerializedName("stateCode")
    val stateCode: String?
)
data class address(
    @SerializedName("line1")
    val line1: String?
)

//Price data classes
data class prices(
    @SerializedName("min")
    val min: Double?,
    @SerializedName("max")
    val max: Double?
)

data class Page(
    @SerializedName("number")
    val number: Int?,
    @SerializedName("totalElements")
    val totalElements: Int?
)