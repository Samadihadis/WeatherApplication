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


                runOnUiThread {
                    showContent(
                        jsonObject.getString("name"),
                        weatherObject.getString("description"),
                        imageUrl
                    )
                }
            }
        })
    }


    fun showContent(cityName: String, weatherDescription: String, imageUrl: String) {
       binding.cityName.text = cityName
        binding.weatherDescription.text = weatherDescription
        Glide.with(this@MainActivity).load(imageUrl).into(binding.imageViewWeather)
    }


}