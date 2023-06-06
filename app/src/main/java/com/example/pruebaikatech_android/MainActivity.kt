package com.example.pruebaikatech_android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pruebaikatech_android.adapter.VehicleAdapter
import com.example.pruebaikatech_android.api.APIService
import com.example.pruebaikatech_android.api.Location
import com.example.pruebaikatech_android.api.Vehicle
import com.example.pruebaikatech_android.api.VehicleResponse
import com.example.pruebaikatech_android.databinding.ActivityMainBinding
import com.example.pruebaikatech_android.users.LoginActivity
import com.example.pruebaikatech_android.users.PerfilActivity
import com.example.pruebaikatech_android.users.RegisterProfileActivity
import com.example.pruebaikatech_android.vehicle.DetailActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: VehicleAdapter
    private val vehicleImage = mutableListOf<Vehicle>()
    private val db = Firebase.firestore
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getVehiclesAPI()
        initRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("CommitPrefEdits")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuSalir->{
                val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.clear()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                onBackPressed()
            }
            R.id.menuPerfil->{
                val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
                val cedula = prefs.getString("Cedula", "")
                val password = prefs.getString("Password", "")
                db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("Datos").document("MisDatos")
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val intent = Intent(this, PerfilActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, RegisterProfileActivity::class.java)
                            startActivity(intent)
                        }
                    }
                requestPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            R.id.menuVehiculosPropios->{
                val intent = Intent(this, MyVehicleActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        adapter = VehicleAdapter(vehicleImage, cedula.toString(), password.toString(),
            { onItemFavorite() }) { onItemSelected(it) }
        binding.rvVehicle.layoutManager = LinearLayoutManager(this)
        binding.rvVehicle.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://run.mocky.io/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getVehiclesAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getDogsByBreeds("d8c1c023-5ab7-413c-bbec-a3963ff3eed7")
            val vehicles: VehicleResponse? = call.body()
            runOnUiThread{
                if (call.isSuccessful && !vehicles?.vehiculos.isNullOrEmpty()) {
                    val images = vehicles?.vehiculos ?: emptyList()
                    vehicleImage.clear()
                    vehicleImage.addAll(images)
                    getVehiclesDb()
                    adapter.notifyDataSetChanged()
                }else {
                    showError()
                }
            }
        }
    }

    private fun showError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
    }

    private fun onItemSelected(vehicle: Vehicle) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.VEHICLE, vehicle)
        startActivity(intent)
    }

    private fun onItemFavorite() {
        getVehiclesAPI()
    }

    fun getVehiclesDb() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("VehiculosPropios")
            .get()
            .addOnSuccessListener { it ->
                if (it.size() > 0) {
                    for (document in it) {
                        val model = document.data.get("model").toString()
                        val delet_request = document.data.get("delet_request").toString()
                        val favorite = document.data.get("favorite").toString()
                        val locationList = Location(document.data.get("address").toString(), document.data.get("latitude").toString(), document.data.get("longitude").toString())
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
                        vehicleImage.add(vehicleList)
                    }
                }
            }
    }

    override fun onResume() {
        super.onResume()
        getVehiclesAPI()
    }
}
