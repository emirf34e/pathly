
package com.example.pathly.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// ARCHIVO QUE CONSTRUYE LA URL Y CONECTA EL API

object RetrofitClient {

    // URL PRINCIPAL DE OPEN-METEO
    private const val BASE_URL = "https://api.open-meteo.com/"

    // VARIABLE QUE CONSTRUYE LA PETICIÓN
    val api: OpenMeteoApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoApi::class.java)

    }

}
