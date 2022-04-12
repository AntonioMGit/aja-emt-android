package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.TimeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import retrofit2.Response
import retrofit2.http.*

interface APIService {

    @GET
    suspend fun getTimeArrivalBus(@Url url:String): Response<TimeArrivalBus>
    @GET
    suspend fun getListaParadas(@Url url:String): Response<ListaParadas>
   // @POST("/login")

    @POST("/login")
    @FormUrlEncoded
    suspend fun  login(@Field("user") user:String,@Field("password") password:String ): Response<String>
   // @FieldMap params:Map<String,String>
    //
}