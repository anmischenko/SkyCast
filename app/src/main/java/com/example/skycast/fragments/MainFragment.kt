package com.example.skycast.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.skycast.DialogManager
import com.example.skycast.MainViewModel
import com.example.skycast.R
import com.example.skycast.adapters.VpAdapter
import com.example.skycast.adapters.WeatherModel
import com.example.skycast.databinding.FragmentMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

const val API_KEY = "6fd38ba823a94ded872140417233006"
class MainFragment : Fragment() {

    val language = Locale.getDefault().getLanguage()
    private lateinit var fLocationClient: FusedLocationProviderClient
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    private val fList = listOf(
        TodayFragment.newInstance(),
        DaysFragment.newInstance()
    )

    private val tList = listOf(
        if (language == "ru") "Сегодня" else "Today",
        if (language == "ru") "3 дня" else "3 Days"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onResume() = with(binding) {
        super.onResume()
        checkLocation()
        imgWind.setOnClickListener{
            Toast.makeText(activity, getString(R.string.wind), Toast.LENGTH_SHORT).show()
        }
        imgHumidity.setOnClickListener{
            Toast.makeText(activity, getString(R.string.humidity), Toast.LENGTH_SHORT).show()
        }
        imgVisibility.setOnClickListener{
            Toast.makeText(activity, getString(R.string.visibility), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = with(binding) {
        return when (item.itemId) {
            R.id.sync -> {
                tabLayout.selectTab(tabLayout.getTabAt(0))
                checkLocation()
                true
            }
            R.id.search -> {
                DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener {
                    override fun onClick(name: String?) {
                        if (name != null) {
                            requestWeatherData(name)
                        }
                    }

                    override fun onClickNot() {
                        requestWeatherData(getString(R.string.moscow))
                    }

                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        vp.isUserInputEnabled = false
        checkPermission()
        init()
        updateCurrentCard()
        swipeRL.setOnRefreshListener {
            init()
            updateCurrentCard()
            if (!isLocationEnabled()) {
                requestWeatherData(getString(R.string.moscow))
            }
            swipeRL.isRefreshing = false
        }
    }


    private fun init() = with(binding) {
        fLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val adapter = VpAdapter(activity as FragmentActivity, fList)
        vp.adapter = adapter
        TabLayoutMediator(tabLayout, vp) {
                tab, pos -> tab.text = tList[pos]
        }.attach()
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener{
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }

                override fun onClickNot() {
                    requestWeatherData(getString(R.string.moscow))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener{
                requestWeatherData("${it.result.latitude},${it.result.longitude}")
            }

    }

    private fun updateCurrentCard() = with(binding) {
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            tvCity.text = it.city
            tvCurrentTemp.text = "${it.currentTemp.ifEmpty { "${it.minTemp}°C / ${it.maxTemp}" }}°C"
            tvConditionMain.text = it.condition
            val strImg = it.imgUrl.replace("64", "128")
            Picasso.get().load("https:" + strImg).into(imgV)
            val speed = if (language == "ru") "м/с" else "m/s"
            val wind = (it.wind.toFloat() / 2.237).toInt().toString()
            tvWind.text = "${wind} ${speed}"
            tvHumidity.text = "${it.humidity.toFloat().toInt().toString()} %"
            val distance = if (language == "ru") "км" else "km"
            tvVisibility.text = "${it.visibility.toFloat().toInt().toString()} ${distance}"
            imgHumidity.visibility = View.VISIBLE
            imgWind.visibility = View.VISIBLE
            imgVisibility.visibility = View.VISIBLE
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String?) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=3&aqi=no&alerts=no" +
                "&lang=" + language
        val queue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                    result -> parseWeatherData(result.toString())
            },
            {
                    error -> Log.d("MyLog", "Error: $error")
            }
        )
        queue.add(jsonObjectRequest)

    }


    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])

    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {

        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherItem.maxTemp,
            weatherItem.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherItem.today,
            mainObject.getJSONObject("current").getString("wind_mph"),
            mainObject.getJSONObject("current").getString("humidity"),
            mainObject.getJSONObject("current").getString("vis_km"),

            )
        model.liveDataCurrent.value = item

    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()) {
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getString("mintemp_c").toFloat().toInt().toString(),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString(),
                day.getJSONObject("day").getString("maxwind_mph"),
                day.getJSONObject("day").getString("avghumidity"),
                day.getJSONObject("day").getString("avgvis_km"),

            )
            list.add(item)
        }
        model.liveDataList.value = list
        return list
    }

    companion object {

        @JvmStatic
        fun newInstance() = MainFragment()
    }
}