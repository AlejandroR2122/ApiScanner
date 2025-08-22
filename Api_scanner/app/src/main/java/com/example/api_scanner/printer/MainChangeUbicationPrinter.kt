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
import com.example.api_scanner.databinding.ActivityMainChangeUbicationPrinterBinding
import com.example.api_scanner.fragments.FragmentDetailsPrinter.Companion.YOUR_REQUEST_CODE
import com.example.api_scanner.scanner.MainScanner
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainChangeUbicationPrinter : AppCompatActivity() {
    private var baseUrl: String = RetrofitInstance.BASE_URL
    private lateinit var binding: ActivityMainChangeUbicationPrinterBinding
    private lateinit var valuesConsultas: List<consulta>
    private lateinit var valuesArea: List<DataArea>
    private lateinit var dataServicio: List<DataServicio>
    private lateinit var valuesModificados: ImpresoraData
    private lateinit var objetoData: ImpresoraData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializa el binding
        binding = ActivityMainChangeUbicationPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extracted()     // Extraer informacion del fragment
        hideSystemUI() // eliminar botones ui

        btnIncidencia()  // Crear una boton de incidencia

        setIpMask(binding.editTextIp)  // Mascara IP
        // Llamar a la función para cargar las consultas
        consultasResponse(objetoData)
        areaResponse(objetoData)
        plantaSpinner(plantas, objetoData)
        getModelo() // Coger modelo de la impresora
        //CENTRO
        CentroResponse(objetoData)
        //Servicios
        ListServiciosResponse(objetoData)
        checkStateUSB() // Btn USB
        setAccordion()
        // Ejecutar CodMansis
        findViewById<Button>(R.id.btnCodMansis).setOnClickListener {
            val intent = Intent(this, MainScanner::class.java)
            startActivityForResult(intent, YOUR_REQUEST_CODE) // Usa startActivityForResult

        }
        findViewById<Button>(R.id.btnEnviar).setOnClickListener {
            sendDataModificated()
        }


    }

    // Recupero los datos exraidos por de la peticion al servidor
    private fun extracted() {
        // Recuperar el dato usando la misma clave
        val data = intent.getStringExtra("data")
        // Verificar que 'data' no sea nulo antes de convertirlo
        if (data != null) {
            // Transformar a Objeto
            val gson = Gson()
            // Guardo el valor en la varaible global de la clase
            objetoData = gson.fromJson(data, ImpresoraData::class.java)
            valuesModificados = objetoData.copy()
        }
    }

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
                    { idSelected -> valuesModificados.idconsulta = idSelected })


                // ESTO NO VA AQUI TENGO QUE CAMBIARLO
                rellenarCampos(objetoData)
            } else {

                // Manejar el caso cuando la respuesta es nula si es necesario
            }
        }
    }

    // SPINNER PLANTA
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
                valuesModificados.planta = selectedItem
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // No se seleccionó nada
            }
        }
    }


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
                    { idSelected -> valuesModificados.idcentro = idSelected })
                println("VALUES MODIFICADOS " + valuesModificados.idcentro)
                //Seleccionar Centro
                val indexCentros = valuesArea.indexOfFirst { it.id == objetoData.idcentro }
                SpinnerCentros.setSelection(indexCentros)

            }
        }
    }

    //Send ListServicio
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
                    { selectedId -> valuesModificados.idservicio = selectedId })

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


    /// SEND CENTRO

    private fun CentroResponse(objetoData: ImpresoraData) {
        val connection = connections()
        connection.sendCentro(objetoData.idcentro.toString()) { response ->

            //  binding.editTextIdCentro.setText(response?.nombre)
        }
    }


// RELLENAR CAMPOS

    private fun rellenarCampos(data: ImpresoraData) {
        binding.textViewCodigoSAS.text = data.codSAS
        binding.textViewName.setText(objetoData.nombre)
        binding.editTextcodMansis.setText(data.codMansis ?: "")
        binding.editTextMatricula.setText(data.matricula)
        binding.textViewMac.text = data.mac
        binding.editTextIp.setText(data.ip)
        binding.textViewFirmware.text = data.firmware ?: ""
        checkStado(data.estado)
        setSrcImage(binding.imagePrinter, objetoData.imagen)
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

    fun changeTime(milisegundosTotales: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
        val date = Date(milisegundosTotales)
        return dateFormat.format(date)
    }

    private fun changeToSecond(tiempo: String?): Long? {
        println("Tiempo changeToSecond: $tiempo")

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


    private fun setSrcImage(imageView: ImageView, imageUrlS: String?) {
        val imageUrl = baseUrl + "gesprint" + imageUrlS
        Glide.with(this) // Contexto actual, 'this' si es actividad o 'requireContext()' si es fragmento
            .load(imageUrl) // URL de la imagen
            .error(R.drawable.impresora_fuera_servicio)
            .into(imageView) // El ImageView donde se mostrará la imagen
    }

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
                    isEnabled=false

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

    private fun changeStatus(status: String) {
        when (status) {
            "USB" -> valuesModificados.estado = "U"
            "RED" -> valuesModificados.estado = "I"
            "Fuera Almacen error inventario" -> valuesModificados.estado = "F"
            "Retirada definitiva" -> valuesModificados.estado = "B"
            "Reparacion" -> valuesModificados.estado = "R"
            "Almacen" -> valuesModificados.estado = "A"
            "Pendiente entrar almacen" -> valuesModificados.estado = "P"
            "Indefinido" -> valuesModificados.estado = "N"
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

    fun getValues() {
        valuesModificados.idservicio
        valuesModificados.idconsulta
        valuesModificados.codSAS = binding.textViewCodigoSAS.text.toString()

        valuesModificados.numSerie = binding.textViewNumSerie.text.toString()

        //TRANSFORMAR FECHA MILISEGUNDOS
        valuesModificados.fechaInstalacion =
            changeToSecond(binding.textViewFechaInstalacion.text.toString())
                ?.toLong() // Conversión a Long, verificar formato

        valuesModificados.mac = binding.textViewMac.text.toString()
        valuesModificados.firmware =
            binding.textViewFirmware.text.toString().takeIf { it.isNotBlank() } // Nullable
        valuesModificados.valiniciales = binding.textViewValIniciales.text.toString()
            .toInt() // Conversión a Int, verificar formato

        valuesModificados.nivelToner = binding.textViewNivelToner.text.toString()
            .toInt() // Conversión a Int, verificar formato

        //TRANSFORMAR FECHA MILISEGUNDOS
        // valuesModificados.fechaalta = changeToSecond(binding.editTextFechaAlta.text.toString())
        //TRANSFORMAR FECHA MILISEGUNDOS
        valuesModificados.fechaMod = changeToSecond(binding.textViewFechaMod.text.toString())

        valuesModificados.fechaBaja =
            binding.textViewFechaBaja.text.toString().takeIf { it.isNotBlank() }
                ?.toLong()
        changeStatus(binding.textViewName.toString())
        valuesModificados.almacen = ""
        //////////////
        valuesModificados.ip = binding.editTextIp.text.toString()

        valuesModificados.almacen = binding.textViewAlmacen.text.toString();

        valuesModificados.persona = binding.editTextPersona.text.toString()
            .ifEmpty { null } // Asignar valor predeterminado si no existe en `data`


        valuesModificados.telefono = binding.editTextTelefono.text.toString()
            .ifEmpty { null } // Asignar valor predeterminado si no existe en `data`

        //   valuesModificados.prealmacen = binding.editTextPrealmacen.text.toString().takeIf { it.isNotBlank() }?.toIntOrNull() // Intenta convertir a Int, retorna null si no puede
        valuesModificados.usuarioRecepcion =
            binding.editTextUsuarioRecepcion.text.toString().takeIf { it.isNotBlank() } // Nullable
        valuesModificados.vlan = binding.textViewVlan.text.toString() // Se obtiene directamente
        //  valuesModificados.usuario =binding.editTextUsuario.text.toString() // Se obtiene directamente
        valuesModificados.matricula =
            binding.editTextMatricula.text.toString() // Se obtiene directamente
        valuesModificados.valactuales = binding.textViewValActuales.text.toString()
            .toInt() // Conversión a Int, verificar formato
            //   valuesModificados.idprovincia = binding.editTextIdProvincia.text.toString()
            .toInt() // Conversión a Int, verificar formato
        valuesModificados.ubicacion =
            binding.editTextUbicacion.text.toString() // Se obtiene directamente

        valuesModificados.codMansis =
            binding.editTextcodMansis.text.toString().takeIf { it.isNotBlank() } // Nullable


        valuesModificados.fechaEntradaAlmacen =
            null // Asignar valor predeterminado si no existe en `data`
        valuesModificados.fechaRecepcion =
            null // Asignar valor predeterminado si no existe en `data`
        valuesModificados.fechaSalidaAlmacen =
            null // Asignar valor predeterminado si no existe en `data`

    }


    fun checkStateUSB() {
        val btnUSB = binding.switchExample
        btnUSB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                valuesModificados.estado = "U"
                binding.editTextIp.apply {
                    setText("")
                    inputType = android.text.InputType.TYPE_NULL
                    isClickable = false
                    isFocusableInTouchMode = false
                    isEnabled=false
                }
                // Fondo deshabilitado
                binding.LayoutIP.setBackgroundResource(R.drawable.linear_background_disabled)
            } else {
                valuesModificados.estado = "I"
                binding.editTextIp.apply {
                    setText("")
                    inputType = android.text.InputType.TYPE_CLASS_TEXT
                    isClickable = true
                    isFocusableInTouchMode = true
                    isEnabled=true

                }
                // Fondo habilitado
                binding.LayoutIP.setBackgroundResource(R.drawable.linear_background_enabled)
            }
        }
    }


    // Enviar datos modificados
    fun sendDataModificated() {

        getValues()
        if (valuesModificados.estado == "I" && valuesModificados.ip == "") {
            Toast.makeText(this, "Campos obligatorios incompletos 'IP'", Toast.LENGTH_SHORT).show()

        } else {
            if (valuesModificados.codMansis == null || valuesModificados.idservicio == null || valuesModificados.idcentro == null || valuesModificados.ubicacion == null) {
                Toast.makeText(this, "Campos obligatorios incompletos", Toast.LENGTH_SHORT).show()
            } else {
                var connection = connections()

                connection.sendPrinterUpdate(valuesModificados.id, valuesModificados,
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
    fun getModelo ()
    {
        var connection  = connections()
        connection.getModeloImpresoras(objetoData.idmodelo, onResponse = { response->
            binding.textViewIdModelo.text = response.data?.nombre

        })
    }
    fun btnIncidencia(){
        binding.btnCancel.setOnClickListener {

            openModal()
        }
    }
    fun openModal(){
        val fragmentManager = supportFragmentManager
        val dialog = VentanaModal.newInstance("averiaImpresora")

        // Convertimos el objeto a JSON usando Gson
        val gson = Gson()
        val objetoDataModificatedJson = gson.toJson(valuesModificados)

        // Recuperamos el Bundle existente y añadimos nuevos datos
        val bundle = dialog.arguments ?: Bundle()
        bundle.putString("dataModificated", objetoDataModificatedJson)

        // Añadimos el Bundle al dialog
        dialog.arguments = bundle

        // Mostramos el dialog
        dialog.show(fragmentManager, "CustomDialog")
    }
    fun setAccordion() {
        val llAccordion: LinearLayout = findViewById(R.id.contenidoAccordionUbicacion)
        val tvHeader: TextView = findViewById(R.id.HeaderAccordionUbicacion)
        val layoutAccordion: LinearLayout = findViewById(R.id.layoutAccordionUbicacion)

        tvHeader.setOnClickListener {
            if (llAccordion.visibility == View.GONE) {
                // Mostrar contenido y cambiar color de fondo
                val drawable = layoutAccordion.background as GradientDrawable
                drawable.setColor(ContextCompat.getColor(this, R.color.colorAccordion)) // Cambia el color del solid
                llAccordion.visibility = View.VISIBLE
                tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0)
            } else {
                // Ocultar contenido y revertir color de fondo si es necesario
                val drawable = layoutAccordion.background as GradientDrawable
                drawable.setColor(ContextCompat.getColor(this, android.R.color.transparent)) // Revertir a transparente
                llAccordion.visibility = View.GONE
                tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
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
