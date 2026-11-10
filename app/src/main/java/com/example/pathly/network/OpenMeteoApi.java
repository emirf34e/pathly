
package com.example.pathly.network;

import com.example.pathly.model.ClimaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenMeteoApi {

    // CONSULTA 1: CLIMA ACTUAL
    @GET("v1/forecast?current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code&timezone=auto")
    Call<ClimaResponse> obtenerClimaActual(
            @Query("latitude") double latitud,
            @Query("longitude") double longitud
    );

    // CONSULTA 2: PRONOSTICO POR HORAS
    @GET("v1/forecast?hourly=temperature_2m,precipitation_probability&forecast_days=1&timezone=auto")
    Call<ClimaResponse> obtenerPronosticoHoras(
            @Query("latitude") double latitud,
            @Query("longitude") double longitud
    );

    // CONSULTA 3: PRONOSTICO SEMANAL
    @GET("v1/forecast?daily=temperature_2m_max,temperature_2m_min,weather_code&forecast_days=7&timezone=auto")
    Call<ClimaResponse> obtenerPronosticoSemanal(
            @Query("latitude") double latitud,
            @Query("longitude") double longitud
    );
}
