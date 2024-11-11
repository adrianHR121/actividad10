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

class MainActivity : AppCompatActivity() {
    //Objetos de componentes gráficos
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

    //Gestionar operaciones con la BD
    private lateinit var requestQueue: RequestQueue

    //API del servidor MySQL
    private val url = "http://{ponle tu ip}"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Asociar los objetos con componentes gráficos
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

        //Inicializar gestor de operaciones
        requestQueue = Volley.newRequestQueue(this)
        btnAgregar.setOnClickListener {
            agregarContacto()
        }
        btnBuscar.setOnClickListener {
            buscarContacto()
        }
        btnActualizar.setOnClickListener{
            actualizarContacto()
        }
        btnEliminar.setOnClickListener {
            eliminarContacto()
        }
        btnLista.setOnClickListener {
            val intent = Intent(this, ListadoActivity::class.java)
            startActivity(intent)
        }
    }//onCreate
    private fun ejecutarWebService(url: String, msg: String) {
        Toast.makeText(applicationContext,msg, Toast.LENGTH_LONG).show()
        val stringRequest = StringRequest(Request.Method.GET, url, {
            fun onResponse(response: String?) {
                Toast.makeText(this@MainActivity, response.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        }, {
            fun onErrorResponse(error: VolleyError) {
                Toast.makeText(this@MainActivity, error.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        })
        val requestQueue: RequestQueue =
            Volley.newRequestQueue(this@MainActivity)
        requestQueue.add(stringRequest)
    } //ejecutarWebService
    private fun agregarContacto() {
        ejecutarWebService(
            url + "androidInsercionMySql.php?nombre=" +
                    etNombre.text + "&apellidos=" + etApellidos.text +
                    "&telefono=" + etTelefono.text + "&email=" +
                    etCorreo.text,
            "Contacto registrado."
        )
    }//agregarContacto
    private fun buscarContacto() {
        //Instancia que recibe la información del servidor
        val cliente = AsyncHttpClient()
        //Llamada al arhivo PHp
        cliente.get(
            url + "androidBusquedaMySql.php?nombre=" +
                    etNombre.text + "&apellidos=" + etApellidos.text,
            object : AsyncHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,

                    headers: Array<out Header>?,
                    responseBody: ByteArray?
                ) {
                //El código 200 indica que hubo registros

                    if (statusCode == 200) {

                        try {
                            val x = responseBody.toString()
                            //Si existen registros como resultado de labusqueda
                            if (x != "0") {
                            //Recibe la información y coloca en elarreglo JSON

                                val contacto = JSONArray(responseBody)
                                //Colocar la información en las cajas de texto
                                //etTelefono.setText(contacto.getJSONObject(0).getString("Telefono"))
                                etCorreo.setText(contacto.getJSONObject(0).getString("Email"))
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Contacto no encontrado.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } //else
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@MainActivity,
                                "Error al obtener información.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } //catch
                    } //if
                    else {

                        Toast.makeText(
                            this@MainActivity,

                            "Sin resultados en busqueda.",
                            Toast.LENGTH_SHORT

                        ).show()
                    } //else
                }
                override fun onFailure(
                    statusCode: Int,

                    headers: Array<out Header>?,
                    responseBody: ByteArray?,
                    error: Throwable?

                ) {
                    TODO("Not yet implemented")
                }
            })
    } //buscarContacto
    private fun actualizarContacto() {
        ejecutarWebService(
            url + "androidActualizacionMySql.php?nombre=" +
                    etNombre.text + "&apellidos=" + etApellidos.text +
                    "&telefono=" + etTelefono.text + "&email=" +
                    etCorreo.text,
            "Contacto actualizado."
        )
        limpiarCampos()
    } //actualizarContacto
    private fun eliminarContacto() {
        ejecutarWebService(
            url + "androidEliminacionMySql.php?nombre=" +
                    etNombre.text + "&apellidos=" + etApellidos.text,
            "Contacto eliminado."
        )
        limpiarCampos()
    } //eliminarContacto
    private fun limpiarCampos() {
        etNombre.setText("")
        etApellidos.setText("")
        etTelefono.setText("")
        etCorreo.setText("")
        etNombre.requestFocus()
    } //limpiarCampos
}//class