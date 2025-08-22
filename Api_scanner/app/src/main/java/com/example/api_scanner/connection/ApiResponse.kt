package com.example.api_scanner.connection


data class ApiRespuesta(
    val success: Boolean,
    val message: String,
    val data: ImpresoraData

)
data class ApiModeloRespuesta(
    val success: Boolean,
    val message: String,
    val data: ResponseModelo?
)
val plantas: List<String> =
    listOf("Baja", "Sotano", "Subsotano", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

data class ImpresoraData(
    var id: Int,
    var numSerie: String,
    var nombre: String,
    var codSAS: String,
    var codMansis: String?, // Nullable ya que puede ser null en el JSON
    var matricula: String,
    var mac: String,
    var ip: String?,
    var firmware: String?, // Nullable
    var estado: String,
    var almacen: String,
    var planta: String?,
    var ubicacion: String,
    var persona: String?, // Nullable
    var telefono: String?, // Nullable
    var prealmacen: Int?, // Nullable
    var usuarioRecepcion: String?, // Nullable
    var vlan: String?,
    var usuario: String?,
    var estadoSNMP: String?,
    var valactuales: Int,
    var valiniciales: Int,
    var nivelToner: Int,
    var idprovincia: Int,
    var idarea: Int?,
    var idcentro: Int?,
    var idconsulta: Int?, // Nullable
    var idservicio: Int?,
    var idmodelo: Int,
    var fechaInstalacion: Long?, // En milisegundos
    var fechaalta: Long?, // En milisegundos
    var fechaEntradaAlmacen: Long?, // Nullable
    var fechaRecepcion: Long?, // Nullable
    var fechaSalidaAlmacen: Long?, // Nullable
    var fechaMod: Long?, // En milisegundos
    var fechaBaja: Long?, // Nullable
    var imagen: String?
)

data class ApiResponse(
    val message: String,
    val status: Int
)

data class consulta(
    val id: Int?,
    val nombre: String
)

data class DataArea(
    val id: Int?,
    val nombre: String,
    val idarea: Int?,
    val hospital: String,
    val Vlan: Int?,
    val almacen: Int?
)

data class DataServicio(
    val id: Int?,
    val nombre: String
)

data class DataCentro(
    val id: Int?,
    val nombre: String,
    val idarea: Int?,
    val hospital: String,
    val vlan: Int?,
    val almacen: Int
)

data class DataAverias(
    val id: Int?,
    val nombre: String,
    val criticidad: String?
)
data class ResponseModelo(
    val id:Int,
    val nombre:String,
    val imagen: String?,
    val tipo: String
)

data class DataIncidencia(
    var id: Int,
    var numSerie: String,
    var fechaAveria: Long,   // fecha actual
    var usuarioTecnico: String,
    var idarea: Int?,
    var almacen: String,
    var motivoAveria: String,  //Incidencia
    var provincia: Int,
    var copiasEntradaAveria: Int,  // numer de copias
    val copiasSalidaAveria: Int?,
    var incidencia:Int?    //
)

data class LoginResponse(
    val accessToken: String, // Ajusta el campo según lo que devuelva tu API
    val refreshToken: String // Ajusta el campo según lo que devuelva tu API
)
data class ApiResponseLogin(
    val success: Boolean,
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val error: String? = null
)
data class DataUser(
    var user: String,
    var password: String
)