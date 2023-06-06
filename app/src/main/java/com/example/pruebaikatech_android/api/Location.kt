package com.example.pruebaikatech_android.api

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val address: String,
    val latitude: String,
    val longitude: String
): Parcelable