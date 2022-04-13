package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.TimeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.example.proyectoemtaja.models.peticiones.LoginRequest
import com.example.proyectoemtaja.models.peticiones.LoginResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET
    suspend fun getTimeArrivalBus(@Url url:String): Response<TimeArrivalBus>
    @GET
    suspend fun getListaParadas(@Url url:String): Response<ListaParadas>
   // @POST("/login")

    @POST("login")
    //@FormUrlEncoded
    suspend fun  login(@Body request:LoginRequest ): Response<LoginResponse>
  //@Body login:LoginRequest
   // @FieldMap params:Map<String,String>
    //@Field("user") user:String,@Field("password") password:String
}