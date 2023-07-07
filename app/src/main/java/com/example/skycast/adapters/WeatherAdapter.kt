package com.example.skycast.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.R
import com.example.skycast.databinding.ListItemBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class WeatherAdapter(val listener: Listener?) : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {
    class Holder(view: View, val listener: Listener?) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)
        private var itemTemp: WeatherModel? = null
        init {
            itemView.setOnClickListener {
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        fun bind(item: WeatherModel) = with(binding) {
            itemTemp = item
            tvDateI.text = dateOrHour(item.currentTemp.isEmpty(), item.time)
            tvCondition.text = item.condition
            tvTemp.text = "${item.currentTemp.ifEmpty { "${item.minTemp}°C / ${item.maxTemp}" }}°C"
            Picasso.get().load("https:" + item.imgUrl).into(imgToday)

        }

        private fun dateOrHour(str: Boolean, date: String) : String {
            val result = if (str) {
                val dateNum = date.substringBefore(" ")
                val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                val outputFormat = SimpleDateFormat("dd.MM")
                outputFormat.format(inputFormat.parse(dateNum))
            } else {
                date.substringAfter(" ")
            }
            return result
        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(item: WeatherModel)
    }

}