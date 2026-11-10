
package com.example.pathly.model

import com.google.gson.annotations.SerializedName

data class Clima(

    @SerializedName("time")
    val fechas: List<String>?,

    @SerializedName("temperature_2m_max")
    val temperaturasMaximas: List<Double?>?,

    @SerializedName("temperature_2m_min")
    val temperaturasMinimas: List<Double?>?,

    @SerializedName("weather_code")
    val codigosClima: List<Int?>?

)
