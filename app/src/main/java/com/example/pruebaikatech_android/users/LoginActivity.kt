package com.example.pruebaikatech_android.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pruebaikatech_android.MainActivity
import com.example.pruebaikatech_android.databinding.ActivityLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener { validarDatos() }
    }

    private fun validarDatos() {
        val cedula: String = binding.tiCedula.text.toString()
        val password: String = binding.tiPassword.text.toString()
        if (!cedula.isNullOrEmpty() && !password.isNullOrEmpty()) {
            db.collection("PruebaIkatech").document("Usuarios").collection(cedula).document(password)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putString("Cedula", cedula)
                        editor.putString("Password", password)
                        editor.apply()
                        navigateTo()
                    } else {
                        Toast.makeText(this, "El usuario no existe", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    showError()
                }
        } else {
            Toast.makeText(this, "Por favor escriba su cedula y contraseña", Toast.LENGTH_LONG).show()
        }
    }

    private fun navigateTo() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showError() {
        Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_LONG).show()
    }
}