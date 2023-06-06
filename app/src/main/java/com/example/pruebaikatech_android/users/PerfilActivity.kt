package com.example.pruebaikatech_android.users

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pruebaikatech_android.databinding.ActivityPerfilBinding
import com.example.pruebaikatech_android.vehicle.RegisterVehicleActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cargarDatos()
        binding.btnAgregar.setOnClickListener { agregarVehiculo() }
    }

    private fun agregarVehiculo() {
        val intent = Intent(this, RegisterVehicleActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("CommitPrefEdits", "ApplySharedPref", "SetTextI18n")
    private fun cargarDatos() {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        var numberVehicle: String = "1"
        binding.tvCedula.text = cedula
        binding.tvPassword.text = password
        db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString())
            .document(password.toString()).collection("Datos").document("MisDatos")
            .get()
            .addOnSuccessListener {
                binding.tvNombre.text = it.data?.get("Nombre").toString()
                binding.tvDireccion.text = it.data?.get("address").toString()
                numberVehicle = it.data?.get("numberVehicle").toString()
                if (numberVehicle.toInt() <= 3) {
                    val editor = prefs.edit()
                    editor.putString("numberVehicle", numberVehicle)
                    editor.commit()
                    binding.btnAgregar.text = "Agregar vehiculo $numberVehicle de 3"
                } else {
                    binding.btnAgregar.text = "Ya agregaste 3 vehiculos"
                    binding.btnAgregar.isEnabled = false
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Paila", Toast.LENGTH_SHORT).show()
            }
    }
}
