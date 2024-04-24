package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actualizar:Button=findViewById(R.id.actualizar)
        actualizar.setOnClickListener{
            val intent=Intent(this,EditarDatos::class.java)
            startActivity(intent)
        }

    }


    fun getUserInfoKotlin(view: View) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://52.200.139.211:5000/user")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("MainActivity", "Error al realizar la solicitud HTTP", e)
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val userInfo = response.body?.string()

                    runOnUiThread {
                        try {
                            val userJson = JSONObject(userInfo!!)
                            val nameValueTextView =
                                findViewById<TextView>(R.id.name_value_text_view)
                            val emailValueTextView =
                                findViewById<TextView>(R.id.email_value_text_view)
                            val rutValueTextView = findViewById<TextView>(R.id.rut_value_text_view)

                            nameValueTextView.text = userJson.getString("name")
                            emailValueTextView.text = userJson.getString("email")
                            rutValueTextView.text = userJson.getString("rut")
                        } catch (e: JSONException) {
                            Log.e("MainActivity", "Error al analizar el JSON", e)
                        }
                    }
                } else {
                    Log.e("MainActivity", "Respuesta no exitosa: ${response.code}")
                }
            }
        })
    }

}