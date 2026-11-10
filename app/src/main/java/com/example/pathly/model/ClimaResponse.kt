
package com.example.pathly.model

import com.google.gson.annotations.SerializedName

// RESPUESTA GENERAL DEL API
data class ClimaResponse(

    @SerializedName("current")
    val actual: ClimaActual?,

    @SerializedName("hourly")
    val porHoras: ClimaHoras?,

    @SerializedName("daily")
    val diario: ClimaDiario?

)

// MODELO PARA EL CLIMA ACTUAL
data class ClimaActual(

    @SerializedName("temperature_2m")
    val temperatura: Double?,

    @SerializedName("relative_humidity_2m")
    val humedad: Int?,

    @SerializedName("apparent_temperature")
    val sensacionTermica: Double?,

    @SerializedName("weather_code")
    val codigoClima: Int?

)

// MODELO PARA EL PRONÓSTICO POR HORAS
data class ClimaHoras(

    @SerializedName("time")
    val horas: List<String>?,

    @SerializedName("temperature_2m")
    val temperaturas: List<Double?>?,

    @SerializedName("precipitation_probability")
    val probabilidadLluvia: List<Int?>?

)

// MODELO PARA EL PRONÓSTICO SEMANAL
data class ClimaDiario(

    @SerializedName("time")
    val fechas: List<String>?,

    @SerializedName("temperature_2m_max")
    val temperaturasMaximas: List<Double?>?,

    @SerializedName("temperature_2m_min")
    val temperaturasMinimas: List<Double?>?,

    @SerializedName("weather_code")
    val codigosClima: List<Int?>?

)
