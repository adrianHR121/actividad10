package com.android.example.actividad10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
class ListadoActivity : AppCompatActivity() {
    //Instancias
    private lateinit var etListado: TextView
    private lateinit var btnRegresar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado)

        //Asociamos el componente gráfico
        etListado = findViewById(R.id.txtDetalle)
        btnRegresar = findViewById(R.id.btnRegresar)

        //Llamada al método para mostrar los registros de la BD
        listarContactos()
        btnRegresar.setOnClickListener { regresar() }
    }//onCreate
    private fun listarContactos() {
        val cliente = AsyncHttpClient()
        cliente.get("http://192.168.100.68/androidConsultaMySql.php?", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                if (statusCode == 200) {
                    try {
                        val responseText = String(responseBody, Charsets.UTF_8)
                        println("Server Response: $responseText")

                        if (responseText != "0") {
                            val contactos = JSONArray(responseText)
                            etListado.text = "" // Clear previous content
                            for (i in 0 until contactos.length()) {
                                val contacto = contactos.getJSONObject(i)
                                etListado.append("Contacto #${i + 1}\n")
                                etListado.append("Nombre: ${contacto.getString("Nombre")}\n")
                                etListado.append("Apellidos: ${contacto.getString("Apellidos")}\n")
                                etListado.append("Telefono: ${contacto.getString("Telefono")}\n")
                                etListado.append("Correo: ${contacto.getString("Email")}\n\n")
                            }
                        } else {
                            Toast.makeText(this@ListadoActivity, "Contacto no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        println("JSON Exception: $e")
                        Toast.makeText(this@ListadoActivity, "Error al obtener información.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ListadoActivity, "Sin resultados en busqueda.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header?>?, responseBody: ByteArray?, error: Throwable?) {
                val errorMsg = responseBody?.let { String(it, Charsets.UTF_8) } ?: "Unknown error"
                println("Error: $errorMsg")
                Toast.makeText(this@ListadoActivity, "Error de conexión.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun regresar() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    } //regresar
}//class