package com.example.api_scanner.scanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.api_scanner.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException

class MainScanner : AppCompatActivity() {
    object SCAN_REQUEST {
        const val SCAN_REQUEST_CODE = 1
        const val ALTA_REQUEST_CODE = 2
    }

    companion object {
        var CODIGO_SAS: String = "685362O"
        var CODIGO_Mansis: String = ""
    }

    private lateinit var binding: ActivityMainBinding
    private var qrScanIntegrator: IntentIntegrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
//Esta parte de arriba sirve para poder manipular los componentes del view xml sin tener que usar la id

        setContentView(view)  // Se mostrara primero view que es el root

        //    setOnClickListener() // Se llama a la funcion que gestiona el click del scaner
        setupScanner() // Inicializa la variable qrScanIntegrato, que es una clase que gestionara el scanner
        startScanner()


    }


    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    fun startScanner() {
        // Acción cuando se hace clic en el botón.
        qrScanIntegrator?.setRequestCode(SCAN_REQUEST.SCAN_REQUEST_CODE);
        // Mejora: las startActivityForResult (funciones que se ejecutan y devuelve una respuesta)
        // pueden identificarse con setRequestCode para especificar luego a que funion hacen referencia

        qrScanIntegrator?.initiateScan() // Ejecuta la funcion Scan que abre la camara y escanea codigo
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SCAN_REQUEST.SCAN_REQUEST_CODE -> {
                // Verifica si el resultado es OK
                if (resultCode == Activity.RESULT_OK) {
                    val result = IntentIntegrator.parseActivityResult(resultCode, data)

                    if (result != null) {
                        if (result.contents == null) {
                            Toast.makeText(this, "Error result Null", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            // Si el código QR contiene datos.
                            try {
                                if (result.formatName == "CODE_39") {
                                    //CODIGO DE BARRAS SAS
                                    //Binding.codigoSAS.setText(result.contents);

                                    intent.putExtra("codigo", result.contents)
                                    intent.putExtra("format", result.formatName)

                                    setResult(
                                        Activity.RESULT_OK,
                                        intent
                                    ) // Necesitas devolver OK para que onActivityResult se ejecute
                                    finish() // Cierra la actividad para que onActivityResult se ejecute
                                    CODIGO_SAS = result.contents

                                } else if (result.formatName == "QR_CODE") {
                                    //CODIGO QR
                                    intent.putExtra("codigo", result.contents)
                                    intent.putExtra("format", result.formatName)
                                    // CODIGO_Mansis = result.contents

                                    setResult(
                                        Activity.RESULT_OK,
                                        intent
                                    ) // Necesitas devolver OK para que onActivityResult se ejecute
                                    finish() // Cierra la actividad para que onActivityResult se ejecute
                                } else {
                                    Toast.makeText(
                                        this,
                                        "EL CODIGO NO COINCIDE CON LO ESPERADO ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                }


                            } catch (e: JSONException) {
                                e.printStackTrace()
                                //                      binding.CodigoUbicacion.setText(result.contents);
                                CODIGO_Mansis = result.contents
                                Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        //             binding.CodigoUbicacion.setText("RESULT=NULL")
                        CODIGO_Mansis = "RESULT=NULL"

                        Toast.makeText(this, "Resultado null", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    // Manejar otros resultados o errores aquí si es necesario
                    //      binding.CodigoUbicacion.setText("RESULT=CANCELLED")
                    Toast.makeText(this, "Erro en el Scanneo", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            SCAN_REQUEST.ALTA_REQUEST_CODE -> {

                // Verifica si el resultado es OK
                if (resultCode == Activity.RESULT_OK) {
                    val result = IntentIntegrator.parseActivityResult(resultCode, data)

                    if (result != null) {
                        if (result.contents == null) {
                            CODIGO_Mansis = "RESULT.CONTENT=NULL"
                        } else {
                            // Si el código QR contiene datos.
                            try {
                                if (result.formatName == "CODE_39") {


                                } else if (result.formatName == "QR_CODE") {

                                } else {
                                    Toast.makeText(
                                        this,
                                        "EL CODIGO NO COINCIDE CON LO ESPERADO ",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }


                            } catch (e: JSONException) {
                                e.printStackTrace()

                                CODIGO_Mansis = result.contents
                                Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {

                        Toast.makeText(this, "Resultado Null", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {

                    Toast.makeText(this, "Erro en el Scanneo", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

    }
}