package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.timeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.paradasLinea.ParadasLinea
import com.example.proyectoemtaja.models.peticiones.*
import com.example.proyectoemtaja.models.usuario.Favorito
import com.example.proyectoemtaja.models.usuario.Usuario
import com.example.proyectoemtaja.utilities.Cabeceras
import com.example.proyectoemtaja.utilities.UrlServidor
import retrofit2.Response
import retrofit2.http.*
import java.util.ArrayList

/**
 * Peticiones al servidor
 */
interface APIService {

    ///////////////////////////////////////////
    ///////////////////////////////////////////
    /////////////////// EMT ///////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////

    /**
     * Peticion de informacion de parada
     * @param url url de la peticion
     * @param token token de usuario
     */
    @GET
    suspend fun getTimeArrivalBus(
        @Url url: String,
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<TimeArrivalBus>

    /**
     * Peticion de informacion de lista de paradas
     * @param token token de usuario
     */
    @GET( UrlServidor.URL_LISTAR_PARADAS)
    suspend fun getListaParadas(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ListaParadas>

    /**
     * Peticion de informacion de paradas de una linea
     * @param url url de la peticion
     * @param token token de usuario
     */
    @GET
    suspend fun getParadasLinea(
        @Url url: String,
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ParadasLinea>

    ///////////////////////////////////////////
    ///////////////////////////////////////////
    ///////////////// USUARIO /////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////

    /**
     * Peticion de actualizacion de usuario
     * @param token token de usuario
     * @param usuario datos para la actualizacion
     */
    @PUT(UrlServidor.URL_ACTUALIZAR_USUARIO)
    suspend fun actualizarUsuario(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body usuario:ActualizarUsuarioRequest
    ): Response<Usuario>

    /**
     * Peticion de informacion usuario
     * @param token token de usuario
     */
    @GET(UrlServidor.URL_BUSCAR_USUARIO)
    suspend fun buscarUsuario(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<Usuario>

    /**
     * Peticion de insertar usuario
     * @param usuario datos de usuario
     */
    @POST(UrlServidor.URL_INSERTAR_USUARIO)
    suspend fun insertUsuario(
        @Body usuario: Usuario
    ): Response<Usuario>

    /**
     * Peticion de login
     * @param user correo
     * @param password clave
     */
    @POST(UrlServidor.URL_LOGIN)
    @FormUrlEncoded
    suspend fun login(
        @Field(Cabeceras.CAMPO_USUARIO) user: String,
        @Field(Cabeceras.CAMPO_PASSWORD) password: String
    ): Response<LoginResponse>

    /**
     * Peticion de prueba de token
     * @param token token de usuario
     */
    @GET(UrlServidor.URL_PROBAR_TOKEN)
    suspend fun probarToken(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<Void>

    /**
     * Solicitud de cambio de contraseña
     * @param correo correo del usuario
     */
    @POST(UrlServidor.URL_PEDIR_CODIGO)
    suspend fun pedirCodigo(
        @Header("correo") correo: String
    ): Response<Void>

    /**
     * Peticion para cambiar clave
     * @param request datos necesarios para el cambio de clave
     */
    @PUT(UrlServidor.URL_CAMBIAR_PASSWORD)
    suspend fun cambiarClave(
        @Body request: CambiarClaveRequest
    ): Response<Void>


    ////////////////////////////////////////////
    ////////////////////////////////////////////
    ///////////////// FAVORITO /////////////////
    ////////////////////////////////////////////
    ////////////////////////////////////////////

    /**
     * Peticion de obtencion de favoritos
     * @param token token de usuario
     */
    @GET(UrlServidor.URL_LISTAR_FAVORITOS)
    suspend fun getFavoritos(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ArrayList<Favorito>>

    /**
     * Peticion para guardar datos de favorito
     * @param token token de usuario
     * @param favorito datos de favorito
     */
    @POST(UrlServidor.URL_INSERTAR_FAVORITO)
    suspend fun insertarFavorito(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body favorito: Favorito
    ): Response<Favorito>

    /**
     * Peticion para actualizar datos de favorito
     * @param token token de usuario
     * @param favorito datos de favorito
     */
    @PUT(UrlServidor.URL_ACTUALIZAR_FAVORITO)
    suspend fun actualizarFavorito(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body favorito: Favorito
    ): Response<Favorito>

    /**
     * Peticion para borrar datos de favorito
     * @param token token de usuario
     * @param favorito datos de favorito
     */
    @HTTP(method = "DELETE", path = UrlServidor.URL_BORRAR_FAVORITO, hasBody = true)
    suspend fun borrarFavorito(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body favorito: BorrarFavoritoRequest
    ): Response<Void>
}