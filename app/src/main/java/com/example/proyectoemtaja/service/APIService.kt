package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.timeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.peticiones.LoginResponse
import com.example.proyectoemtaja.models.usuario.Usuario
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET
    suspend fun getTimeArrivalBus(
        @Url url: String,
        @Header("Authorization") token: String,
        @Header("idUsuario") usuario: String
    ): Response<TimeArrivalBus>

    @GET
    suspend fun getListaParadas(
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<ListaParadas>
    // @POST("/login")

    @POST("usuario/insertar")
    suspend fun insertUsuario(@Body usuario: Usuario): Response<Usuario>

    @POST("usuario/login")
    @FormUrlEncoded
    suspend fun login(
        @Field("correo") user: String,
        @Field("password") password: String/*@Body request:LoginRequest*/
    ): Response<LoginResponse>

    @GET("usuario/probar-token")
    suspend fun probarToken(
        @Header("Authorization") token: String?
    ): Response<Void>

}