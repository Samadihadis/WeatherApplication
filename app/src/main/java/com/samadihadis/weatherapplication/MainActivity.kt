package com.samadihadis.weatherapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.samadihadis.weatherapplication.databinding.ActivityMainBinding
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.SimpleFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getData()

    }

        fun showContent(
            cityName: String,
            weatherDescription: String,
            imageUrl: String,
            sunrise: Int,
            sunset: Int,
            temp: Double,
            feelsLike: Double,
            tempMin: Double,
            tempMax: Double,
            pressure: Int,
            humidity: Int
        ) {
            binding.cityName.text = cityName
            binding.weatherDescription.text = weatherDescription
            binding.sunrise.text = "طلوع آفتاب: " + getTimeFromUnixTime(sunrise)
            binding.sunset.text = "غروب آفتاب: " + getTimeFromUnixTime(sunset)
            binding.textViewTemp.text = "دما : $temp"
            binding.textViewFeelsLike.text = "دمای احساس شده : $feelsLike"
            binding.textViewTempMin.text = "حداقل دما : $tempMin"
            binding.textViewTempMax.text = "حداکثر دما : $tempMax"
            binding.textViewPressure.text = "فشار هوا : $pressure"
            binding.textViewHumidity.text = "رطوبت هوا : $humidity"
            Glide.with(this@MainActivity).load(imageUrl).into(binding.imageViewWeather)
        }


        fun getTimeFromUnixTime(unixTime: Int): String {
            val time = unixTime * 1000.toLong()
            val date = Date(time)
            val formatter = SimpleDateFormat("HH:mm a")
            return formatter.format(date)

        }

        fun getData() {

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/weather?q=tehran&appid=121de68caff6c35cb4ef79c94198d991&lang=fa&units=metric")
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d("tagx", "onFailure:failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    val rawContent = response.body!!.string()
                    getDataAndShowThem(rawContent)
                }
            })
        }


        fun getDataAndShowThem(rawData: String) {
            val jsonObject = JSONObject(rawData)

            val weatherArray = jsonObject.getJSONArray("weather")
            val weatherObject = weatherArray.getJSONObject(0)
            val iconId = weatherObject.getString("icon")
            val imageUrl = "https://openweathermap.org/img/wn/${iconId}@2x.png"


            val sunrise = jsonObject.getJSONObject("sys").getInt("sunrise")
            val sunset = jsonObject.getJSONObject("sys").getInt("sunset")

            val temp = jsonObject.getJSONObject("main").getDouble("temp")
            val feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like")
            val tempMin = jsonObject.getJSONObject("main").getDouble("temp_min")
            val tempMax = jsonObject.getJSONObject("main").getDouble("temp_max")
            val pressure = jsonObject.getJSONObject("main").getInt("pressure")
            val humidity = jsonObject.getJSONObject("main").getInt("humidity")


            runOnUiThread {
                showContent(
                    jsonObject.getString("name"),
                    weatherObject.getString("description"),
                    imageUrl,
                    sunrise,
                    sunset,
                    temp,
                    feelsLike,
                    tempMin,
                    tempMax,
                    pressure,
                    humidity
                )
            }

        }

    }



