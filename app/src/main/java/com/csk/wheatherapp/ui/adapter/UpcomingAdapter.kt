package com.csk.wheatherapp.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csk.wheatherapp.utilities.Helper
import com.csk.wheatherapp.data.remote.model.UpcomingWeatherResponse
import com.csk.wheatherapp.databinding.ListUpcomingBinding

class UpcomingAdapter(private var listWeather: List<UpcomingWeatherResponse.Daily?>?) :
    RecyclerView.Adapter<UpcomingAdapter.UpComingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpComingViewHolder =
        UpComingViewHolder(
            ListUpcomingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: UpComingViewHolder, position: Int) {
        listWeather?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return listWeather?.size?:0
    }

    inner class UpComingViewHolder(private val binding: ListUpcomingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(daily: UpcomingWeatherResponse.Daily) {
            binding.tvItemDay.text = Helper.convertDate(daily.dt?.toLong() ?: 0)
            binding.tvItemTemp.text = "${daily.main?.temp}℃"
            Glide.with(itemView)
                .load("http://openweathermap.org/img/wn/${daily.weather?.get(0)?.icon}@2x.png")
                .into(binding.imgItemCloud)
        }
    }
}