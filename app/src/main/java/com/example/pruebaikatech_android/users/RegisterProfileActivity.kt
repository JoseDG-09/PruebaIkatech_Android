package com.example.pruebaikatech_android.users

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pruebaikatech_android.MainActivity
import com.example.pruebaikatech_android.maps.MapSelectedActivity
import com.example.pruebaikatech_android.databinding.ActivityRegisterProfileBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterProfileBinding
    companion object {
        private const val REQUEST_MAP = 1
    }
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var address = ""
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cargarDatos()
        binding.btnDireccion.setOnClickListener { obtenerDireccion() }
        binding.btnGuardar.setOnClickListener { guardarDatos() }
    }

    fun cargarDatos() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        binding.tvCedula.text = cedula
        binding.tvPassword.text = password
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

    private fun guardarDatos() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        if (!binding.etNombre.text.isNullOrEmpty() && !binding.tvDireccion.text.isNullOrEmpty()) {
            val nombre = binding.etNombre.text.toString()
            val datos = hashMapOf(
                "Nombre" to nombre,
                "address" to address,
                "latitude" to latitude,
                "longitude" to longitude,
                "numberVehicle" to 1
            )
            db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString())
                .document(password.toString()).collection("Datos").document("MisDatos")
                .set(datos)
                .addOnSuccessListener {
                    Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_SHORT).show()
                }
        }else {
            Toast.makeText(this, "Por favor complete todos los datos", Toast.LENGTH_SHORT).show()
        }
    }
}