package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.timeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.peticiones.FavoritoResponse
import com.example.proyectoemtaja.models.peticiones.LoginResponse
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
        @Header(Cabeceras.HEADER_TOKEN) token: String,
        @Header(Cabeceras.HEADER_USUARIO) idUsuario: String
    ): Response<TimeArrivalBus>

    @GET( UrlServidor.URL_LISTAR_PARADAS)
    suspend fun getListaParadas(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ListaParadas>

    @POST(UrlServidor.URL_INSERTAR_USUARIO)
    suspend fun insertUsuario(
        @Body usuario: Usuario
    ): Response<Usuario>

    @POST(UrlServidor.URL_LOGIN)
    @FormUrlEncoded
    suspend fun login(
        @Field(Cabeceras.CAMPO_USUARIO) user: String,
        @Field(Cabeceras.CAMPO_PASSWORD) password: String/*@Body request:LoginRequest*/
    ): Response<LoginResponse>

    @GET(UrlServidor.URL_PROBAR_TOKEN)
    suspend fun probarToken(
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<Void>

    @GET(UrlServidor.URL_LISTAR_FAVORITOS)
    suspend fun getFavoritos(
        @Header(Cabeceras.HEADER_USUARIO) idUsuario: String,
        @Header(Cabeceras.HEADER_TOKEN) token: String
    ): Response<ArrayList<FavoritoResponse>>
}