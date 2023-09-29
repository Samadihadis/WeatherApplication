package com.samadihadis.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var checkBox: AppCompatCheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        checkBox = findViewById(R.id.checkBox)

        val client = OkHttpClient()

        val request = Request.Builder().url("https://jsonplaceholder.typicode.com/todos/1").build()

        val callback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("tagh", "onFailure: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
//                Log.d("tagh", "onResponse: ${response.body?.string()}")

                val rawResponse = response.body!!.string()

                val jsonObject = JSONObject(rawResponse)


                val myTodo = Todo(
                    userId = jsonObject.getInt("userId"),
                    id = jsonObject.getInt("id"),
                    title = jsonObject.getString("title"),
                    completed = jsonObject.getBoolean("completed")
                )

            }
        }

        client.newCall(request).enqueue(callback)

    }



}