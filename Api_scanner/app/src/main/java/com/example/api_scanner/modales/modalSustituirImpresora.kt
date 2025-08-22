package com.example.api_scanner.modales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.api_scanner.R
import com.example.api_scanner.connection.DataArea
import com.example.api_scanner.connection.ImpresoraData
import com.example.api_scanner.connection.connections
import com.google.gson.Gson
import SpinnerAdapter
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import com.example.api_scanner.MainActivity
import com.example.api_scanner.connection.DataServicio
import com.example.api_scanner.connection.consulta
import com.example.api_scanner.connection.plantas

class modalSustituirImpresora : DialogFragment() {
    private lateinit var impresoraDataReparacion: ImpresoraData
    private lateinit var impresoraPrincipal: ImpresoraData
    private lateinit var impresoraFinalEditada:ImpresoraData
    private lateinit var valuesArea: List<DataArea>
    private lateinit var dataServicio: List<DataServicio>
    private lateinit var valuesConsultas: List<consulta>

    // Declarar variables globales del view
    private lateinit var telefonoView: EditText
    private lateinit var ubicacionView: EditText
    private lateinit var codMansisView: EditText
    private lateinit var numSerieView: TextView
    private lateinit var matriculaView: TextView
    private lateinit var personaEncargadaView: EditText
    private lateinit var usuarioRecepcionView: EditText
    private lateinit var ipView: EditText
    private lateinit var btnCancelarView: Button
    private lateinit var btnConfirmarView: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_sustituir_impresora, container, false)

        receiveData()
        setupSpinners(view)
        setViewData(view)
        println(impresoraDataReparacion)
        return view
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = 1100
            val height = 1700
            dialog.window?.setLayout(width, height)  // Ajusta el tamaño
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

    // recojo los datos pasados como argumentos
    fun receiveData() {
        // Recuperar el JSON del argumento
        val jsonData = arguments?.getString("dataModificated")
        val jsonImpresoraPrincipal = arguments?.getString("ImpresoraPrincipal")

        // Deserializar el JSON a un objeto usando Gson
        jsonData?.let {
            val gson = Gson()
            impresoraDataReparacion = gson.fromJson(it, ImpresoraData::class.java)
        }
        jsonImpresoraPrincipal?.let {
            val gson = Gson()
            impresoraPrincipal=gson.fromJson(it, ImpresoraData::class.java)
        }

        // Copio la impresora Principal en el objeto que mandaré
        impresoraFinalEditada = impresoraPrincipal.copy()

    }


    fun setViewData(view: View) {
        // Asignar las vistas a las variables globales
        telefonoView = view.findViewById<EditText>(R.id.editTextTelefono).apply {
            setText(impresoraDataReparacion.telefono)
        }
        ubicacionView = view.findViewById<EditText>(R.id.editTextUbicacion).apply {
            setText(impresoraDataReparacion.ubicacion)
        }
        codMansisView = view.findViewById<EditText>(R.id.editTextcodMansis).apply {
            setText(impresoraDataReparacion.codMansis)
        }
        numSerieView = view.findViewById<TextView>(R.id.textViewNumSerie).apply {
            text = impresoraFinalEditada.numSerie
        }
        matriculaView = view.findViewById<TextView>(R.id.textViewMatricula).apply {
            text = impresoraFinalEditada.matricula
        }
        personaEncargadaView = view.findViewById<EditText>(R.id.editTextPersonaEncargada).apply {
            setText(impresoraDataReparacion.persona)
        }
        usuarioRecepcionView = view.findViewById<EditText>(R.id.editTextUsuarioRecepcion).apply {
            setText(impresoraDataReparacion.usuarioRecepcion)
        }
        ipView = view.findViewById<EditText>(R.id.editTextIp).apply {
            setText(impresoraDataReparacion.ip)
        }
        btnCancelarView = view.findViewById<Button>(R.id.btnCancelar).apply {
            setOnClickListener { dismiss() }
        }
        btnConfirmarView = view.findViewById<Button>(R.id.btnConfirmar).apply {
            setOnClickListener {
                getImpresoraModificatedImpresora()
                println("Esto es impresora Data $impresoraFinalEditada")
                sendSustituirImpresora()
            }
        }
    }

    fun setIpMask(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (isFormatting) return
                isFormatting = true
                var cleanString = charSequence.toString().replace(Regex("[^0-9]"), "")

                if (cleanString.length > 12) { // Maximo 3 dígitos por octeto
                    cleanString = cleanString.substring(0, 12)
                }

                val formattedString = StringBuilder()

                // Aplicar la máscara de la IP: xxx.xxx.xxx.xxx
                for (i in cleanString.indices) {
                    if (i == 3 || i == 6 || i == 9) {
                        formattedString.append(".")
                    }
                    formattedString.append(cleanString[i])
                }

                editText.setText(formattedString)
                editText.setSelection(formattedString.length)
                isFormatting = false
            }

            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // No hacer nada antes de que el texto cambie
            }

            override fun afterTextChanged(editable: Editable?) {
                // No hacer nada después de que el texto cambió
            }
        })
    }

    fun sendSustituirImpresora() {
        val connection = connections()
        connection.sendPrinterUpdate(impresoraFinalEditada.id,
            impresoraFinalEditada,
            onResponse = { responseString ->
                //Respuesta recibida
                Toast.makeText(
                    requireContext(),
                    "Impresora actualizada correctaemnte",
                    Toast.LENGTH_LONG
                )
                    .show()
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)


            }) { response ->
        }
    }

    fun getImpresoraModificatedImpresora() {
        impresoraFinalEditada.persona = personaEncargadaView.text.toString()
        impresoraFinalEditada.usuarioRecepcion = usuarioRecepcionView.text.toString()
        impresoraFinalEditada.ip = ipView.text.toString()
        impresoraFinalEditada.telefono = telefonoView.text.toString()
        impresoraFinalEditada.ubicacion = ubicacionView.text.toString()
        impresoraFinalEditada.codMansis= codMansisView.text.toString()
    }

    fun setupSpinners(view: View) {
        areaResponse(impresoraDataReparacion, view)
        consultasResponse(view)
        ListServiciosResponse(view)
        plantaSpinner(plantas, impresoraDataReparacion, view)

        // MASCARA IP
        setIpMask(view.findViewById(R.id.editTextIp))
    }

    /*
     * Los spinners tanto "area" "consulta" "servicio" y "planta" se definen aqui
     *  Se ejecutan en setupSpinners
     *
     */

    // lista area
    private fun areaResponse(objetoData: ImpresoraData, view: View) {
        val connection = connections()
        connection.sendArea(objetoData.idarea.toString()) { response ->
            if (response != null) {
                // Creamos el valor null vacio del spinner
                val AreaNull = DataArea(
                    id = null,
                    nombre = "Selecciona un area",
                    idarea = null,
                    hospital = "Selecciona una consulta ",
                    Vlan = null,
                    almacen = null
                )
                // creamos la lista del spinner con el valor null dentro
                valuesArea = listOf(AreaNull) + response

                // SPINNER CENTROS View
                val SpinnerCentros = view.findViewById<Spinner>(R.id.spinnerCentros)

                // Inicializamos el adaptador con la lista de servicios
                SpinnerAdapter(
                    requireContext(),
                    SpinnerCentros,
                    valuesArea,
                    { it.nombre },
                    { it.id },
                    { idSelected -> impresoraFinalEditada.idcentro = idSelected })
                //Seleccionar Centro
                val indexCentros = valuesArea.indexOfFirst { it.id == objetoData.idcentro }
                SpinnerCentros.setSelection(indexCentros)

            }
        }
    }

    //lista ListServicio
    private fun ListServiciosResponse(view: View) {
        val connection = connections()
        connection.sendListServicios { response ->
            if (response != null) {
                // Creamos el valor null vacio
                val ServiciosNull = DataServicio(id = null, nombre = "Selecciona un servicio")

                dataServicio =
                    listOf(ServiciosNull) + response  // Agregar el elemento de "Selecciona un servicio"

                // Asegúrate de que la lista no sea null
                val SpinnerListServicios = view.findViewById<Spinner>(R.id.spinnerServicios)

                // Inicializamos el adaptador con la lista de servicios
                SpinnerAdapter(
                    requireContext(),
                    SpinnerListServicios,
                    dataServicio,
                    { it.nombre },
                    { it.id },
                    { selectedId -> impresoraFinalEditada.idservicio = selectedId })

                // Si hay un servicio seleccionado, lo marcamos
                val indexServicio = dataServicio.indexOfFirst { it.id == impresoraDataReparacion.idservicio }
                if (indexServicio >= 0) {
                    SpinnerListServicios.setSelection(indexServicio)
                } else {
                    // Si el servicio no está en la lista, seleccionamos el primer elemento (por ejemplo, "Selecciona un servicio")
                    SpinnerListServicios.setSelection(0)
                }


            }
        }
    }

    // lista consulta
    private fun consultasResponse(view: View) {
        val connection = connections()
        connection.consultasSend { response ->
            if (response != null) {
                val nullConsulta = consulta(id = null, nombre = "Seleccione una opción")

                // Añadir el objeto nulo al principio de la lista de consultas
                valuesConsultas = listOf(nullConsulta) + response
                val spinner = view.findViewById<Spinner>(R.id.spinnerConsulta)
                SpinnerAdapter(
                    requireContext(),
                    spinner,
                    valuesConsultas,
                    { it.nombre },
                    { it.id },
                    { idSelected -> impresoraFinalEditada.idconsulta = idSelected })


                // ESTO NO VA AQUI TENGO QUE CAMBIARLO
                /***

                rellenarCampos no tiene que estar aqui Periodo pruebas

                 ***/
            } else {

                // Manejar el caso cuando la respuesta es nula si es necesario
            }
        }
    }

    // SPINNER PLANTA
    private fun plantaSpinner(planta: List<String>, data: ImpresoraData, view: View) {
        val spinner = view.findViewById<Spinner>(R.id.SpinnerPlanta)

        // Crear el ArrayAdapter con la lista de opciones
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, planta)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Establecer el valor seleccionado en el Spinner basado en data.planta
        val selectedPosition = planta.indexOf(data.planta)

        if (selectedPosition >= 0) {
            // Si el valor existe en la lista, selecciona el valor en el Spinner
            spinner.setSelection(selectedPosition)
        } else {
            // Si el valor no se encuentra en la lista, puedes establecer un valor por defecto o manejarlo
            spinner.setSelection(0)  // O alguna otra lógica si es necesario
        }

        // Establecer el listener si necesitas capturar la opción seleccionada
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = planta[position]
                impresoraFinalEditada.planta = selectedItem
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // No se seleccionó nada
            }
        }
    }
}