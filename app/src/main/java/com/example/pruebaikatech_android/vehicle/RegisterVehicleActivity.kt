package com.example.pruebaikatech_android.vehicle

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pruebaikatech_android.MainActivity
import com.example.pruebaikatech_android.databinding.ActivityRegisterVehicleBinding
import com.example.pruebaikatech_android.maps.MapSelectedActivity
import com.example.pruebaikatech_android.users.PerfilActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterVehicleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterVehicleBinding
    private val db = Firebase.firestore
    companion object {
        private const val REQUEST_MAP = 1
    }
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterVehicleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDireccion.setOnClickListener { obtenerDireccion() }
        binding.btnGuardar.setOnClickListener { ValidarVehiculo() }
    }

    private fun ValidarVehiculo() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        val numberVehicle = prefs.getString("numberVehicle", "0")
        if (!binding.etMarca.text.isNullOrEmpty() && !binding.etModelo.text.isNullOrEmpty() && !binding.etColeccion.text.isNullOrEmpty() &&
            !binding.etCombustion.text.isNullOrEmpty() && !binding.tvDireccion.text.isNullOrEmpty()
        ) {
            val id: String = binding.etMarca.text.toString() + "-" + latitude + "-" + longitude
            db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString())
                .document(password.toString()).collection("Vehiculos").document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        Toast.makeText(this, "Este vehiculo ya existe", Toast.LENGTH_SHORT).show()
                    } else {
                        guardarDatos(cedula.toString(), password.toString(), id, numberVehicle.toString())
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_SHORT).show()
                }
        }else {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarDatos(cedula: String, password: String, id: String, numberVehicle: String) {
        val datos = hashMapOf(
            "brand" to binding.etMarca.text.toString(),
            "model" to binding.etModelo.text.toString(),
            "delet_request" to false,
            "state" to "propio",
            "favorite" to true,
            "image" to "https://picsum.photos/500/500",
            "collection_name" to binding.etColeccion.text.toString(),
            "combustion_type" to binding.etCombustion.text.toString(),
            "address" to address,
            "latitude" to latitude.toString(),
            "longitude" to longitude.toString()
        )
        db.collection("PruebaIkatech").document("Usuarios").collection(cedula)
            .document(password).collection("VehiculosPropios").document(id)
            .set(datos)
            .addOnSuccessListener {
                Toast.makeText(this, "Vehiculo agregado correctamente", Toast.LENGTH_SHORT).show()
                incrementVehicle(cedula, password, numberVehicle.toInt())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun incrementVehicle(cedula: String, password: String, numberVehicle: Int) {
        db.collection("PruebaIkatech").document("Usuarios").collection(cedula).document(password)
            .collection("Datos").document("MisDatos")
            .update("numberVehicle", numberVehicle + 1)
        val intent = Intent(this, PerfilActivity::class.java)
        startActivity(intent)
    }

    private fun obtenerDireccion() {
        val intent = Intent(this, MapSelectedActivity::class.java)
        startActivityForResult(intent, REQUEST_MAP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MAP && resultCode == Activity.RESULT_OK) {
            latitude = data?.getDoubleExtra("latitude", 0.0)!!
            longitude = data.getDoubleExtra("longitude", 0.0)
            address = data.getStringExtra("direccion")!!
            binding.tvDireccion.text = address
        }
    }
}
