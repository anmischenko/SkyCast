package com.example.skycast.adapters

import java.util.SplittableRandom

data class WeatherModel(
    val city: String,
    val time: String,
    val condition: String,
    val currentTemp: String,
    val maxTemp: String,
    val minTemp: String,
    val imgUrl: String,
    val today: String,
    val wind: String,
    val humidity: String,
    val visibility: String
)