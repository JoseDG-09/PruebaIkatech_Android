package com.example.pruebaikatech_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebaikatech_android.adapter.OwnVehicleAdapter
import com.example.pruebaikatech_android.api.Location
import com.example.pruebaikatech_android.api.Vehicle
import com.example.pruebaikatech_android.databinding.ActivityMyVehicleBinding
import com.example.pruebaikatech_android.vehicle.DetailActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyVehicleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyVehicleBinding
    private lateinit var adapter: OwnVehicleAdapter
    private val db = Firebase.firestore
    private val vehicle = mutableListOf<Vehicle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getOwnVehicles()
        initRecyclerView()
    }
    
    private fun initRecyclerView() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        adapter = OwnVehicleAdapter(vehicle, cedula.toString(), password.toString(),
            { onItemFavorite() }) { onItemSelected(it) }
        binding.rvOwnVehicle.layoutManager = LinearLayoutManager(this)
        binding.rvOwnVehicle.adapter = adapter
    }

    private fun onItemSelected(vehicle: Vehicle) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.VEHICLE, vehicle)
        startActivity(intent)
    }

    private fun onItemFavorite() {
        getOwnVehicles()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getOwnVehicles() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("VehiculosPropios")
            .get()
            .addOnSuccessListener {
                if (it.size() > 0) {
                    vehicle.clear()
                    for (document in it) {
                        if (document.data.get("state").toString().toLowerCase() == "propio" || document.data.get("state").toString().toLowerCase() == "comprado") {
                            val model = document.data.get("model").toString()
                            val delet_request = document.data.get("delet_request").toString()
                            val favorite = document.data.get("favorite").toString()
                            val locationList = Location(
                                document.data.get("address").toString(),
                                document.data.get("latitude").toString(),
                                document.data.get("longitude").toString()
                            )
                            val vehicleList = Vehicle(
                                document.data.get("brand").toString(),
                                model.toInt(),
                                delet_request.toBoolean(),
                                document.data.get("state").toString(),
                                favorite.toBoolean(),
                                document.data.get("image").toString(),
                                locationList,
                                document.data.get("collection_name").toString(),
                                document.data.get("combustion_type").toString()
                            )
                            vehicle.add(vehicleList)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}