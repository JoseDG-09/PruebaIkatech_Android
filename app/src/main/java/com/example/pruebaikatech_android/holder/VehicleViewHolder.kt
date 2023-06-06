package com.example.pruebaikatech_android.holder

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebaikatech_android.R
import com.example.pruebaikatech_android.api.Vehicle
import com.example.pruebaikatech_android.databinding.ItemVehicleBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class VehicleViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemVehicleBinding.bind(view)
    val db = Firebase.firestore
    fun bind(vehicle: Vehicle, cedula: String, password: String, favoriteClick:() -> Unit, onClickListener:(Vehicle) -> Unit) {
        val id: String = "${vehicle.brand}-${vehicle.location.latitude}-${vehicle.location.longitude}"
        db.collection("PruebaIkatech").document("Usuarios").collection(cedula).document(password)
            .collection("VehiculosPropios").document(id.toString())
            .get()
            .addOnSuccessListener { it ->
                if (it.exists()) {
                    var brand: String = it.data?.get("brand").toString()
                    if (!brand.isNullOrEmpty() && brand != "null") {
                        vehicle.brand = brand
                    }

                    var model: String = it.data?.get("model").toString()
                    if (!model.isNullOrEmpty() && model != "null") {
                        vehicle.model = model.toInt()
                    }

                    var delet_request: String = it.data?.get("delete").toString()
                    if (!delet_request.isNullOrEmpty() && delet_request != "null") {
                        vehicle.delet_request = delet_request.toBoolean()
                    }

                    var state: String = it.data?.get("state").toString()
                    if (!state.isNullOrEmpty() && state != "null") {
                        vehicle.state = state
                        if (vehicle.state.toLowerCase() == "propio" || vehicle.state.toLowerCase() == "comprado") {
                            db.collection("PruebaIkatech").document("Usuarios").collection(cedula).document(password).collection("VehiculosPropios").document(id)
                                .set(
                                    hashMapOf("brand" to vehicle.brand,
                                        "model" to vehicle.model,
                                        "delet_request" to vehicle.delet_request,
                                        "state" to vehicle.state,
                                        "favorite" to vehicle.favorite,
                                        "image" to vehicle.image,
                                        "address" to vehicle.location.address,
                                        "latitude" to vehicle.location.latitude,
                                        "longitude" to vehicle.location.longitude,
                                        "collection_name" to vehicle.collection_name,
                                        "combustion_type" to vehicle.combustion_type))
                        }
                    }

                    var favorite: String = it.data?.get("favorite").toString()
                    if (!favorite.isNullOrEmpty() && favorite != "null") {
                        vehicle.favorite = favorite.toBoolean()
                    }

                    var collection_name: String = it.data?.get("collection").toString()
                    if (!collection_name.isNullOrEmpty() && collection_name != "null") {
                        vehicle.collection_name = collection_name
                    }

                    var combustion_type: String = it.data?.get("combustion").toString()
                    if (!combustion_type.isNullOrEmpty() && combustion_type != "null") {
                        vehicle.combustion_type = combustion_type
                    }
                }

                Picasso.get().load(vehicle.image).into(binding.ivImagen)
                binding.txMarca.text = vehicle.brand
                binding.txStado.text = vehicle.state

                if (!vehicle.favorite) {
                    binding.ivFavorito.setBackgroundResource(R.drawable.ic_star_border)
                }else {
                    binding.ivFavorito.setBackgroundResource(R.drawable.ic_star_24)
                }
                binding.ivFavorito.setOnClickListener {
                    if (!vehicle.favorite) {
                        db.collection("PruebaIkatech").document("Usuarios").collection(cedula).document(password).collection("VehiculosPropios").document(id)
                            .set(
                                hashMapOf("brand" to vehicle.brand,
                                    "model" to vehicle.model,
                                    "delet_request" to vehicle.delet_request,
                                    "state" to vehicle.state,
                                    "favorite" to true,
                                    "image" to vehicle.image,
                                    "address" to vehicle.location.address,
                                    "latitude" to vehicle.location.latitude,
                                    "longitude" to vehicle.location.longitude,
                                    "collection_name" to vehicle.collection_name,
                                    "combustion_type" to vehicle.combustion_type))
                            .addOnSuccessListener {
                                Toast.makeText(binding.ivFavorito.context, "Agrgado a favoritos", Toast.LENGTH_SHORT).show()
                                favoriteClick()
                            }
                            .addOnFailureListener {
                                Toast.makeText(binding.ivFavorito.context, "No fue posible agregar a favoritos", Toast.LENGTH_SHORT).show()
                            }
                    }else {
                        db.collection("PruebaIkatech").document("Usuarios").collection(cedula).document(password).collection("VehiculosPropios").document(id)
                            .set(
                                hashMapOf("brand" to vehicle.brand,
                                    "model" to vehicle.model,
                                    "delet_request" to vehicle.delet_request,
                                    "state" to vehicle.state,
                                    "favorite" to false,
                                    "image" to vehicle.image,
                                    "address" to vehicle.location.address,
                                    "latitude" to vehicle.location.latitude,
                                    "longitude" to vehicle.location.longitude,
                                    "collection_name" to vehicle.collection_name,
                                    "combustion_type" to vehicle.combustion_type))
                            .addOnSuccessListener {
                                Toast.makeText(binding.ivFavorito.context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                                favoriteClick()
                            }
                            .addOnFailureListener {
                                Toast.makeText(binding.ivFavorito.context, "No fue posible eliminar de favoritos", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                itemView.setOnClickListener { onClickListener(vehicle) }
            }.addOnFailureListener {
                Toast.makeText(binding.ivFavorito.context, "Error en la base de datos", Toast.LENGTH_SHORT).show()
            }
    }
}
