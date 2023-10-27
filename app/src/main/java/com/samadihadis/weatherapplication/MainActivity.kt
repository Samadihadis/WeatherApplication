package com.samadihadis.weatherapplication

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.samadihadis.weatherapplication.databinding.ActivityMainBinding
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.toLongOrDefault
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    private fun showContent(
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

        with(binding){
            cityImageView.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            cityNameTextView.text = cityName
            weatherDescriptionTextView.text = weatherDescription
            sunriseTextView.text = "طلوع: " + getTimeFromUnixTime(sunrise)
            sunsetTextView.text = "غروب: " + getTimeFromUnixTime(sunset)
            textViewTemp.text = "دما : $temp"
            textViewFeelsLike.text = "دمای احساس شده : $feelsLike"
            textViewTempMin.text = "حداقل دما : $tempMin"
            textViewTempMax.text = "حداکثر دما : $tempMax"
            textViewPressure.text = "فشار هوا : $pressure"
            textViewHumidity.text = "رطوبت هوا : $humidity"
        }
        Glide.with(this@MainActivity).load(imageUrl).into(binding.imageViewWeather)
    }


    private fun getTimeFromUnixTime(unixTime: Int): String {
        val time = unixTime * 1000.toLong()
        val date = Date(time)
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(date)
    }

    private fun getData() {

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

    fun reloadData(view: View) {

        with(binding){
            cityImageView.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            cityNameTextView.text = "شهر"
            weatherDescriptionTextView.text = "آب و هوا"
            sunriseTextView.text = "طلوع: --"
            sunsetTextView.text = "غروب: --"
            textViewTemp.text = "دما: --"
            textViewFeelsLike.text = "دمای احساس شده: --"
            textViewTempMin.text = "حداقل دما: --"
            textViewTempMax.text = "حداکثر دما: --"
            textViewPressure.text = "فشار هوا: --"
            textViewHumidity.text = "رطوبت هوا: --"
        }

        Glide.with(this@MainActivity).load(R.drawable.ic_refresh).into(binding.imageViewWeather)


        getData()
    }



}



