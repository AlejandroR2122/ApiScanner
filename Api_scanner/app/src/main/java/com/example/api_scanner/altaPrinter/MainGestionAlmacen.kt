package com.example.api_scanner.altaPrinter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.api_scanner.MainActivity
import com.example.api_scanner.R
import com.example.api_scanner.connection.ImpresoraData
import com.example.api_scanner.connection.RetrofitInstance
import com.example.api_scanner.connection.connections
import com.example.api_scanner.databinding.ActivityMainGestionPrealmacenPrinterBinding
import com.google.gson.Gson
import java.util.Calendar

class MainGestionAlmacen : AppCompatActivity() {
    private var baseUrl: String = RetrofitInstance.BASE_URL
    private lateinit var binding: ActivityMainGestionPrealmacenPrinterBinding
    private lateinit var objetoData: ImpresoraData
    private lateinit var objetoDataModificated: ImpresoraData
    private lateinit var user: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Iniciar binding para manejar las vistas
        binding = ActivityMainGestionPrealmacenPrinterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNameUser()


        hideSystemUI() // Eliminar los botones de navegacion de la ui
        extracted() // Extraer los datos del fragment anterior

    }

    private fun extracted() {
        // Recojo los datos del fragment
        val data = intent.getStringExtra("data")
        if (data != null) {
            //Cojo data pasado por un intent y lo transformo a un objeto ResponseData
            val gson = Gson()
            objetoData = gson.fromJson(data, ImpresoraData::class.java)
            initAltaPrinter()
        } else {
            // Manejo de error si no hay datos
            println("No se recibieron datos.")
        }
    }

    // Inicia la ventana AltaPrinter
    fun initAltaPrinter() {
        setterCampos(objetoData)
        // Obtenemos el name de login
        // creamos funcion del btn para aceptar
        btnStatusAlmacen()
        getModelo ()
        btnCancel()
    }

    // Modifica los campos del view
    fun setterCampos(objetoData: ImpresoraData) {

        objetoDataModificated =
            objetoData // Copiamos los datos que llegan en la respuesta final, por si se pierde algun campo
        //  binding.textViewCodigoSAS.text=objetoData.codSAS
        //binding.textEditCodSAS.setText(objetoData.codSAS)
        binding.textViewName.setText(objetoData.nombre)
        binding.textViewNumSerie.text = objetoData.numSerie
        // set srcImg
        setSrcImage(binding.imagePrinter, objetoData.imagen)
        //CAMBIAR EL ESTADO AL QUE CORRESPONDE
        checkStado(objetoData.estado).toString()  // Funcion para cambiar el estado
        binding.TextIdAlmacen.text = objetoData.almacen
        binding.TextIdModelo.text = objetoData.idmodelo.toString()
        // binding.editTextUbicacion.setText(objetoData.ubicacion)

    }

    private fun setSrcImage(imageView: ImageView, imageUrlS: String?) {
        val imageUrl = baseUrl + "gesprint" + imageUrlS
        Glide.with(this) // Contexto actual, 'this' si es actividad o 'requireContext()' si es fragmento
            .load(imageUrl) // URL de la imagen
            .error(R.drawable.impresora_fuera_servicio)
            .into(imageView) // El ImageView donde se mostrará la imagen
    }

    private fun setNameUser() {
        // Recibir user
        val sharedPref =this.getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        user = sharedPref.getString("user_name", "") ?: ""

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
    fun getModelo ()
    {
        var connection : connections = connections()
        connection.getModeloImpresoras(objetoDataModificated.idmodelo, onResponse = { response->
            binding.TextIdModelo.text = response.data?.nombre

        })
    }
    fun btnStatusAlmacen() {
        binding.SendModificated.setOnClickListener {

            objetoDataModificated.estado = "A"  // Cambiar el estado de la impresora
            objetoDataModificated.fechaMod = obtenerFechaHoyEnMilisegundos()
            objetoDataModificated.fechaRecepcion = obtenerFechaHoyEnMilisegundos()
            objetoDataModificated.usuarioRecepcion = user

            if (objetoDataModificated.usuarioRecepcion != "" &&
                objetoDataModificated.fechaRecepcion != null &&
                objetoDataModificated.fechaMod != null) {
                sendStatusChange() // Funcion envio al servidor cogiendo los datos de objetoDataModificated
                // Vuelta al MainActivity
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error en los datos de impresora", Toast.LENGTH_SHORT).show()
            }
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

    fun openModal() {
        val fragmentManager = supportFragmentManager
        val dialog = VentanaModal.newInstance("averiaPrealmacen")

        // Convertimos el objeto a JSON usando Gson
        val gson = Gson()
        val objetoDataModificatedJson = gson.toJson(objetoDataModificated)

        // Creamos un Bundle con el JSON
        val bundle = Bundle()
        bundle.putString("dataModificated", objetoDataModificatedJson)

        // Añadimos el Bundle al dialog
        dialog.arguments = bundle

        //Abrir Mostramos el dialog
        dialog.show(fragmentManager, "CustomDialog")
    }

    fun sendStatusChange() {
        val connection = connections()
        connection.sendPrinterUpdate(objetoDataModificated.id,
            objetoDataModificated,
            onResponse = { responseString ->
                Toast.makeText(this, "Impresora actualizada, puesta en almacén", Toast.LENGTH_LONG)
                    .show()
                // Manejar la respuesta exitosa aquí
                println("Respuesta recibida: $responseString")
                // Aquí puedes actualizar la UI o realizar otra acción
            },
            onFailure = { throwable ->
                // Manejar el error aquí
                Toast.makeText(this, "Error al modficiar la impresora", Toast.LENGTH_LONG).show()

                println("Errores: ${throwable.message}")
                // Aquí puedes mostrar un mensaje de error al usuario o registrar el error
            })

    }


    fun btnCancel() {
        binding.btnCancel.setOnClickListener {
            openModal()
           // sendStatusChange()

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
