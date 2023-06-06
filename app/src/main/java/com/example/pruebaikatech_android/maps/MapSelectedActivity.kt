package com.example.pruebaikatech_android.maps

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pruebaikatech_android.databinding.ActivityMapSelectedBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import java.io.IOException

class MapSelectedActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var binding: ActivityMapSelectedBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSelectedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapsViewSelected
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapReady(map: GoogleMap) {
        val geocoder = Geocoder(this)
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
        googleMap.setOnMapClickListener{latLng->
            val latitudMarcada = latLng.latitude
            val longitudMarcada = latLng.longitude
            try {
                val direcciones = geocoder.getFromLocation(latitudMarcada, longitudMarcada, 1)
                if (!direcciones.isNullOrEmpty()) {
                    val direccionMarcada = direcciones[0].getAddressLine(0)

                    val resultIntent = Intent()
                    resultIntent.putExtra("latitude", latitudMarcada)
                    resultIntent.putExtra("longitude", longitudMarcada)
                    resultIntent.putExtra("direccion", direccionMarcada)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}