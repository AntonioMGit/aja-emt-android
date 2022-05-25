package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.timeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.paradasLinea.ParadasLinea
import com.example.proyectoemtaja.models.peticiones.*
import com.example.proyectoemtaja.models.usuario.Usuario
import com.example.proyectoemtaja.utilities.Cabeceras
import com.example.proyectoemtaja.utilities.UrlServidor
import retrofit2.Response
import retrofit2.http.*
import java.util.ArrayList

interface APIService {

    @GET
    suspend fun getTimeArrivalBus(
        @Url url: String,
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<TimeArrivalBus>

    @GET( UrlServidor.URL_LISTAR_PARADAS)
    suspend fun getListaParadas(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ListaParadas>

    @GET
    suspend fun getParadasLinea(
        @Url url: String,
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Header(Cabeceras.HEADER_USUARIO) idUsuario: String
    ): Response<ParadasLinea>

    @PUT(UrlServidor.URL_ACTUALIZAR_USUARIO)
    suspend fun actualizarUsuario(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body usuario:ActualizarUsuarioRequest
    ): Response<Usuario>

    @GET(UrlServidor.URL_BUSCAR_USUARIO)
    suspend fun buscarUsuario(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<Usuario>

    @POST(UrlServidor.URL_INSERTAR_USUARIO)
    suspend fun insertUsuario(
        @Body usuario: Usuario
    ): Response<Usuario>

    @POST(UrlServidor.URL_LOGIN)
    @FormUrlEncoded
    suspend fun login(
        @Field(Cabeceras.CAMPO_USUARIO) user: String,
        @Field(Cabeceras.CAMPO_PASSWORD) password: String
    ): Response<LoginResponse>

    @GET(UrlServidor.URL_PROBAR_TOKEN)
    suspend fun probarToken(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<Void>

    @GET(UrlServidor.URL_LISTAR_FAVORITOS)
    suspend fun getFavoritos(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ArrayList<Favorito>>

    //Peticiones de favoritos

    @POST(UrlServidor.URL_INSERTAR_FAVORITO)
    suspend fun insertarFavorito(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body favorito: Favorito
    ): Response<Favorito>

    @PUT(UrlServidor.URL_ACTUALIZAR_FAVORITO)
    suspend fun actualizarFavorito(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body favorito: Favorito
    ): Response<Favorito>

    @HTTP(method = "DELETE", path = UrlServidor.URL_BORRAR_FAVORITO, hasBody = true)
    suspend fun borrarFavorito(
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Body favorito: BorrarFavoritoRequest
    ): Response<Void>

    @POST(UrlServidor.URL_PEDIR_CODIGO)
    suspend fun pedirCodigo(
        @Header("correo") correo: String
    ): Response<Void>

    @PUT(UrlServidor.URL_CAMBIAR_PASSWORD)
    suspend fun cambiarClave(
        @Body request: CambiarClaveRequest
    ): Response<Void>
}