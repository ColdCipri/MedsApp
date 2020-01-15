package com.example.medsapp.service

import com.example.medsapp.service.Model.User;
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("users")
    fun create(@Body user: User): Observable<User>

    @GET("users/{id}")
    fun read(@Path("id") id: Int): Observable<User>

    @GET("users/")
    fun readAll(): Observable<List<User>>

    @PUT("users/{id}")
    fun update(@Path("id") id: Int, @Body user: User): Observable<User>

    @DELETE("users/{id}")
    fun delete(@Path("id") id: Int): Observable<Response<Void>>
}