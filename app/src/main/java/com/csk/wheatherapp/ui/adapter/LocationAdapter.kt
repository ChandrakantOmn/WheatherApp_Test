package com.csk.wheatherapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.csk.wheatherapp.data.local.entity.Location
import com.csk.wheatherapp.databinding.ListItemBinding


class LocationAdapter(
    private val locations: MutableList<Location>,
    private val onItemSelectListener: OnItemSelectListener
) :
    RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = locations.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(locations[position])
    }


    inner class ViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(item: Location) {
            binding.textView.text = item.cityName
            binding.textView.setOnClickListener {
                onItemSelectListener.onItemSelect(item)
            }
            binding.imageView.setOnClickListener {
                locations.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                onItemSelectListener.onDelete(item)
            }
        }


    }
}

interface OnItemSelectListener {
    fun onItemSelect(location: Location)
    fun onDelete(location: Location)

}
