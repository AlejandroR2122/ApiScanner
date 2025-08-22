package com.example.api_scanner.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.api_scanner.MainActivity
import com.example.api_scanner.R
import com.example.api_scanner.connection.connections
import com.example.api_scanner.printer.MainChangeUbicationPrinter
import com.example.api_scanner.printer.MainUbicationPrinter
import com.example.api_scanner.scanner.MainScanner
import com.google.gson.Gson


class FragmentDetailsPrinter : Fragment() {

    companion object {
        const val YOUR_REQUEST_CODE = 1

    }

    private lateinit var btn: Button     //BORRAR DESPUES BTN DE PRUEBA !!!!!!
    private lateinit var btnScanner: Button
    var CodSAS = "685362O"

    //Iniciar la conexion
    var conecction = connections()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del fragment
        val view = inflater.inflate(R.layout.fragment_details_layout, container, false)

        btnScanner = view.findViewById(R.id.btnScanDetails)
        btn = view.findViewById(R.id.btnServer)    //BORRAR DESPUES BTN DE PRUEBA !!!!!!
        startScanner()   // Configuramos el boton scanner

        submitData()   // Boton submit predefinido
        return view
    }

    fun startScanner() {

        btnScanner.setOnClickListener {
            val intent = Intent(context, MainScanner::class.java)
            startActivityForResult(intent, YOUR_REQUEST_CODE) // Usa startActivityForResult
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

                            Toast.makeText(context, "Codigo SAS Obtenido", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Codigo Incorrecto ", Toast.LENGTH_LONG).show()
                        }

                        conecction.sendMessageToServer(
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

                                    }
                                } catch (e: Exception) {

                                }
                            } else {

                            }

                        }

                    }


                } else {
                    println("FALLO")
                    var intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            else -> {
                Toast.makeText(context, "REQUUEST NO ENCONTRADO", Toast.LENGTH_LONG).show()

            }

        }

    }

    fun submitData() {
        btn.setOnClickListener {
            btn.isEnabled = false // Desactiva temporalmente el botón
            conecction.sendMessageToServer(
                MainScanner.CODIGO_SAS,
                MainScanner.CODIGO_SAS,
                requireContext()
            ) { response ->
                println("Respuesta completa: $response")
                try {
                    if (response != null) {
                        val estado = response.data.estado ?: ""
                        println("ESTADO: $estado")
                        when (estado) {
                            "A" -> {
                                val mv = Intent(requireContext(), MainUbicationPrinter::class.java)
                                val jsonData = Gson().toJson(response.data)
                                mv.putExtra("message", response.message)
                                mv.putExtra("data", jsonData)
                                println("Navegando a MainUbicationPrinter")
                                startActivity(mv)
                            }
                            "U", "I" -> {
                                val mv = Intent(requireContext(), MainChangeUbicationPrinter::class.java)
                                val jsonData = Gson().toJson(response.data)
                                mv.putExtra("message", response.message)
                                mv.putExtra("data", jsonData)
                                println("Navegando a MainChangeUbicationPrinter")
                                startActivity(mv)
                            }
                            else -> {
                                println("Estado no reconocido: $estado")
                            }
                        }
                    } else {
                        println("Respuesta nula del servidor")
                        Toast.makeText(
                            context,
                            "No se recibió respuesta del servidor",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Log.e("ALEJANDRO", "Error general: ${e.message}", e)
                } finally {
                    btn.isEnabled = true // Reactiva el botón
                }
            }
        }
    }

}

