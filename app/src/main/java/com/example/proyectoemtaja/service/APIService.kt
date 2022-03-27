package com.example.projectemt.service

import com.example.projectemt.models.TimeArrivalBus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface APIService {

    @GET
    suspend fun getTimeArrivalBus(@Url url:String):Response<TimeArrivalBus>
}