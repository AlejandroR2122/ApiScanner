package com.example.api_scanner.connection


import android.content.Context
import android.util.Log
import android.widget.Toast

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


class connections {
    companion object {
        lateinit var impresoraData: ImpresoraData   // Datos impresora
    }


    val ApiConsultas = RetrofitInstance.apiConsultas

    //////////////////////////////SEND consultas///////////////////////////////////
    fun consultasSend(onResponse: (List<consulta>?) -> Unit) {

        ApiConsultas.sendConsulta().enqueue(object : Callback<List<consulta>> {
            override fun onResponse(
                call: Call<List<consulta>>,
                response: Response<List<consulta>>
            ) {
                if (response.isSuccessful) {
                    // Si la respuesta es exitosa, pasar el cuerpo a la función de callback
                    onResponse(response.body()) // Aquí procesas la lista de consultas
                } else {
                    // Manejar el caso en que la respuesta no es exitosa

                    Log.e("Alejandro", "Cuerpo de error: ${response.errorBody()?.string()}")
                    onResponse(null) // Puedes pasar null si hay un error
                }
            }

            override fun onFailure(call: Call<List<consulta>>, t: Throwable) {
                // Manejar el fallo de la llamada
                Log.e("Alejandro", "Fallo: ${t.message}", t)
                onResponse(null) // También pasar null aquí
            }
        })
    }

    //////////////////////////////////////// SEND CENTRO
    fun sendCentro(parametro: String, onResponse: (DataCentro?) -> Unit) {
        ApiConsultas.sendCentro(parametro).enqueue(object : Callback<DataCentro> {
            override fun onResponse(
                call: Call<DataCentro>,
                response: Response<DataCentro>
            ) {

                if (response.code() == 200) {
                    println("Send centros " + response.body())
                    onResponse(response.body())
                } else {
                    Log.e("ERROR SEND AREA", "HA ocurrido un error la respuesta del servidor")
                    return
                }
            }

            override fun onFailure(call: Call<DataCentro>, t: Throwable) {

            }
        })
    }

    /////////////////////////// GET IMPRESORAS REPARACION ///////////////////
    fun getImpresorasReparacion(parametro: Int, onResponse: (MutableList<ImpresoraData>) -> Unit) {
        ApiConsultas.getImpresorasReparacion(parametro)
            .enqueue(object : Callback<MutableList<ImpresoraData>> {
                override fun onResponse(
                    call: Call<MutableList<ImpresoraData>>,
                    response: Response<MutableList<ImpresoraData>>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        onResponse(response.body()!!)

                    }

                }

                override fun onFailure(call: Call<MutableList<ImpresoraData>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }

    //////////////////////////////SEND AREA///////////////////////////////////
    fun sendArea(parametro: String, onResponse: (List<DataArea>?) -> Unit) {
        ApiConsultas.sendArea(parametro).enqueue(object : Callback<List<DataArea>> {
            override fun onResponse(
                call: Call<List<DataArea>>,
                response: Response<List<DataArea>>
            ) {
                if (response.code() == 200) {
                    println("RESPUESTA QUE BUSCO " + response.body())
                    onResponse(response.body())
                } else {
                    Log.e("ERROR SEND AREA", "HA ocurrido un error la respuesta del servidor")
                    return
                }
            }

            override fun onFailure(call: Call<List<DataArea>>, t: Throwable) {
                // Manejar el error de conexión
                t.printStackTrace() // Opcional: imprime el error en la consola
                onResponse(null) // Devuelve null en caso de error
            }
        })


    }

    /////////////SERVICIOS///////////////////////
    fun sendListServicios(onResponse: (List<DataServicio>) -> Unit) {
        ApiConsultas.sendListServicios().enqueue(object : Callback<List<DataServicio>> {
            override fun onResponse(
                call: Call<List<DataServicio>>,
                response: Response<List<DataServicio>>
            ) {
                if (response.code() == 200) {
                    println("SendListServicios " + response)
                    val body = response.body()
                    if (body != null) {
                        onResponse(body)
                    } else {
                        println("Error: body is not a List<DataServicio>")
                    }
                } else {
                    println("Error response.code not 200")
                }


            }

            override fun onFailure(call: Call<List<DataServicio>>, t: Throwable) {

            }

        })
    }


    //////////////////////Obtener datos IMPRESORA EN "DATOS IMPRESORA"/////////////////////////
    val apiService = RetrofitInstance.apiSend
    fun sendMessageToServer(
        parametro: String, message: String, context: Context, onResponse: (ApiRespuesta?)
        -> Unit
    ) {
        // Llamar al método que ahora usa GET
        apiService.getDataDetailsPrinter(parametro, message)
            .enqueue(object : Callback<ImpresoraData> {
                override fun onResponse(
                    call: Call<ImpresoraData>,
                    response: Response<ImpresoraData>
                ) {
                    if (response.code() == 200) {
                        println("reponse " + response.body())
                        response.body()?.let { responseData ->
                            // Aquí puedes crear la variable de tipo ResponseData
                            impresoraData = ImpresoraData(
                                id = responseData.id,
                                numSerie = responseData.numSerie,
                                nombre = responseData.nombre,
                                codSAS = responseData.codSAS,
                                codMansis = responseData.codMansis,
                                matricula = responseData.matricula,
                                mac = responseData.mac,
                                ip = responseData.ip,
                                firmware = responseData.firmware,
                                estado = responseData.estado,
                                almacen = responseData.almacen,
                                planta = responseData.planta,
                                ubicacion = responseData.ubicacion,
                                persona = responseData.persona,
                                telefono = responseData.telefono,
                                prealmacen = responseData.prealmacen,
                                usuarioRecepcion = responseData.usuarioRecepcion,
                                vlan = responseData.vlan,
                                usuario = responseData.usuario,
                                estadoSNMP = responseData.estadoSNMP,
                                valactuales = responseData.valactuales,
                                valiniciales = responseData.valiniciales,
                                nivelToner = responseData.nivelToner,
                                idprovincia = responseData.idprovincia,
                                idarea = responseData.idarea,
                                idcentro = responseData.idcentro,
                                idconsulta = responseData.idconsulta,
                                idservicio = responseData.idservicio,
                                idmodelo = responseData.idmodelo,
                                fechaInstalacion = responseData.fechaInstalacion,
                                fechaalta = responseData.fechaalta,
                                fechaEntradaAlmacen = responseData.fechaEntradaAlmacen,
                                fechaRecepcion = responseData.fechaRecepcion,
                                fechaSalidaAlmacen = responseData.fechaSalidaAlmacen,
                                fechaMod = responseData.fechaMod,
                                fechaBaja = responseData.fechaBaja,
                                imagen = responseData.imagen
                            )
                            onResponse(
                                ApiRespuesta(
                                    success = true,
                                    message = "Éxito",
                                    data = impresoraData
                                )
                            )
                        }
                    } else if (response.code() == 400) {
                        Toast.makeText(
                            context,
                            "Impresora no encontrada en Almacen",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // Toast.makeText(context, "Error en la conexiones", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ImpresoraData>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Error en la conexiones: ${t.message}",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    onResponse(null) // Llama al callback con nulo en caso de fallo
                }
            })
    }

    //////////////// OBTENER MODELOS IMPRESORAS ///////////////////////
    fun getModeloImpresoras(parametro: Int, onResponse: (ApiModeloRespuesta) -> Unit) {
        apiService.getModelo(parametro).enqueue(object : Callback<ResponseModelo> {
            override fun onResponse(
                call: Call<ResponseModelo>,
                response: Response<ResponseModelo>
            ) {
                if (response.code() == 200 && response.isSuccessful) {
                    val responseBody = response.body() ?: return onResponse(
                        ApiModeloRespuesta(
                            success = false,
                            message = "Respuesta vacía del servidor",
                            data = null
                        )
                    )

                    onResponse(
                        ApiModeloRespuesta(
                            success = true,
                            message = "TODO CORRECTO",
                            data = responseBody
                        )
                    )
                }
            }


            override fun onFailure(call: Call<ResponseModelo>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    //////////////////////OBTENER DATOS EN GESTION ALMACEN/////////////////////////
    fun connectionGestionAlmacen(
        parametro: String, message: String, context: Context, onResponse: (ApiRespuesta?)
        -> Unit
    ) {
        // Llamar al método que ahora usa GET
        apiService.getDataGestionAlmacen(parametro, message)
            .enqueue(object : Callback<ImpresoraData> {
                override fun onResponse(
                    call: Call<ImpresoraData>,
                    response: Response<ImpresoraData>
                ) {
                    if (response.code() == 200) {

                        response.body()?.let { responseData ->
                            // Aquí puedes crear la variable de tipo ResponseData
                            impresoraData = ImpresoraData(
                                id = responseData.id,
                                numSerie = responseData.numSerie,
                                nombre = responseData.nombre,
                                codSAS = responseData.codSAS,
                                codMansis = responseData.codMansis,
                                matricula = responseData.matricula,
                                mac = responseData.mac,
                                ip = responseData.ip,
                                firmware = responseData.firmware,
                                estado = responseData.estado,
                                almacen = responseData.almacen,
                                planta = responseData.planta,
                                ubicacion = responseData.ubicacion,
                                persona = responseData.persona,
                                telefono = responseData.telefono,
                                prealmacen = responseData.prealmacen,
                                usuarioRecepcion = responseData.usuarioRecepcion,
                                vlan = responseData.vlan,
                                usuario = responseData.usuario,
                                estadoSNMP = responseData.estadoSNMP,
                                valactuales = responseData.valactuales,
                                valiniciales = responseData.valiniciales,
                                nivelToner = responseData.nivelToner,
                                idprovincia = responseData.idprovincia,
                                idarea = responseData.idarea,
                                idcentro = responseData.idcentro,
                                idconsulta = responseData.idconsulta,
                                idservicio = responseData.idservicio,
                                idmodelo = responseData.idmodelo,
                                fechaInstalacion = responseData.fechaInstalacion,
                                fechaalta = responseData.fechaalta,
                                fechaEntradaAlmacen = responseData.fechaEntradaAlmacen,
                                fechaRecepcion = responseData.fechaRecepcion,
                                fechaSalidaAlmacen = responseData.fechaSalidaAlmacen,
                                fechaMod = responseData.fechaMod,
                                fechaBaja = responseData.fechaBaja,
                                imagen = responseData.imagen
                            )
                            onResponse(
                                ApiRespuesta(
                                    success = true,
                                    message = "Éxito",
                                    data = impresoraData
                                )
                            )
                        }
                    } else if (response.code() == 400) {
                        Toast.makeText(
                            context,
                            "Impresora no encontrada en prealmacen:/  Error: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Impresora no encontrada en prealmacen :/   Error: ${response.code()}",

                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(call: Call<ImpresoraData>, t: Throwable) {
                    println("Comprueba la conexion a internet y asegurese de que esta conectado a la red correcta ")
                    Toast.makeText(context, "Error en la conexión: ${t.message}", Toast.LENGTH_LONG)
                        .show()
                    onResponse(null) // Llama al callback con nulo en caso de fallo
                }
            })
    }

    //////////////////// OBTENER LISTA DE AVERIAS  ///////////////////////////////
    fun getTypeAverias(onResponse: (List<DataAverias>?) -> Unit) {
        ApiConsultas.getDataAverias().enqueue(object : Callback<List<DataAverias>> {
            override fun onResponse(
                call: Call<List<DataAverias>>,
                response: Response<List<DataAverias>>
            ) {
                if (response.isSuccessful) {
                    println("DATA " + response.body())
                    // Si la respuesta es exitosa, pasamos los datos a onResponse
                    onResponse(response.body()) // Pasa la lista de DataAverias
                } else {
                    // Si la respuesta no es exitosa, pasamos null a onResponse
                    onResponse(null)
                }
            }

            override fun onFailure(call: Call<List<DataAverias>>, t: Throwable) {
                // Si ocurre un error en la conexión, pasamos null a onResponse
                onResponse(null)
            }
        })
    }

    //////////////////// Crear incidencia Impresora //////////////////////////
    fun sendAveriaImpresora(
        parametro: Int,
        body: DataIncidencia,
        onResponse: (ApiResponse) -> Unit
    ) {
        RetrofitInstance.apiSend.sendAveriaImpresora(parametro, body)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        // Llamar al callback con la respuesta exitosa
                        response.body()?.let {
                            onResponse(it)  // Pasa la respuesta a la función de callback
                        }
                    } else {
                        // Manejo de error
                        println("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Manejo de fallos en la conexión
                    println("Falló la conexión: ${t.message}")
                }
            })
    }

    // Crear incidencia PreAlmacen
    fun sendIncidenciaPreAlmacen(
        parametro: Int,
        body: DataIncidencia,
        onResponse: (ApiResponse) -> Unit
    ) {
        RetrofitInstance.apiSend.sendAveriaPrealmacen(parametro, body)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        // Llamar al callback con la respuesta exitosa
                        response.body()?.let {
                            onResponse(it)  // Pasa la respuesta a la función de callback
                        }
                    } else {
                        // Manejo de error
                        println("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    // Manejo de fallos en la conexión
                    println("Falló la conexión: ${t.message}")
                }
            })
    }

    fun sendModificated(
        parametro: Int,
        body: ImpresoraData,
        onResponse: (ApiResponse) -> Unit // Cambiado a ApiResponse
    ) {
        RetrofitInstance.apiSend.sendDataModificated(parametro, body)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == 200) {
                            response.body()?.let { apiResponse ->
                                println("Respuesta exitosa: ${apiResponse.message}")

                                onResponse(apiResponse) // Llama al callback con la respuesta
                            } ?: run {

                            }
                        }
                    }

                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                }

            })
    }

    fun connectionLogin(
        username: String,
        password: String,
        onResponse: (ApiResponseLogin) -> Unit,  // Callback para manejar la respuesta
        onFailure: (Throwable) -> Unit     // Callback para manejar errores
    ) {
        val call = apiService.connectionLogin(username, password)
        println("CALL " + call.request())
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()
                    onResponse(
                        ApiResponseLogin(
                            success = true,
                            accessToken = token?.accessToken,
                            refreshToken = token?.refreshToken
                        )
                    )  // Callback éxito
                } else {
                    println("Error: ${response.code()} - ${response.message()}")
                    onResponse(
                        ApiResponseLogin(
                            success = false,
                            error = response.message()
                        )
                    ) // Error API
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                println("Network Error: ${t.message}")
                onFailure(t) // Callback fallo
            }
        })
    }


    //////////////////// Actualizar impresora //////////////////////////

    fun sendPrinterUpdate(
        parametro: Int,
        body: ImpresoraData,
        onResponse: (ApiResponse) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        RetrofitInstance.apiSend.sendDataModificated(parametro, body)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    println(println("BODY DEL MENSAJE " + body.toString()))
                    if (response.isSuccessful) {
                        response.body()?.let { apiResponse ->
                            println("Respuesta exitosa: ${apiResponse.message}")
                            onResponse(apiResponse) // Llama al callback con la respuesta
                        } ?: run {
                            onFailure(Throwable("Respuesta vacía")) // Manejar respuesta vacía
                        }
                    } else {
                        println("Error en la respuesta")
                        onFailure(Throwable("Error en la conexión: ${response.code()}")) // Manejar error en la respuesta
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    println("Error en la llamada: ${t.message}")
                    when (t) {
                        is IOException -> println("Problema de red o conexión")
                        is HttpException -> println("Error HTTP: ${t.code()} - ${t.message()}")
                        else -> println("Error desconocido")
                    }
                    onFailure(t) // Llama al callback de onFailure con el Throwable original
                }
            })
    }

    fun getListHistoricoIncidencias(
        parametro: String,
        onResponse: (MutableList<DataIncidencia>) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        // Hacer la llamada Retrofit usando el método apiSend.getHistoricoAveria
        RetrofitInstance.apiSend.getHistoricoAveria(parametro)
            .enqueue(object : Callback<MutableList<DataIncidencia>> {
                override fun onResponse(
                    call: Call<MutableList<DataIncidencia>>,
                    response: Response<MutableList<DataIncidencia>>
                ) {
                    // Verificamos si la respuesta fue exitosa
                    if (response.isSuccessful) {
                        println("RESPONSE " +response)
                        println("RESPONSE " +response.body())
                        println("RESPONSE " +call)

                        val data = response.body() ?: mutableListOf() // Si la respuesta está vacía, devolvemos una lista vacía
                        onResponse(data)
                    } else {
                        // Si la respuesta no es exitosa, llamar al callback onFailure con el código de error
                        onFailure(Throwable("Error: ${response.code()} - ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<MutableList<DataIncidencia>>, t: Throwable) {
                    // Si ocurre un error en la llamada (problema de red, por ejemplo)
                    onFailure(t)
                }
            })
    }


    fun sendLogin() {

    }

}
