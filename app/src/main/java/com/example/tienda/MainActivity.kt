package com.example.tienda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.privacysandbox.tools.core.model.Method
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tienda.modelo.categoria
import org.json.JSONArray
import org.json.JSONException
import java.util.Locale.Category

class MainActivity : AppCompatActivity() {
    lateinit var txtCodigo: EditText
    lateinit var txtNombre: EditText
    lateinit var txtPrecio: EditText
    lateinit var cbCategoria: Spinner
    lateinit var btnAgregar: Button
    lateinit var btnConsultar: Button
    lateinit var btnEliminar: Button
    lateinit var btnActualizar: Button
    lateinit var listaCategorias: MutableList<categoria>
    private var idCategoria: Int = 0
    private var idProducto: Int = 0







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtCodigo = findViewById(R.id.txtCodigo)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnActualizar = findViewById(R.id.btnActualizar)
        cbCategoria = findViewById(R.id.cbCategoria)
        listaCategorias = mutableListOf<categoria>()

        obtenerCategorias()

       btnAgregar.setOnClickListener { agregar()}
        btnConsultar.setOnClickListener { consultar() }
        btnEliminar.setOnClickListener { eliminar() }
        /*btnActualizar.setOnClickListener { actualizar() }*/

        cbCategoria.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
               idCategoria = listaCategorias[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val adaptador = ArrayAdapter<categoria>(this, android.R.layout.simple_spinner_dropdown_item,listaCategorias)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cbCategoria.adapter=adaptador



    }
    private fun obtenerCategorias(){
        val url = "https://alexxoo.pythonanywhere.com/categoria"
        val queue = Volley.newRequestQueue(this)
        val jsonCategorias = JsonArrayRequest(Request.Method.GET, url, null,
            Response.Listener<JSONArray>() {response ->
            try {
                val jsonArray=response
                for (i in 0 until jsonArray.length()){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val id = jsonObject.getString("id")
                    val nombre = jsonObject.getString("catNombre")
                    var categoria = categoria(id.toInt(), nombre)
                    listaCategorias.add(categoria)
                }
            }catch (e: JSONException){
                e.printStackTrace()
            }
        },Response.ErrorListener { error ->
            Toast.makeText(this,error.toString(), Toast.LENGTH_LONG).show()
    })
        queue.add(jsonCategorias)
    }
    private  fun agregar(){
        val url = "https://alexxoo.pythonanywhere.com/producto"
        val queue = Volley.newRequestQueue(this )
        val resultadoPost = object  : StringRequest(Request.Method.POST,url,
            Response.Listener<String>{response ->
                Toast.makeText(this, "Producto Agregado Correctamente", Toast.LENGTH_LONG).show()
                limpiar()
            }, Response.ErrorListener { error ->
            Toast.makeText(this, "Error ${error.message}", Toast.LENGTH_LONG).show()
        }){
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("proCodigo", txtCodigo.text.toString())
                parametros.put("proNombre", txtNombre.text.toString())
                parametros.put("proPrecio", txtPrecio.text.toString())
                parametros.put("proCategoria", idCategoria.toString())
                return parametros
            }
        }
        queue.add(resultadoPost)
    }

    private fun consultar(){
        val id = txtCodigo.text.toString()
        var url = "https://alexxoo.pythonanywhere.com/producto/$id"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                txtCodigo.setText(response.getString("proCodigo"))
                txtNombre.setText(response.getString("proNombre"))
                txtPrecio.setText(response.getString("proPrecio"))
                idProducto = response.getString("id").toInt()

            }, Response.ErrorListener { error ->
                Toast.makeText(this,error.toString(), Toast.LENGTH_LONG).show()
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun eliminar(){
        var url = "https://alexxoo.pythonanywhere.com/producto/$idProducto"
        val queue = Volley.newRequestQueue(this)
        val resultadoPost = object : StringRequest(Request.Method.DELETE,url,
            Response.Listener {response ->  
                Toast.makeText(this, "Producto Eliminado", Toast.LENGTH_LONG).show()
                limpiar()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error Al Eliminar el producto $error" ,Toast.LENGTH_LONG ).show()
            }
        ){

        }
        queue.add(resultadoPost)
    }

    /*private  fun actualizar(){
        val url = "https://alexxoo.pythonanywhere.com/producto/$idProducto"
        val queue = Volley.newRequestQueue(this )
        val resultadoPost = object  : StringRequest(Request.Method.PUT,url,
            Response.Listener<String>{response ->
                Toast.makeText(this, "Producto Actualizado Correctamente", Toast.LENGTH_LONG).show()
                limpiar()
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "Error ${error.message}", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val parametros = HashMap<String,String>()
                parametros.put("proCodigo", txtCodigo.text.toString())
                parametros.put("proNombre", txtNombre.text.toString())
                parametros.put("proPrecio", txtPrecio.text.toString())
                parametros.put("proCategoria", "id")
                return parametros
            }
        }
        queue.add(resultadoPost)
    }
*/

fun limpiar(){
    txtCodigo.text.clear()
    txtNombre.text.clear()
    txtPrecio.text.clear()
}

}