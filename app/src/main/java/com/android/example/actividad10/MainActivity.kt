package com.android.example.actividad10

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONException
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    private lateinit var etNumEmp: EditText
    private lateinit var etNombre: EditText
    private lateinit var etApellidos: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnAgregar: ImageButton
    private lateinit var btnBuscar: ImageButton
    private lateinit var btnActualizar: ImageButton
    private lateinit var btnEliminar: ImageButton
    private lateinit var btnLista: Button

    private lateinit var requestQueue: RequestQueue

    private val baseUrl = "http://192.168.100.29/practica10/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etNumEmp = findViewById(R.id.txtNumEmp)
        etNombre = findViewById(R.id.txtNombre)
        etApellidos = findViewById(R.id.txtApellidos)
        etTelefono = findViewById(R.id.txtTelefono)
        etCorreo = findViewById(R.id.txtCorreo)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnLista = findViewById(R.id.btnLista)

        requestQueue = Volley.newRequestQueue(this)

        btnAgregar.setOnClickListener { agregarContacto() }
        btnBuscar.setOnClickListener { buscarContacto() }
        btnActualizar.setOnClickListener { actualizarContacto() }
        btnEliminar.setOnClickListener { eliminarContacto() }
        btnLista.setOnClickListener {
            val intent = Intent(this, ListadoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun ejecutarWebService(url: String, successMessage: String) {
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            Toast.makeText(this, successMessage + ": $response", Toast.LENGTH_SHORT).show()
        }, { error: VolleyError ->
            Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
        })
        requestQueue.add(stringRequest)
    }

    private fun agregarContacto() {
        val url = baseUrl + "androidInsercionMySql.php?nombre=" + URLEncoder.encode(etNombre.text.toString(), "UTF-8") +
                "&apellidos=" + URLEncoder.encode(etApellidos.text.toString(), "UTF-8") +
                "&telefono=" + URLEncoder.encode(etTelefono.text.toString(), "UTF-8") +
                "&email=" + URLEncoder.encode(etCorreo.text.toString(), "UTF-8")
        ejecutarWebService(url, "Contacto registrado")
    }

    private fun buscarContacto() {
        val cliente = AsyncHttpClient()
        val url = baseUrl + "androidBusquedaMySql.php?nombre=" + URLEncoder.encode(etNombre.text.toString(), "UTF-8") +
                "&apellidos=" + URLEncoder.encode(etApellidos.text.toString(), "UTF-8")
        cliente.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                try {
                    val responseString = String(responseBody)
                    if (responseString != "0") {
                        val contacto = JSONArray(responseString)
                        etTelefono.setText(contacto.getJSONObject(0).getString("Telefono"))
                        etCorreo.setText(contacto.getJSONObject(0).getString("Email"))
                    } else {
                        Toast.makeText(this@MainActivity, "Contacto no encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@MainActivity, "Error al procesar datos.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseBody: ByteArray?, error: Throwable) {
                Toast.makeText(this@MainActivity, "Error en búsqueda: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarContacto() {
        val url = baseUrl + "androidActualizacionMySql.php?nombre=" + URLEncoder.encode(etNombre.text.toString(), "UTF-8") +
                "&apellidos=" + URLEncoder.encode(etApellidos.text.toString(), "UTF-8") +
                "&telefono=" + URLEncoder.encode(etTelefono.text.toString(), "UTF-8") +
                "&email=" + URLEncoder.encode(etCorreo.text.toString(), "UTF-8")
        ejecutarWebService(url, "Contacto actualizado")
    }

    private fun eliminarContacto() {
        val url = baseUrl + "androidEliminacionMySql.php?nombre=" + URLEncoder.encode(etNombre.text.toString(), "UTF-8") +
                "&apellidos=" + URLEncoder.encode(etApellidos.text.toString(), "UTF-8")
        ejecutarWebService(url, "Contacto eliminado")
    }
}