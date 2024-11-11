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

        //Instancia para obtener los datos del servidor
        val cliente = AsyncHttpClient()
        cliente["http://192.168.90.88/androidConsultaMySql.php?", object :
            AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {

                //El código 200 indica que hubo registros
                if (statusCode == 200) {
                    try {
                        val x = responseBody.toString()

                        //Si existen registros como resultado de la busqueda
                        if (x != "0") {
                            var i = 0 //Contabilizar la cantidad de registros obtenidos
                            val contactos =
                                JSONArray(responseBody.toString())

                            //ciclo para colocar la información dentro del TextView
                            while (i < contactos.length()) {
                                etListado.text.toString().plus("Contacto #${i
                                        + 1}")
                                etListado.text.toString().plus("Nombre: "+
                                        contactos.getJSONObject(i).getString("Nombre"))
                                etListado.text.toString().plus("Apellidos: "
                                        + contactos.getJSONObject(i).getString("Apellidos"))
                                etListado.text.toString().plus("Telefono: " +
                                        contactos.getJSONObject(i).getString("Telefono"))
                                etListado.text.toString().plus("Correo: " +
                                        contactos.getJSONObject(i).getString("Email"))
                                i++
                            } //while
                        } else {
                            Toast.makeText(
                                this@ListadoActivity,"Contacto no encontrado.",Toast.LENGTH_SHORT).show()
                        } //else
                    } catch (e: JSONException) {
                        Toast.makeText(this@ListadoActivity,"Error al obtener información.",Toast.LENGTH_SHORT).show()
                    } //catch
                } //if
                else {
                    Toast.makeText(this@ListadoActivity,"Sin resultados en busqueda.",Toast.LENGTH_SHORT).show()
                } //else
            } //onSuccess
            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
            } //onFailure
        }]
    } //listarContactos
    private fun regresar() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    } //regresar
}//class