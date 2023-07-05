package com.example.skycast.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skycast.MainViewModel
import com.example.skycast.R
import com.example.skycast.adapters.WeatherAdapter
import com.example.skycast.adapters.WeatherModel
import com.example.skycast.databinding.FragmentMainBinding
import com.example.skycast.databinding.FragmentTodayBinding
import org.json.JSONArray
import org.json.JSONObject

class TodayFragment : Fragment() {
    private lateinit var binding: FragmentTodayBinding
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getTodayList(it))
        }
    }

    private fun init() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter(null)
        rcView.adapter = adapter

    }

    private fun getTodayList(wItem: WeatherModel): List<WeatherModel> {
        val todayArray = JSONArray(wItem.today)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until todayArray.length()) {
            val item = WeatherModel(
                "",
                (todayArray[i] as JSONObject).getString("time"),
                (todayArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                (todayArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (todayArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                "",
                (todayArray[i] as JSONObject).getString("wind_mph"),
                (todayArray[i] as JSONObject).getString("humidity"),
                (todayArray[i] as JSONObject).getString("vis_km")
            )
            list.add(item)
        }
        return list
    }

    companion object {

        @JvmStatic
        fun newInstance() = TodayFragment()
    }
}