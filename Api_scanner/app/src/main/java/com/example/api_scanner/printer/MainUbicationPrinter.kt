package com.example.api_scanner.printer


import SpinnerAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.api_scanner.MainActivity
import com.example.api_scanner.R
import com.example.api_scanner.altaPrinter.VentanaModal
import com.example.api_scanner.connection.DataArea
import com.example.api_scanner.connection.DataServicio
import com.example.api_scanner.connection.ImpresoraData
import com.example.api_scanner.connection.RetrofitInstance
import com.example.api_scanner.connection.connections
import com.example.api_scanner.connection.consulta
import com.example.api_scanner.connection.plantas
import com.example.api_scanner.databinding.ActivityMainUbicationPrinterBinding
import com.example.api_scanner.fragments.FragmentDetailsPrinter.Companion.YOUR_REQUEST_CODE
import com.example.api_scanner.scanner.MainScanner
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainUbicationPrinter : AppCompatActivity() {
    private var baseUrl: String = RetrofitInstance.BASE_URL
    private lateinit var binding: ActivityMainUbicationPrinterBinding
    private lateinit var valuesConsultas: List<consulta>
    private lateinit var valuesArea: List<DataArea>
    private lateinit var dataServicio: List<DataServicio>
    private lateinit var valuesImpresoraModificadosUbicacion: ImpresoraData
    private lateinit var objetoDataRecibido: ImpresoraData
    private lateinit var valueReparacion: List<ImpresoraData>
    private lateinit var impresoraSustituta: ImpresoraData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa el binding
        binding = ActivityMainUbicationPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extracted()     // Extraer informacion del fragment
        hideSystemUI() // eliminar botones ui

        btnIncidencia()  // Crear una boton de incidencia

        setIpMask(binding.editTextIp)  // Mascara IP
        // Llamar a la función para cargar las consultas
        consultasResponse(objetoDataRecibido)
        areaResponse(objetoDataRecibido)
        plantaSpinner(plantas, objetoDataRecibido)
        getModelo() // Coger modelo de la impresora
        //CENTRO
        CentroResponse(objetoDataRecibido)
        //Servicios
        ListServiciosResponse(objetoDataRecibido)
        checkStateUSB() // Btn USB
        setAccordionUbicacion()
        // REPARACION SPINNEr
        setupSpinnerReparacion()
        // Ejecutar CodMansis
        findViewById<Button>(R.id.btnCodMansis).setOnClickListener {
            val intent = Intent(this, MainScanner::class.java)
            startActivityForResult(intent, YOUR_REQUEST_CODE) // Usa startActivityForResult

        }
        findViewById<Button>(R.id.btnEnviar).setOnClickListener {
            sendDataModificated()
        }

        setVisibleAccordionIncidencias(binding)
    }

    // Recupero los datos exraidos por de la peticion al servidor en el escaneo principal hecho en el main
    private fun extracted() {
        // Recuperar el dato usando la misma clave
        val data = intent.getStringExtra("data")
        // Verificar que 'data' no sea nulo antes de convertirlo
        if (data != null) {
            // Transformar a Objeto
            val gson = Gson()
            // Guardo el valor en la varaible global de la clase
            objetoDataRecibido = gson.fromJson(data, ImpresoraData::class.java)
            valuesImpresoraModificadosUbicacion = objetoDataRecibido.copy()
        }
    }

    //Recibir consultas
    private fun consultasResponse(objetoData: ImpresoraData) {
        val connection = connections()
        connection.consultasSend { response ->
            if (response != null) {
                val nullConsulta = consulta(id = null, nombre = "Seleccione una opción")

                // Añadir el objeto nulo al principio de la lista de consultas
                valuesConsultas = listOf(nullConsulta) + response
                val spinner = findViewById<Spinner>(R.id.spinnerConsulta)
                SpinnerAdapter(
                    this,
                    spinner,
                    valuesConsultas,
                    { it.nombre },
                    { it.id },
                    { idSelected -> valuesImpresoraModificadosUbicacion.idconsulta = idSelected })


                // ESTO NO VA AQUI TENGO QUE CAMBIARLO
                /***

                rellenarCampos no tiene que estar aqui Periodo pruebas

                 ***/
                rellenarCampos(objetoData)
            } else {

                // Manejar el caso cuando la respuesta es nula si es necesario
            }
        }
    }

    //Recibir Planta
    private fun plantaSpinner(planta: List<String>, data: ImpresoraData) {
        val spinner = findViewById<Spinner>(R.id.SpinnerPlanta)

        // Crear el ArrayAdapter con la lista de opciones
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, planta)
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
                valuesImpresoraModificadosUbicacion.planta = selectedItem
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // No se seleccionó nada
            }
        }
    }

    //Recibir Area
    private fun areaResponse(objetoData: ImpresoraData) {
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
                val SpinnerCentros = findViewById<Spinner>(R.id.spinnerCentros)

                // Inicializamos el adaptador con la lista de servicios
                SpinnerAdapter(
                    this,
                    SpinnerCentros,
                    valuesArea,
                    { it.nombre },
                    { it.id },
                    { idSelected -> valuesImpresoraModificadosUbicacion.idcentro = idSelected })
                println("VALUES MODIFICADOS " + valuesImpresoraModificadosUbicacion.idcentro)
                //Seleccionar Centro
                val indexCentros = valuesArea.indexOfFirst { it.id == objetoData.idcentro }
                SpinnerCentros.setSelection(indexCentros)

            }
        }
    }

    //Recibir ListServicio
    private fun ListServiciosResponse(objetoData: ImpresoraData) {
        val connection = connections()
        connection.sendListServicios { response ->
            if (response != null) {
                // Creamos el valor null vacio
                val ServiciosNull = DataServicio(id = null, nombre = "Selecciona un servicio")

                dataServicio =
                    listOf(ServiciosNull) + response  // Agregar el elemento de "Selecciona un servicio"

                // Asegúrate de que la lista no sea null
                val SpinnerListServicios = findViewById<Spinner>(R.id.spinnerServicios)

                // Inicializamos el adaptador con la lista de servicios
                SpinnerAdapter(
                    this,
                    SpinnerListServicios,
                    dataServicio,
                    { it.nombre },
                    { it.id },
                    { selectedId -> valuesImpresoraModificadosUbicacion.idservicio = selectedId })

                // Si hay un servicio seleccionado, lo marcamos
                val indexServicio = dataServicio.indexOfFirst { it.id == objetoData.idservicio }
                if (indexServicio >= 0) {
                    SpinnerListServicios.setSelection(indexServicio)
                } else {
                    // Si el servicio no está en la lista, seleccionamos el primer elemento (por ejemplo, "Selecciona un servicio")
                    SpinnerListServicios.setSelection(0)
                }


            }
        }
    }


    /// Recibir CENTRO
    private fun CentroResponse(objetoData: ImpresoraData) {
        val connection = connections()
        connection.sendCentro(objetoData.idcentro.toString()) { response ->

            //  binding.editTextIdCentro.setText(response?.nombre)
        }
    }


    // RELLENAR CAMPOS del view
    private fun rellenarCampos(data: ImpresoraData) {
        binding.textViewCodigoSAS.text = data.codSAS
        binding.textViewName.setText(objetoDataRecibido.nombre)
        binding.editTextcodMansis.setText(data.codMansis ?: "")
        binding.editTextMatricula.setText(data.matricula)
        binding.textViewMac.text = data.mac
        binding.editTextIp.setText(data.ip)
        binding.textViewFirmware.text = data.firmware ?: ""
        checkStado(data.estado)
        setSrcImage(binding.imagePrinter, objetoDataRecibido.imagen)
        binding.textViewNumSerie.text = data.numSerie
        //  binding.editTextPrealmacen.setText(data.prealmacen.toString() ?: "")
        binding.textViewAlmacen.text = data.almacen ?: ""
        binding.editTextUsuarioRecepcion.setText(data.usuarioRecepcion ?: "")
        binding.textViewVlan.text = data.vlan
        //  binding.editTextUsuario.setText(data.usuario)
        binding.textViewValActuales.text = data.valactuales.toString()
        binding.textViewValIniciales.text = data.valiniciales.toString()
        binding.textViewNivelToner.text = data.nivelToner.toString()
        //  binding.editTextIdProvincia.setText(data.idprovincia.toString())
        //  binding.editTextIdArea.setText(data.idarea.toString())
        binding.editTextPersona.setText(data.persona ?: "")
        binding.editTextTelefono.setText(data.telefono ?: "")
        //   binding.editTextIdCentro.setText(data.idcentro.toString())
        checkConsulta(data.idconsulta ?: -1)  // Si no tiene idConulta se devuelve vacio
        //  binding.editTextIdServicio.setText(data.idservicio.toString())

        val fechaInstalacion = data.fechaInstalacion as? Long
        if (fechaInstalacion != null) {
            binding.textViewFechaInstalacion.text = changeTime(fechaInstalacion)
        }
        val fechaAlta = data.fechaalta as? Long
        if (fechaAlta != null) {
            binding.textViewFechaAlta.text = changeTime(fechaAlta)
            // binding.editTextFechaAlta.setText(changeTime(fechaAlta))
        }
        val fechaMod = data.fechaalta as? Long
        if (fechaMod != null) {
            binding.textViewFechaMod.text = changeTime(fechaMod)
        }
        val number = data.fechaBaja
        println(" cheee" + number)
        if (number == null) {
            binding.linearFechaBaja.visibility = View.GONE
        } else {
            binding.textViewFechaBaja.text = number.toString()
        }
        binding.textViewFechaBaja.text = data.fechaBaja?.toString() ?: ""
        binding.editTextUbicacion.setText(data.ubicacion)

    }

    // Modificador de tiempo a fecha
    fun changeTime(milisegundosTotales: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = Date(milisegundosTotales)
        return dateFormat.format(date)
    }

    // Modificar fecha impresora a tiempo
    private fun changeToSecond(tiempo: String?): Long? {
        // Si el tiempo es nulo o vacío, asignar null
        if (tiempo.isNullOrEmpty()) {
            return null
        }

        // Establecer el formato de la fecha
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        return try {
            // Parsear la fecha de la cadena de texto
            val date = dateFormat.parse(tiempo)

            // Si la fecha es válida, devolver los milisegundos
            date?.time
        } catch (e: Exception) {
            // Si el formato de la fecha es incorrecto, asignar null
            null
        }
    }

    // Modificar img impresora
    private fun setSrcImage(imageView: ImageView, imageUrlS: String?) {
        val imageUrl = baseUrl + "gesprint" + imageUrlS
        Glide.with(this) // Contexto actual, 'this' si es actividad o 'requireContext()' si es fragmento
            .load(imageUrl) // URL de la imagen
            .error(R.drawable.impresora_fuera_servicio)
            .into(imageView) // El ImageView donde se mostrará la imagen
    }

    // Cambiar estado de la impresora
    private fun checkStado(status: String) {    // La funcion que transforman los estados
        when (status) {
            "U" -> {
                binding.textViewEstado.text = "USB"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccept
                    )

                ) // Color para USB
                val btnUSB = binding.switchExample
                btnUSB.isChecked = true
                binding.editTextIp.apply {
                    setText("")
                    inputType = android.text.InputType.TYPE_NULL
                    isClickable = false
                    isFocusableInTouchMode = false
                    isEnabled = false

                }

            }

            "I" -> {
                binding.textViewEstado.text = "RED"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccept
                    )
                ) // Color para RED
            }

            "F" -> {
                binding.textViewEstado.text = "Fuera Almacen error inventario"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorError
                    )
                ) // Color para error inventario
            }

            "B" -> {
                binding.textViewEstado.text = "Retirada definitiva"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorRetirada
                    )
                ) // Color para retirada definitiva
            }

            "R" -> {
                binding.textViewEstado.text = "Reparacion"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorReparacion
                    )
                ) // Color para reparacion
            }

            "A" -> {
                binding.textViewEstado.text = "Almacen"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAlmacen
                    )
                ) // Color para almacen
            }

            "P" -> {
                binding.textViewEstado.text = "Pendiente entrar almacen"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccept
                    )
                ) // Color para pendiente
            }

            "N" -> {
                binding.textViewEstado.text = "Indefinido"
                binding.textViewEstado.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccept
                    )
                ) // Color para indefinido
            }
        }
    }

    // Cambiar estado de la impresora
    private fun changeStatus(status: String) {
        when (status) {
            "USB" -> valuesImpresoraModificadosUbicacion.estado = "U"
            "RED" -> valuesImpresoraModificadosUbicacion.estado = "I"
            "Fuera Almacen error inventario" -> valuesImpresoraModificadosUbicacion.estado = "F"
            "Retirada definitiva" -> valuesImpresoraModificadosUbicacion.estado = "B"
            "Reparacion" -> valuesImpresoraModificadosUbicacion.estado = "R"
            "Almacen" -> valuesImpresoraModificadosUbicacion.estado = "A"
            "Pendiente entrar almacen" -> valuesImpresoraModificadosUbicacion.estado = "P"
            "Indefinido" -> valuesImpresoraModificadosUbicacion.estado = "N"
        }
    }

    private fun checkConsulta(id: Int) {
        // Buscar el objeto consulta por id
        val itemConsulta = valuesConsultas.find { it.id == id }
        // Verificar si se encontró la consulta

        // Obtener el índice de la consulta encontrada
        val indexConsulta = valuesConsultas.indexOf(itemConsulta)
        // Establecer la selección del Spinner al índice encontrado
        findViewById<Spinner>(R.id.spinnerConsulta).setSelection(indexConsulta)

    }

    // Recuperar las modificaciones hechas en la impresora
    fun getValues() {
        valuesImpresoraModificadosUbicacion.idservicio
        valuesImpresoraModificadosUbicacion.idconsulta
        valuesImpresoraModificadosUbicacion.codSAS = binding.textViewCodigoSAS.text.toString()

        valuesImpresoraModificadosUbicacion.numSerie = binding.textViewNumSerie.text.toString()

        //TRANSFORMAR FECHA MILISEGUNDOS
        valuesImpresoraModificadosUbicacion.fechaInstalacion =
            changeToSecond(binding.textViewFechaInstalacion.text.toString())
                ?.toLong() // Conversión a Long, verificar formato

        valuesImpresoraModificadosUbicacion.mac = binding.textViewMac.text.toString()
        valuesImpresoraModificadosUbicacion.firmware =
            binding.textViewFirmware.text.toString().takeIf { it.isNotBlank() } // Nullable
        valuesImpresoraModificadosUbicacion.valiniciales =
            binding.textViewValIniciales.text.toString()
                .toInt() // Conversión a Int, verificar formato

        valuesImpresoraModificadosUbicacion.nivelToner = binding.textViewNivelToner.text.toString()
            .toInt() // Conversión a Int, verificar formato

        //TRANSFORMAR FECHA MILISEGUNDOS
        // valuesModificados.fechaalta = changeToSecond(binding.editTextFechaAlta.text.toString())
        //TRANSFORMAR FECHA MILISEGUNDOS
        valuesImpresoraModificadosUbicacion.fechaMod =
            changeToSecond(binding.textViewFechaMod.text.toString())

        valuesImpresoraModificadosUbicacion.fechaBaja =
            binding.textViewFechaBaja.text.toString().takeIf { it.isNotBlank() }
                ?.toLong()
        changeStatus(binding.textViewName.toString())
        valuesImpresoraModificadosUbicacion.almacen = ""
        //////////////
        valuesImpresoraModificadosUbicacion.ip = binding.editTextIp.text.toString()

        valuesImpresoraModificadosUbicacion.almacen = binding.textViewAlmacen.text.toString();

        valuesImpresoraModificadosUbicacion.persona = binding.editTextPersona.text.toString()
            .ifEmpty { null } // Asignar valor predeterminado si no existe en `data`


        valuesImpresoraModificadosUbicacion.telefono = binding.editTextTelefono.text.toString()
            .ifEmpty { null } // Asignar valor predeterminado si no existe en `data`

        //   valuesModificados.prealmacen = binding.editTextPrealmacen.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() // Intenta convertir a Int, retorna null si no puede
        valuesImpresoraModificadosUbicacion.usuarioRecepcion =
            binding.editTextUsuarioRecepcion.text.toString().takeIf { it.isNotBlank() } // Nullable
        valuesImpresoraModificadosUbicacion.vlan =
            binding.textViewVlan.text.toString() // Se obtiene directamente
        //  valuesModificados.usuario =binding.editTextUsuario.text.toString() // Se obtiene directamente
        valuesImpresoraModificadosUbicacion.matricula =
            binding.editTextMatricula.text.toString() // Se obtiene directamente
        valuesImpresoraModificadosUbicacion.valactuales =
            binding.textViewValActuales.text.toString()
                .toInt() // Conversión a Int, verificar formato
                //   valuesModificados.idprovincia = binding.editTextIdProvincia.text.toString()
                .toInt() // Conversión a Int, verificar formato
        valuesImpresoraModificadosUbicacion.ubicacion =
            binding.editTextUbicacion.text.toString() // Se obtiene directamente

        valuesImpresoraModificadosUbicacion.codMansis =
            binding.editTextcodMansis.text.toString().takeIf { it.isNotBlank() } // Nullable


        valuesImpresoraModificadosUbicacion.fechaEntradaAlmacen =
            null // Asignar valor predeterminado si no existe en `data`
        valuesImpresoraModificadosUbicacion.fechaRecepcion =
            null // Asignar valor predeterminado si no existe en `data`
        valuesImpresoraModificadosUbicacion.fechaSalidaAlmacen =
            null // Asignar valor predeterminado si no existe en `data`

    }

    // Modificar estado de impresora USB-RED
    fun checkStateUSB() {
        val btnUSB = binding.switchExample
        btnUSB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                valuesImpresoraModificadosUbicacion.estado = "U"
                binding.editTextIp.apply {
                    setText("")
                    inputType = android.text.InputType.TYPE_NULL
                    isClickable = false
                    isFocusableInTouchMode = false
                    isEnabled = false
                }
                // Fondo deshabilitado
                binding.LayoutIP.setBackgroundResource(R.drawable.linear_background_disabled)
            } else {
                valuesImpresoraModificadosUbicacion.estado = "I"
                binding.editTextIp.apply {
                    setText("")
                    inputType = android.text.InputType.TYPE_CLASS_TEXT
                    isClickable = true
                    isFocusableInTouchMode = true
                    isEnabled = true

                }
                // Fondo habilitado
                binding.LayoutIP.setBackgroundResource(R.drawable.linear_background_enabled)
            }
        }
    }


    // Enviar datos modificados de ubicacion
    fun sendDataModificated() {

        getValues()
        if (valuesImpresoraModificadosUbicacion.estado == "I" && valuesImpresoraModificadosUbicacion.ip == "") {
            Toast.makeText(this, "Campos obligatorios incompletos 'IP'", Toast.LENGTH_SHORT).show()

        } else {
            if (valuesImpresoraModificadosUbicacion.codMansis == null || valuesImpresoraModificadosUbicacion.idservicio == null || valuesImpresoraModificadosUbicacion.idcentro == null || valuesImpresoraModificadosUbicacion.ubicacion == null) {
                Toast.makeText(this, "Campos obligatorios incompletos", Toast.LENGTH_SHORT).show()
            } else {
                var connection = connections()

                connection.sendPrinterUpdate(valuesImpresoraModificadosUbicacion.id,
                    valuesImpresoraModificadosUbicacion,
                    onResponse = { responseString ->
                        //Respuesta recibida
                        Toast.makeText(
                            this,
                            "Impresora actualizada correctaemnte",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)


                    }) { response ->
                }
            }
        }


    }

    // Obtener modelo
    fun getModelo() {
        var connection = connections()
        connection.getModeloImpresoras(objetoDataRecibido.idmodelo, onResponse = { response ->
            binding.textViewIdModelo.text = response.data?.nombre

        })
    }

    // Definir btn incidencia
    fun btnIncidencia() {
        binding.btnCancel.setOnClickListener {
            openModalAveria()
        }
    }

    // Open modal impresora Averia
    fun openModalAveria() {
        val fragmentManager = supportFragmentManager
        val dialog = VentanaModal.newInstance("averiaImpresora")

        // Convertimos el objeto a JSON usando Gson
        val gson = Gson()
        val objetoDataModificatedJson = gson.toJson(valuesImpresoraModificadosUbicacion)
        // Recuperamos el Bundle existente y añadimos nuevos datos
        val bundle = dialog.arguments ?: Bundle()
        bundle.putString("dataModificated", objetoDataModificatedJson)

        // Añadimos el Bundle al dialog
        dialog.arguments = bundle

        // Mostramos el dialog
        dialog.show(fragmentManager, "CustomDialog")
    }

    // Crear acordeon del view, para modificar los datos de la impresora
    fun setAccordionUbicacion() {
        val llAccordion: LinearLayout = findViewById(R.id.contenidoAccordionUbicacion)
        val tvHeader: TextView = findViewById(R.id.HeaderAccordionUbicacion)
        val layoutAccordion: LinearLayout = findViewById(R.id.layoutAccordionUbicacion)

        tvHeader.setOnClickListener {
            if (llAccordion.visibility == View.GONE) {
                // Mostrar contenido y cambiar color de fondo
                val drawable = layoutAccordion.background as GradientDrawable
                drawable.setColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccordion
                    )
                ) // Cambia el color del solid
                llAccordion.visibility = View.VISIBLE
                tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            } else {
                // Ocultar contenido y revertir color de fondo si es necesario
                val drawable = layoutAccordion.background as GradientDrawable
                drawable.setColor(
                    ContextCompat.getColor(
                        this,
                        android.R.color.transparent
                    )
                ) // Revertir a transparente
                llAccordion.visibility = View.GONE
                tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
            }
        }
    }

    fun setVisibleAccordionIncidencias(binding: ActivityMainUbicationPrinterBinding) {
        val vistaLinearLayoutIncidencias =binding.contenidoAccordionAverias
        val encabezadoAccordion =binding.encabezadoAccordionIncidencias

        encabezadoAccordion.setOnClickListener {
            // Cambiar visibilidad entre VISIBLE y GONE
            if (vistaLinearLayoutIncidencias.visibility == View.VISIBLE) {
                vistaLinearLayoutIncidencias.visibility = View.GONE
            } else {
                vistaLinearLayoutIncidencias.visibility = View.VISIBLE
                createIncidenciasView()
            }
        }
    }

    // Crear acordeon del view, para listar el historico de  de la impresora
    private fun createIncidenciasView() {
        val connection = connections()
        connection.getListHistoricoIncidencias(
            objetoDataRecibido.numSerie,
            onResponse = { response ->
                println("ESTO ES EL HISTORICO INCIDENCIAS: $response")

                // Configurar RecyclerView
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAverias)
                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.adapter = IncidenciasAccordionAdapter(response)
            },
            onFailure = { throwable ->
                println("ERROR EN LISTAR INCIDENCIAS: $throwable")
            }
        )
    }


    // crear spinner reparacion
    fun setupSpinnerReparacion() {
        val connection = connections()
        if (objetoDataRecibido.idarea != null) {
            connection.getImpresorasReparacion(
                objetoDataRecibido.idarea!!,
                onResponse = { response ->
                    // SPINNER REPARACION VIEW
                    val SpinnerReparacion = findViewById<Spinner>(R.id.SpinnerReparacion)
                    val impresoraVacia = ImpresoraData(
                        id = 0,  // Valor predeterminado para un ID
                        numSerie = "",  // Cadena vacía para el número de serie
                        nombre = "Ningun impresora seleccionada",  // Cadena vacía para el nombre
                        codSAS = "",  // Cadena vacía para el código SAS
                        codMansis = null,  // Nullable, por lo que se puede dejar como null
                        matricula = "",  // Cadena vacía para la matrícula
                        mac = "",  // Cadena vacía para la MAC
                        ip = null,  // Nullable, por lo que se puede dejar como null
                        firmware = null,  // Nullable, por lo que se puede dejar como null
                        estado = "",  // Cadena vacía para el estado
                        almacen = "",  // Cadena vacía para el almacen
                        planta = null,  // Nullable, por lo que se puede dejar como null
                        ubicacion = "",  // Cadena vacía para la ubicación
                        persona = null,  // Nullable, por lo que se puede dejar como null
                        telefono = null,  // Nullable, por lo que se puede dejar como null
                        prealmacen = null,  // Nullable, por lo que se puede dejar como null
                        usuarioRecepcion = null,  // Nullable, por lo que se puede dejar como null
                        vlan = null,  // Nullable, por lo que se puede dejar como null
                        usuario = null,  // Nullable, por lo que se puede dejar como null
                        estadoSNMP = null,  // Nullable, por lo que se puede dejar como null
                        valactuales = 0,  // Valor predeterminado de 0
                        valiniciales = 0,  // Valor predeterminado de 0
                        nivelToner = 0,  // Valor predeterminado de 0
                        idprovincia = 0,  // Valor predeterminado de 0
                        idarea = null,  // Nullable, por lo que se puede dejar como null
                        idcentro = null,  // Nullable, por lo que se puede dejar como null
                        idconsulta = null,  // Nullable, por lo que se puede dejar como null
                        idservicio = null,  // Nullable, por lo que se puede dejar como null
                        idmodelo = 0,  // Valor predeterminado para el ID del modelo
                        fechaInstalacion = null,  // Nullable, por lo que se puede dejar como null
                        fechaalta = null,  // Nullable, por lo que se puede dejar como null
                        fechaEntradaAlmacen = null,  // Nullable, por lo que se puede dejar como null
                        fechaRecepcion = null,  // Nullable, por lo que se puede dejar como null
                        fechaSalidaAlmacen = null,  // Nullable, por lo que se puede dejar como null
                        fechaMod = null,  // Nullable, por lo que se puede dejar como null
                        fechaBaja = null,  // Nullable, por lo que se puede dejar como null
                        imagen = null  // Nullable, por lo que se puede dejar como null
                    )

                    val valueReparacion = listOf(impresoraVacia) + response

                    SpinnerAdapter(
                        context = this,
                        spinner = SpinnerReparacion,
                        values = valueReparacion,
                        nameExtractor = { "${it.codSAS} - ${it.nombre}" }, // Muestra codSas y nombre
                        idExtractor = { it.id },
                        setterModificados = { id ->
                        },
                        fullObjectSetter = { impresora ->

                        },
                        fragmentManager = supportFragmentManager,
                        impresoraSustituidora = objetoDataRecibido
                    )
                })
        }


    }

    // Mascara en la Ip
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

    // Activity Result para el Scanner CodMansis
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == YOUR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val codigo = it.getStringExtra("codigo")
                val format = it.getStringExtra("format")
                if (format == "QR_CODE" && !codigo.isNullOrEmpty()) {
                    binding.editTextcodMansis.setText(codigo)
                    Toast.makeText(this, "Codigo Mansis guardado ", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Codigo Incorrecto ", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    // Ocultar los botones de navegacion
    private fun hideSystemUI() {
        // Obtener la vista de la ventana y configurar las opciones del sistema
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        decorView.systemUiVisibility = uiOptions
    }
}
