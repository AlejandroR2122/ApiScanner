package com.example.api_scanner.altaPrinter

import SpinnerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.api_scanner.MainActivity
import com.example.api_scanner.R
import com.example.api_scanner.connection.DataIncidencia
import com.example.api_scanner.connection.ImpresoraData
import com.example.api_scanner.connection.connections
import com.google.gson.Gson
import java.util.Calendar

class VentanaModal : DialogFragment() {
    private lateinit var objetoDataModificated: ImpresoraData
    private lateinit var DataIncidencia: DataIncidencia
    private var url: String? = null
    private var urlPrivate: String? = null

    companion object {
        fun newInstance(url: String): VentanaModal {
            return VentanaModal().apply {
                // Añadimos
                urlPrivate= url
               /* arguments = Bundle().apply {
                    putString("url", url)
                }

                */
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_incidencias, container, false)
        setupMotivos(view)
        receiveData()  // Esto es lo primero Recibir los datos
        setupSendIncidencia(view)
        setupAlmacenIncidencias(view)
        btnCancel(view)
        return view
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = 1000
            val height = 1400
            dialog.window?.setLayout(width, height)  // Ajusta el tamaño
        }
    }


    fun setupMotivos(view: View) {
        val SpinnerMotivos: Spinner = view.findViewById(R.id.spinnerMotivos)
        val connection = connections()
        connection.getTypeAverias { dataAverias ->
            if (dataAverias != null) {
                // Si los datos se han recibido correctamente, llenamos el Spinner
                val motivosList =
                    dataAverias.map { it.nombre } // Asumimos que DataAverias tiene un campo 'motivo'
                SpinnerAdapter(
                    requireContext(),
                    SpinnerMotivos,
                    dataAverias,
                    { it.nombre },
                    { it.id },
                    { null })

            } else {
                // Si no se recibieron datos o ocurrió un error, mostramos un mensaje
                Toast.makeText(view.context, "Error al cargar los motivos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
fun setupAlmacenIncidencias(view:View){
    view.findViewById<TextView>(R.id.txtAlmacenRecogida).text = objetoDataModificated.almacen
}
    fun updateStatusTime(){
        objetoDataModificated.estado = "R"
        objetoDataModificated.fechaMod = obtenerFechaHoyEnMilisegundos()
    }
    fun setupSendIncidencia(view: View) {
        view.findViewById<Button>(R.id.btnSend).setOnClickListener {
            extracted(view)
            updateStatusTime()
            val conexion = connections()

            if (DataIncidencia.id != null) {
                // Llamada a la función con el callback para manejar la respuesta
                when (urlPrivate) {
                    "averiaImpresora" -> {
                        conexion.sendAveriaImpresora(
                            DataIncidencia.id,
                            DataIncidencia
                        ) { apiResponse ->
                            println(apiResponse)
                            if (apiResponse.status == 200) {
                                Toast.makeText(
                                    context,
                                    "Incidencia enviada correctamente, impresora vuelta a reparacion",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                dismiss()
                                var intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            } else {

                                var intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                    "averiaPrealmacen" -> {
                        conexion.sendIncidenciaPreAlmacen(
                            DataIncidencia.id,
                            DataIncidencia
                        ) { apiResponse ->
                            println(apiResponse)
                            if (apiResponse.status == 200) {
                                Toast.makeText(
                                    context,
                                    "Incidencia enviada correctamente, impresora vuelta a reparacion",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                dismiss()
                                var intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            } else {

                                var intent = Intent(context, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }

            } else {
                println("ID de DataIncidencia es nulo, no se puede enviar")
            }
        }
    }

    private fun extracted(view: View) {
        // Instancio la clase DataIncidencia
        DataIncidencia = DataIncidencia(
            id = 0,
            numSerie = "",
            fechaAveria = 0,  // Fecha actual en milisegundos
            usuarioTecnico = "Alejandro",
            idarea = 0,  // Por ejemplo, área con ID 2
            almacen = "",
            motivoAveria = "",
            provincia = 0,  // ID de la provincia
            copiasEntradaAveria = 0,  // Número de copias de entrada
            copiasSalidaAveria = 0,
            incidencia = null
        )
        DataIncidencia.id = objetoDataModificated.id
        DataIncidencia.numSerie = objetoDataModificated.numSerie
        DataIncidencia.fechaAveria = updateFechaIncidencia()
        //  DataIncidencia.usuarioTecnico = objetoDataModificated.usuario
        DataIncidencia.idarea = objetoDataModificated.idarea
        DataIncidencia.motivoAveria = getValueIncidencia(view)
        DataIncidencia.provincia = objetoDataModificated.idprovincia
        DataIncidencia.almacen = objetoDataModificated.almacen
        DataIncidencia.copiasEntradaAveria =
            objetoDataModificated.valactuales  // REVISAR SI SE REFIERE A ESTO
        DataIncidencia.incidencia = view.findViewById<EditText>(R.id.EditTextNumeroIncidencia).text.toString().toIntOrNull()

    }
    fun sendStatusChange() {
        val connection = connections()
        connection.sendPrinterUpdate(objetoDataModificated.id,
            objetoDataModificated,
            onResponse = { responseString ->
                Toast.makeText(context, "Impresora actualizada, puesta en almacén", Toast.LENGTH_LONG)
                    .show()
                // Manejar la respuesta exitosa aquí
                println("Respuesta recibida: $responseString")
                // Aquí puedes actualizar la UI o realizar otra acción
            },
            onFailure = { throwable ->
                // Manejar el error aquí
                Toast.makeText(context, "Error al modficiar la impresora", Toast.LENGTH_LONG).show()

                println("Errores: ${throwable.message}")
                // Aquí puedes mostrar un mensaje de error al usuario o registrar el error
            })

    }

    fun getValueIncidencia(view: View): String {
        val spinnerMotivos: Spinner = view.findViewById(R.id.spinnerMotivos)
        val descripcionAveria: EditText = view.findViewById(R.id.TextAreaDescripcionAveria)
        val textoDescripcionAveria: String = descripcionAveria.text.toString()
        val selectedValue = spinnerMotivos.selectedItem.toString()
        val returnIncidencia = selectedValue + " /:" + textoDescripcionAveria
        return returnIncidencia
    }

    fun updateFechaIncidencia(): Long {
        val currentTimeMillis = System.currentTimeMillis()  // Milisegundos actuales
        return currentTimeMillis
    }

    fun receiveData() {
        // Recuperar el JSON desde los argumentos
        val json = arguments?.getString("dataModificated")
        if (json != null) {
            // Convertir el JSON de nuevo a un objeto ResponseData usando Gson
            val gson = Gson()
            objetoDataModificated = gson.fromJson(json, ImpresoraData::class.java)
        }


    }
    fun obtenerFechaHoyEnMilisegundos(): Long {
        // Obtener la fecha de hoy (día, mes, año) usando Calendar
        val calendar = Calendar.getInstance()

        // Establecer la hora a medianoche (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Obtener los milisegundos desde la época UNIX
        return calendar.timeInMillis
    }
    fun btnCancel(view: View) {
        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {

            Toast.makeText(context, "Incidencia cancelada", Toast.LENGTH_SHORT)
                .show()

            dismiss()

        }
    }
}