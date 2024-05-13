package com.example.homework4

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log.i
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.homework4.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var geo:LatLng
    //GM API: AIzaSyCW2u7UTp8PS4h82cgcAcW0B0zVul_Yf6s //OLD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val locationSent = intent.getStringExtra("Address")
        geo = getLocationAddress(this,locationSent)
        //UiSettings.setZoomControlsEnabled(true)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        val loaction = LatLng(geo.latitude, geo.longitude)
        val zoomLevel = 15.0f
        mMap.addMarker(MarkerOptions().position(loaction).title("Location Marker"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loaction.latitude, loaction.longitude), zoomLevel)) //CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLevel)
        //mMap.cameraPosition.target.latitude
        //mMap.cameraPosition.target.longitude
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    //https://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address
    private fun getLocationAddress(context: Context, add: String?): LatLng{
        val coder = Geocoder(context)
        val address: List<Address>
        lateinit var p1: LatLng

        try {
            address = add?.let { coder.getFromLocationName(it, 5) }!!
            /*var count = 0
            while(count < address.size){
                i("View Address Array", "${address[count]}")
                count++
            }*/
            val loc: Address = address.get(0)
            p1 = LatLng(loc.latitude, loc.longitude)
        }catch (e: IOException){
            i("Error", e.stackTrace.toString())
            finish()
            super.onResume()
        }catch (e: Error){
            i("Error", e.stackTrace.toString())
            finish()
            super.onResume()
        }
        return p1
    }

    fun returnActivity(ignoredView: View){
        finish()
        //super.onResume()

    }
}