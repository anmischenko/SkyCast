package com.example.skycast.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skycast.R
import com.example.skycast.adapters.WeatherAdapter
import com.example.skycast.adapters.WeatherModel
import com.example.skycast.databinding.FragmentMainBinding
import com.example.skycast.databinding.FragmentTodayBinding

class TodayFragment : Fragment() {
    private lateinit var binding: FragmentTodayBinding
    private lateinit var adapter: WeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTodayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
    }

    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter()
        rcView.adapter = adapter
        val list = listOf(
            WeatherModel("",
                "12:00",
                "Sunny",
                "26",
                "",
                "",
                "",
                ""),
            WeatherModel("",
                "13:00",
                "Sunny",
                "26",
                "",
                "",
                "",
                ""),
            WeatherModel("",
                "14:00",
                "Sunny",
                "29",
                "",
                "",
                "",
                "")


        )
        adapter.submitList(list)
    }

    companion object {

        @JvmStatic
        fun newInstance() = TodayFragment()
    }
}