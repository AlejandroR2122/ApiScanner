package com.example.api_scanner.connection

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface ApiService {
    //Conexion Api
    @GET("/gesprint/") // El endpoint raíz
    fun connect(): Call<String> // Espera un String como respuesta

    //Impresora con Cod.SAS
    @GET("/gesprint/rest/api/impresora/{parametro}") // Cambia "tu-endpoint" por la ruta correcta
    fun getDataDetailsPrinter(
        @Path("parametro") parametro: String,
        @Query("message") message: String // Agrega el mensaje como parámetro de consulta
    ): Call<ImpresoraData>

    //Impresora con Cod.SAS
    @GET("/gesprint/rest/api/almacen-pre/{parametro}") // Cambia "tu-endpoint" por la ruta correcta
    fun getDataGestionAlmacen(
        @Path("parametro") parametro: String,
        @Query("message") message: String // Agrega el mensaje como parámetro de consulta
    ): Call<ImpresoraData>

    // MODELO
    @GET("/gesprint/rest/api/modelo/{parametro}")
    fun getModelo(
        @Path("parametro") parametro: Int,
    ):Call<ResponseModelo>

    //CONSULTAS
    @GET("/gesprint/rest/api/consultas")
    fun sendConsulta(): Call<List<consulta>>

    //AREAS
    @GET("/gesprint/rest/api/centros/{parametro}")
    fun sendArea(
        @Path("parametro") parametro: String
    ): Call<List<DataArea>>

    @GET("/gesprint/rest/api/servicio/{parametro}")
    fun sendServicio(
        @Path("parametro") parametro: String
    ): Call<DataServicio>


    @GET("/gesprint/rest/api/impresorasEnReparacion/{parametro}")
    fun getImpresorasReparacion(
        @Path("parametro") parametro: Int
    ):Call<MutableList<ImpresoraData>>

    @GET("/gesprint/rest/api/servicios")
    fun sendListServicios(
    ): Call<List<DataServicio>>

    @GET("/gesprint/rest/api/centro/{parametro}")
    fun sendCentro(
        @Path("parametro") parametro: String
    ): Call<DataCentro>


    @PUT("/gesprint/rest/api/actualizarImpresora/{parametro}")
    fun sendDataModificated(
        @Path("parametro") parametro: Int,
        @Body body: ImpresoraData
    ): Call<ApiResponse>

    @GET("/gesprint/rest/api/averias")
    fun getDataAverias(): Call<List<DataAverias>>

    @PUT("/gesprint/rest/api/denegarAlmacen/{parametro}")
    fun sendAveriaPrealmacen(
        @Path("parametro") parametro: Int,
        @Body body: DataIncidencia
    ): Call<ApiResponse>

    @PUT("/gesprint/rest/api/actualizarAlmacen/{parametro}")
    fun sendAlmacenModificated(
        @Path("parametro") parametro: Int,
        @Body body: DataIncidencia
    ): Call<ApiResponse>

    // Crear Error Averia Impresora
    @PUT("/gesprint/rest/api/denegarAlmacen/{parametro}")
    fun sendAveriaImpresora(
        @Path("parametro") parametro: Int,
        @Body body: DataIncidencia
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("/gesprint/services/auth/login")
    fun connectionLogin(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // Crear Error Averia Impresora
    @GET("/gesprint/rest/api/historicoAveria/{parametro}")
    fun getHistoricoAveria(
        @Path("parametro") parametro: String
    ): Call<MutableList<DataIncidencia>>
}
