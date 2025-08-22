package com.example.api_scanner.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.api_scanner.MainActivity
import com.example.api_scanner.R
import com.example.api_scanner.altaPrinter.MainGestionAlmacen
import com.example.api_scanner.connection.connections
import com.example.api_scanner.printer.MainUbicationPrinter
import com.example.api_scanner.scanner.MainScanner
import com.google.gson.Gson

class FragmentGestionAlmacen : Fragment() {

    companion object {
        const val YOUR_REQUEST_CODE = 1
        const val ALTA_REQUEST_CODE = 2
    }

    private lateinit var btn: Button
    private lateinit var btnScanner: Button
    var CodSAS = "685362O"

    //Iniciar la conexion
    var conecction = connections()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_gestion_almacen, container, false)


        btnScanner = view.findViewById(R.id.btnAlta)

        btn = view.findViewById(R.id.btnServer) //Borrar despues
        startScanner()
        submitData()

        return view
    }

    fun startScanner() {

        btnScanner.setOnClickListener {
            val intent = Intent(context, MainScanner::class.java)
            startActivityForResult(intent, ALTA_REQUEST_CODE) // Usa startActivityForResult
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            YOUR_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val codigo = it.getStringExtra("codigo")
                        val format = it.getStringExtra("format")
                        if (format == "CODE_39" && !codigo.isNullOrEmpty()) {
                            CodSAS = codigo
                            println(CodSAS)
                            Toast.makeText(context, "Codigo Mansis Obtenido", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Codigo Incorrecto ", Toast.LENGTH_LONG).show()
                        }

                        conecction.connectionGestionAlmacen(
                            CodSAS,
                            CodSAS,
                            requireContext()
                        ) { response ->
                            // Manejar la respuesta del servidor
                            if (response != null) {
                                try {
                                    if (response.success) {
                                        val mv = Intent(context, MainUbicationPrinter::class.java)

                                        //Manejar el json
                                        val gson = Gson()
                                        val jsonData: String = gson.toJson(response.data)

                                        mv.putExtra("message", response.message)
                                        mv.putExtra("data", jsonData)
                                        startActivity(mv)
                                    } else {
                                        Log.e(
                                            "ALEJANDRO",
                                            "Error en la respuesta del servidor: ${response.message}"
                                        )
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "ALEJANDRO",
                                        "Error al parsear la respuesta: ${e.message}"
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error en la conexion a internet, comprueba la conexiona a la red",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("ALEJANDRO", "No se recibió respuesta del servidor.")
                            }

                        }

                    }


                } else {
                    var intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            ALTA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val codigo = it.getStringExtra("codigo")
                        val format = it.getStringExtra("format")
                        if (format == "CODE_39" && !codigo.isNullOrEmpty()) {
                            CodSAS = codigo
                            Toast.makeText(context, "Codigo Mansis Obtenido", Toast.LENGTH_LONG)

                            conecction.connectionGestionAlmacen(
                                CodSAS,
                                CodSAS,
                                requireContext()
                            ) { response ->
                                // Manejar la respuesta del servidor
                                if (response != null) {
                                    try {
                                        if (response.success) {
                                            val mv = Intent(context, MainGestionAlmacen::class.java)

                                            //Manejar el json
                                            val gson = Gson()
                                            val jsonData: String = gson.toJson(response.data)

                                            mv.putExtra("message", response.message)
                                            mv.putExtra("data", jsonData)
                                            startActivity(mv)
                                        } else {
                                            Log.e(
                                                "ALEJANDRO",
                                                "Error en la respuesta del servidor: ${response.message}"
                                            )
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "ALEJANDRO",
                                            "Error al parsear la respuesta: ${e.message}"
                                        )
                                    }
                                } else {

                                }
                            }
                        } else {
                            Toast.makeText(context, "Codigo Incorrecto ", Toast.LENGTH_LONG)
                        }
                    }
                }
            }

            else -> {
                Toast.makeText(context, "REQUEST NO ENCONTRADO", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun submitData() { // Funcion para enviar la peticion a GestionAlmacen
        btn.setOnClickListener {
            conecction.connectionGestionAlmacen(CodSAS, CodSAS, requireContext()) { response ->

                // Manejar la respuesta del servidor
                if (response != null) {
                    try {
                        if (response.success) {
                            val mv = Intent(context, MainGestionAlmacen::class.java)

                            //Manejar el json
                            val gson = Gson()
                            val jsonData: String = gson.toJson(response.data)

                            mv.putExtra("message", response.message)
                            mv.putExtra("data", jsonData)
                            startActivity(mv)
                        } else {
                            Log.e(
                                "ALEJANDRO",
                                "Error en la respuesta del servidor: ${response.message}"
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "ALEJANDRO",
                            "Error al parsear la respuesta: ${e.message}"
                        )
                    }
                } else {
                    Log.e("ALEJANDRO", "No se recibió respuesta del servidor.")

                }
            }

        }
    }

}