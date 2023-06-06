package com.example.pruebaikatech_android.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vehicle(
    var brand:String,
    var model:Int,
    var delet_request:Boolean,
    var state:String,
    var favorite:Boolean,
    var image: String,
    var location: Location,
    var collection_name: String,
    var combustion_type: String
): Parcelable