package com.appointmed.mobile.data.network

import com.appointmed.mobile.data.model.LoginRequest
import com.appointmed.mobile.data.model.RegisterRequest
import com.appointmed.mobile.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<User>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<User>

    @GET("{id}")
    fun getUser(@Path("id") id: Long): Call<User>

    @PUT("{id}")
    fun updateUser(@Path("id") id: Long, @Body user: User): Call<User>
}
