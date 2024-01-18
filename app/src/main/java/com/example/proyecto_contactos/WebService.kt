package com.example.proyecto_contactos

import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

object AppConstantes {
    const val BASE_URL = "http://10.0.2.2:3000"
}

interface WebService {

    @GET("/contactos")
    suspend fun obtenerContactos(): Response<ContactosResponse>

    @GET("/contacto/{idUsuario}")
    suspend fun obtenerUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<Usuario>

    @POST("/contacto/add")
    suspend fun agregarUsuario(
        @Body usuario: Usuario
    ): Response<String>

    @PUT("/contacto/update/{idUsuario}")
    suspend fun actualizarUsuario(
        @Path("idUsuario") idUsuario: Int,
        @Body usuario: Usuario
    ): Response<String>

    @DELETE("/contacto/delete/{idUsuario}")
    suspend fun borrarUsuario(
        @Path("idUsuario") idUsuario: Int
    ): Response<String>
}

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(WebService::class.java)
    }
}
