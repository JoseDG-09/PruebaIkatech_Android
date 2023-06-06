package com.example.pruebaikatech_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebaikatech_android.R
import com.example.pruebaikatech_android.api.Vehicle
import com.example.pruebaikatech_android.holder.VehicleViewHolder

class VehicleAdapter(private val vehicles:List<Vehicle>, private val cedula: String, private val password: String, private val favoriteClick:() -> Unit, private val onClickListener: (Vehicle) -> Unit):
    RecyclerView.Adapter<VehicleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return VehicleViewHolder(layoutInflater.inflate(R.layout.item_vehicle, parent, false))
    }

    override fun getItemCount(): Int = vehicles.size

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val item = vehicles[position]
        holder.bind(item, cedula, password, favoriteClick, onClickListener)
    }
}
