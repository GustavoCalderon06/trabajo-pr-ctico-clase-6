package com.example.myapplication
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class EditarDatos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_datos)
        getUserInfo()
        val buttonSave = findViewById<Button>(R.id.button_save)
        buttonSave.setOnClickListener {
            val name = findViewById<EditText>(R.id.edit_text_name).text.toString()
            val email = findViewById<EditText>(R.id.edit_text_email).text.toString()
            val rut = findViewById<EditText>(R.id.edit_text_rut).text.toString()

            updateUserInfo(name, email, rut)
        }
    }
    private fun getUserInfo() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("http://52.200.139.211:5000/user")
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("EditarDatos", "Error al realizar la solicitud HTTP", e)
            }
            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (response.isSuccessful) {
                    val userInfo = response.body?.string()

                    runOnUiThread {
                        try {
                            val userJson = JSONObject(userInfo!!)
                            val nameTextView = findViewById<EditText>(R.id.edit_text_name)
                            val emailTextView = findViewById<EditText>(R.id.edit_text_email)
                            val rutTextView = findViewById<EditText>(R.id.edit_text_rut)

                            nameTextView.setText(userJson.getString("name"))
                            emailTextView.setText(userJson.getString("email"))
                            rutTextView.setText(userJson.getString("rut"))
                        } catch (e: JSONException) {
                            Log.e("EditarDatos", "Error al analizar el JSON", e)
                        }
                    }
                } else {
                    Log.e("EditarDatos", "Respuesta no exitosa: ${response.code}")
                }
            }
        })
    }

    private fun updateUserInfo(name: String, email: String, rut: String) {
        GlobalScope.launch {
            try {
                val client = OkHttpClient()
                // Crear el cuerpo de datos JSON para el POST
                val jsonBody = """
                {
                    "name": "$name",
                    "email": "$email",
                    "rut": "$rut"
                }
            """.trimIndent()
                val request = Request.Builder()
                    .url("http://52.200.139.211:5000/user/update")  // Enviar datos a la URL del endpoint
                    .post(RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody))
                    .build()
                withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val responseData = response.body?.string() ?: ""

                        // Ejecutar el Toast en el hilo principal
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Información actualizada con éxito: $responseData",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        finish()
                    } else {
                        // Ejecutar el Toast en el hilo principal
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Error: ${response.code}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("EditarDatos", "Error al actualizar la información", e)

                // Ejecutar el Toast en el hilo principal
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Error al actualizar la información",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}