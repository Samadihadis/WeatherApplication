package com.samadihadis.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.samadihadis.weatherapplication.databinding.ActivityMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=tehran&appid=121de68caff6c35cb4ef79c94198d991&lang=fa")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("tagx", "onFailure:failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val rawResponse = response.body!!.string()

                val jsonObject = JSONObject(rawResponse)

                val weatherArray = jsonObject.getJSONArray("weather")
                val weatherObject = weatherArray.getJSONObject(0)
                val iconId = weatherObject.getString("icon")
                val imageUrl = "https://openweathermap.org/img/wn/${iconId}10d@2x.png"


                val sunrise = jsonObject.getJSONObject("sys").getInt("sunrise")
                val sunset = jsonObject.getJSONObject("sys").getInt("sunset")


                runOnUiThread {
                    showContent(
                        jsonObject.getString("name"),
                        weatherObject.getString("description"),
                        imageUrl,
                        sunrise,
                        sunset
                    )
                }
            }
        })
    }


    fun showContent(
        cityName: String,
        weatherDescription: String,
        imageUrl: String,
        sunrise: Int,
        sunset: Int
    ) {
        binding.cityName.text = cityName
        binding.weatherDescription.text = weatherDescription
        binding.sunrise.text = getTimeFromUnixTime(sunrise)
        binding.sunset.text = getTimeFromUnixTime(sunset)
        Glide.with(this@MainActivity).load(imageUrl).into(binding.imageViewWeather)
    }


    fun getTimeFromUnixTime(unixTime: Int): String {
        val time = unixTime * 1000.toLong()
        val date = Date(time)
        val formatter = SimpleDateFormat("HH:mm a")
        return formatter.format(date)

    }

}