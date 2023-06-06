package com.example.pruebaikatech_android.api

import com.google.gson.annotations.SerializedName

data class VehicleResponse (@SerializedName("vehicles") var vehiculos: List<Vehicle>)