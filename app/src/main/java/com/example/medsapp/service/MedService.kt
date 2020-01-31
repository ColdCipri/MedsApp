package com.example.medsapp.service

import com.example.medsapp.service.Model.Med;
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface MedService {
    @POST("meds")
    fun create(@Body med: Med): Observable<Boolean>

    @GET("meds/{user}")
    fun readMedsByUser(@Path("user") userEmail: String): Observable<List<Med>>

    @GET("meds/")
    fun readAll(): Observable<List<Med>>

    @PUT("meds/{id}")
    fun update(@Path("id") id: Int, @Body med: Med): Observable<Boolean>

    @DELETE("meds/{id}")
    fun delete(@Path("id") id: Int): Observable<Response<Void>>
}