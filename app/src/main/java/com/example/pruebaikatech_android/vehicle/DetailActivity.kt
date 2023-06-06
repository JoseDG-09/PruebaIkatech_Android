package com.example.pruebaikatech_android.vehicle

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.pruebaikatech_android.api.Vehicle
import com.example.pruebaikatech_android.databinding.ActivityDetailBinding
import com.example.pruebaikatech_android.maps.MapsActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    val db = Firebase.firestore

    private lateinit var binding: ActivityDetailBinding
    private var latitude: String = ""
    private var longitude: String = ""
    private var nombre: String = ""

    companion object{
        const val VEHICLE = "MainActivity:vehicle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val vehicle = intent.getParcelableExtra<Vehicle>(VEHICLE)
        detail(vehicle)
    }

    @SuppressLint("SetTextI18n")
    private fun detail(vehicle: Vehicle?) {
        val prefs = getSharedPreferences("User", Context.MODE_PRIVATE)
        val cedula = prefs.getString("Cedula", "")
        val password = prefs.getString("Password", "")
        val id: String = vehicle?.brand.toString() + "-" + vehicle?.location?.latitude.toString() + "-" + vehicle?.location?.longitude.toString()
        Picasso.get().load(vehicle?.image).into(binding.ivImagen)
        binding.tvMarca.text = vehicle?.brand
        binding.tvModelo.text = vehicle?.model.toString()
        binding.tvEstado.text = vehicle?.state
        val estado = vehicle?.state.toString().toLowerCase()
        if (estado == "comprado") {
            binding.btnCompra.text = "Compraste este vehiculo"
            binding.btnCompra.isEnabled = false
        }else if(estado == "desacticado") {
            binding.btnCompra.text = "Este vehiculo esta desactivado"
            binding.btnCompra.isEnabled = false
        }else if(estado == "propio") {
            binding.btnCompra.text = "Este vehiculo ya te pertenece"
            binding.btnCompra.isEnabled = false
        }else if(estado == "disponible") {
            binding.btnCompra.text = "Comprar"
            binding.btnCompra.isEnabled = true
            binding.btnCompra.setOnClickListener {
                db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("VehiculosPropios").document(id)
                    .set(
                        hashMapOf("brand" to vehicle?.brand,
                            "model" to vehicle?.model,
                            "delet_request" to vehicle?.delet_request,
                            "state" to "comprado",
                            "favorite" to vehicle?.favorite,
                            "image" to vehicle?.image,
                            "address" to vehicle?.location?.address,
                            "latitude" to vehicle?.location?.latitude,
                            "longitude" to vehicle?.location?.longitude,
                            "collection_name" to vehicle?.collection_name,
                            "combustion_type" to vehicle?.combustion_type))
                    .addOnSuccessListener {
                        vehicle?.state = "comprado"
                        binding.tvEstado.text = "comprado"
                        Toast.makeText(binding.btnFavorite.context, "Vehiculo comprado", Toast.LENGTH_SHORT).show()
                        binding.btnFavorite.text = "Agregar a favoritos"
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.btnFavorite.context, "No fue posible eliminar de favoritos", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.tvColeccion.text = vehicle?.collection_name
        binding.tvCombustible.text = vehicle?.combustion_type
        if (vehicle?.location?.address == "") {
            binding.tvDireccion.text = "No hay informacion"
            binding.btnMaps.isEnabled = false
            binding.btnMaps.text = "No hay informacion"
        }else {
            binding.tvDireccion.text = vehicle?.location?.address
            binding.btnMaps.isEnabled = true
            binding.btnMaps.text = "Buscar en maps"
        }
        val favorite: String = vehicle?.favorite.toString()
        if (favorite.toBoolean()) {
            binding.btnFavorite.text = "Eliminar de favoritos"
            binding.btnFavorite.setOnClickListener {
                db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("VehiculosPropios").document(id)
                    .set(
                        hashMapOf("brand" to vehicle?.brand,
                            "model" to vehicle?.model,
                            "delet_request" to vehicle?.delet_request,
                            "state" to vehicle?.state,
                            "favorite" to false,
                            "image" to vehicle?.image,
                            "address" to vehicle?.location?.address,
                            "latitude" to vehicle?.location?.latitude,
                            "longitude" to vehicle?.location?.longitude,
                            "collection_name" to vehicle?.collection_name,
                            "combustion_type" to vehicle?.combustion_type))
                    .addOnSuccessListener {
                        vehicle?.favorite = false
                        Toast.makeText(binding.btnFavorite.context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        binding.btnFavorite.text = "Agregar a favoritos"
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.btnFavorite.context, "No fue posible eliminar de favoritos", Toast.LENGTH_SHORT).show()
                    }
            }
        }else {
            binding.btnFavorite.text = "Agregar a favoritos"
            binding.btnFavorite.setOnClickListener {
                db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("VehiculosPropios").document(id)
                    .set(
                        hashMapOf("brand" to vehicle?.brand,
                            "model" to vehicle?.model,
                            "delet_request" to vehicle?.delet_request,
                            "state" to vehicle?.state,
                            "favorite" to true,
                            "image" to vehicle?.image,
                            "address" to vehicle?.location?.address,
                            "latitude" to vehicle?.location?.latitude,
                            "longitude" to vehicle?.location?.longitude,
                            "collection_name" to vehicle?.collection_name,
                            "combustion_type" to vehicle?.combustion_type))
                    .addOnSuccessListener {
                        vehicle?.favorite = true
                        Toast.makeText(binding.btnFavorite.context, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                        binding.btnFavorite.text = "Eliminar de favoritos"
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.btnFavorite.context, "No fue posible agregar a favoritos", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        val delet_request = vehicle?.delet_request.toString()
        if (delet_request.toBoolean()) {
            binding.btnDelete.text = "Cancelar la eliminacion..."
            binding.btnDelete.setOnClickListener {
                db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString()).document(password.toString()).collection("VehiculosPropios").document(id)
                    .set(
                        hashMapOf("brand" to vehicle?.brand,
                            "model" to vehicle?.model,
                            "delet_request" to false,
                            "state" to vehicle?.state,
                            "favorite" to vehicle?.favorite,
                            "image" to vehicle?.image,
                            "address" to vehicle?.location?.address,
                            "latitude" to vehicle?.location?.latitude,
                            "longitude" to vehicle?.location?.longitude,
                            "collection_name" to vehicle?.collection_name,
                            "combustion_type" to vehicle?.combustion_type))
                    .addOnSuccessListener {
                        vehicle?.delet_request = false
                        Toast.makeText(binding.btnFavorite.context, "Solicitud de borrado cancelada", Toast.LENGTH_SHORT).show()
                        binding.btnDelete.text = "Solicitar eliminacion"
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.btnFavorite.context, "No fue posible cancelar la solicitud de eliminacion", Toast.LENGTH_SHORT).show()
                    }
            }
        }else {
            binding.btnDelete.text = "Solicitar eliminacion"
            binding.btnDelete.setOnClickListener {
                db.collection("PruebaIkatech").document("Usuarios").collection(cedula.toString())
                    .document(password.toString()).collection("VehiculosPropios").document(id)
                    .set(
                        hashMapOf(
                            "brand" to vehicle?.brand,
                            "model" to vehicle?.model,
                            "delet_request" to true,
                            "state" to vehicle?.state,
                            "favorite" to vehicle?.favorite,
                            "image" to vehicle?.image,
                            "address" to vehicle?.location?.address,
                            "latitude" to vehicle?.location?.latitude,
                            "longitude" to vehicle?.location?.longitude,
                            "collection_name" to vehicle?.collection_name,
                            "combustion_type" to vehicle?.combustion_type
                        )
                    )
                    .addOnSuccessListener {
                        vehicle?.delet_request = true
                        Toast.makeText(binding.btnFavorite.context, "Solicitud de eliminacion enviada", Toast.LENGTH_SHORT).show()
                        binding.btnDelete.text = "Cancelar la eliminacion..."
                    }
                    .addOnFailureListener {
                        Toast.makeText(binding.btnFavorite.context,"No fue posible cancelar la solicitud de eliminacion", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        latitude = vehicle?.location?.latitude.toString()
        longitude = vehicle?.location?.longitude.toString()
        nombre = vehicle?.brand.toString() + " " + vehicle?.model.toString()
        binding.btnMaps.setOnClickListener {
            openMap()
        }
    }

    private fun openMap() {
        val newLatitude = latitude.toDouble()
        val newLongitude = longitude.toDouble()
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("latitude", newLatitude)
        intent.putExtra("longitude", newLongitude)
        intent.putExtra("name", nombre)
        startActivity(intent)
    }
}