package com.example.pruebaikatech_android.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebaikatech_android.R
import com.example.pruebaikatech_android.api.Vehicle
import com.example.pruebaikatech_android.holder.OwnVehicleViewHolder

class OwnVehicleAdapter(private val vehicel:List<Vehicle>,
                        private val cedula: String,
                        private val password: String,
                        private val favoriteClick:() -> Unit,
                        private val onClickListener: (Vehicle) -> Unit):
    RecyclerView.Adapter<OwnVehicleViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnVehicleViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return OwnVehicleViewHolder(layoutInflater.inflate(R.layout.item_vehicle_db, parent, false))
    }

    override fun getItemCount(): Int = vehicel.size

    override fun onBindViewHolder(holder: OwnVehicleViewHolder, position: Int) {
        val item = vehicel[position]
        holder.bind(item, cedula, password, favoriteClick, onClickListener)
    }
}