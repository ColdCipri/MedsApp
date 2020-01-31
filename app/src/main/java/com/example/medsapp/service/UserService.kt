package com.example.medsapp.service

import com.example.medsapp.service.Model.User;
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("Users")
    fun create(@Body user: User): Observable<User>

    @GET("Users/{email}")
    fun read(@Path("email") email: String): Observable<List<User>>

    @GET("Users/")
    fun readAll(): Observable<List<User>>

    @PUT("Users/{email}")
    fun update(@Path("email") email: String, @Body user: User): Observable<User>

    @DELETE("Users/{email}")
    fun delete(@Path("email") email: String): Observable<Response<Void>>
}