package com.example.proyectoemtaja.service

import com.example.proyectoemtaja.models.TimeArrival.TimeArrivalBus
import com.example.proyectoemtaja.models.listaParadas.ListaParadas
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {

    @GET
    suspend fun getTimeArrivalBus(@Url url:String): Response<TimeArrivalBus>
    @GET
    suspend fun getListaParadas(@Url url:String): Response<ListaParadas>
}