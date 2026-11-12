
package com.example.pathly

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.pathly.model.ClimaActual
import com.example.pathly.model.ClimaResponse
import com.example.pathly.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrincipalActivity : AppCompatActivity() {

    // COORDENADAS DE CUERNAVACA
    private val latitud = 18.9242
    private val longitud = -99.2216

    // ELEMENTOS DE LA INTERFAZ
    private lateinit var webMapa: WebView
    private lateinit var tvTemperatura: TextView
    private lateinit var tvDetalleClima: TextView
    private lateinit var tvDatosExtra: TextView
    private lateinit var tvTituloClima: TextView
    private lateinit var ivClima: ImageView

    // BOTONES DE CONSULTAS
    private lateinit var btnClimaActual: Button
    private lateinit var btnPronosticoHoras: Button
    private lateinit var btnPronosticoSemanal: Button

    // BOTON PARA ABRIR DETALLES
    private lateinit var btnVerDetalles: Button

    private var consultaActiva = "actual"

    // GUARDAR TEMPORALMENTE LOS DATOS DEL API
    private var ultimoClima: ClimaActual? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { vista, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            vista.setPadding(
                barras.left,
                barras.top,
                barras.right,
                barras.bottom
            )
            insets
        }

        // VINCULAR ELEMENTOS DEL XML
        webMapa = findViewById(R.id.webMapa)
        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvDetalleClima = findViewById(R.id.tvDetalleClima)
        tvDatosExtra = findViewById(R.id.tvDatosExtra)
        tvTituloClima = findViewById(R.id.tvTituloClima)
        ivClima = findViewById(R.id.ivClima)

        // BOTONES
        btnClimaActual = findViewById(R.id.btnClimaActual)
        btnPronosticoHoras =
            findViewById(R.id.btnPronosticoHoras)
        btnPronosticoSemanal =
            findViewById(R.id.btnPronosticoSemanal)

        btnVerDetalles = findViewById(R.id.btnVerDetalles)

        // CARGAR MAPA
        cargarMapa()

        // CONSULTAR CLIMA AL ENTRAR
        obtenerClimaActual()

        // EVENTOS DE CONSULTAS
        btnClimaActual.setOnClickListener {
            obtenerClimaActual()
        }

        btnPronosticoHoras.setOnClickListener {
            obtenerPronosticoHoras()
        }

        btnPronosticoSemanal.setOnClickListener {
            obtenerPronosticoSemanal()
        }

        // ABRIR PANTALLA DE DETALLES
        btnVerDetalles.setOnClickListener {
            abrirDetallesClima()
        }
    }

    // METODO PARA MOSTRAR EL MAPA
    @SuppressLint("SetJavaScriptEnabled")
    private fun cargarMapa() {

        webMapa.settings.javaScriptEnabled = true
        webMapa.settings.domStorageEnabled = true
        webMapa.webViewClient = WebViewClient()

        val urlMapa =
            "https://www.openstreetmap.org/export/embed.html" +
                    "?bbox=-99.30%2C18.87%2C-99.15%2C18.98" +
                    "&layer=mapnik" +
                    "&marker=$latitud%2C$longitud"

        webMapa.loadUrl(urlMapa)
    }

    // CONSULTA 1: CLIMA ACTUAL
    private fun obtenerClimaActual() {

        consultaActiva = "actual"
        tvTituloClima.text = "Clima actual"
        tvDetalleClima.text = "Consultando..."
        tvDatosExtra.text = "Cargando información..."

        RetrofitClient.api.obtenerClimaActual(
            latitud, longitud
        ).enqueue(object : Callback<ClimaResponse> {

            override fun onResponse(
                call: Call<ClimaResponse>,
                response: Response<ClimaResponse>
            ) {
                val actual = response.body()?.actual

                if (!response.isSuccessful || actual == null) {
                    if (consultaActiva == "actual") {
                        mostrarError()
                    }
                    return
                }

                // CONSERVAR DATOS PARA DETAILS ACTIVITY
                ultimoClima = actual

                if (consultaActiva != "actual") return

                tvTemperatura.text =
                    "${actual.temperatura ?: "--"} °C"

                tvDetalleClima.text =
                    descripcionClima(actual.codigoClima)

                tvDatosExtra.text =
                    "Humedad: ${actual.humedad ?: "--"}%\n" +
                            "Sensación térmica: " +
                            "${actual.sensacionTermica ?: "--"} °C"

                cargarIcono(actual.codigoClima)
            }

            override fun onFailure(
                call: Call<ClimaResponse>,
                t: Throwable
            ) {
                if (consultaActiva == "actual") {
                    mostrarError()
                }
            }
        })
    }

    // CONSULTA 2: PRONOSTICO POR HORAS
    private fun obtenerPronosticoHoras() {

        consultaActiva = "horas"
        tvTituloClima.text = "Pronóstico por horas"
        tvTemperatura.text = "-- °C"
        tvDetalleClima.text = "Consultando..."
        tvDatosExtra.text = "Cargando pronóstico..."
        Glide.with(this).clear(ivClima)

        RetrofitClient.api.obtenerPronosticoHoras(
            latitud, longitud
        ).enqueue(object : Callback<ClimaResponse> {

            override fun onResponse(
                call: Call<ClimaResponse>,
                response: Response<ClimaResponse>
            ) {
                if (consultaActiva != "horas") return

                val datos = response.body()?.porHoras

                if (!response.isSuccessful || datos == null) {
                    mostrarError()
                    return
                }

                val horas = datos.horas.orEmpty()
                val temperaturas =
                    datos.temperaturas.orEmpty()
                val lluvia =
                    datos.probabilidadLluvia.orEmpty()

                val cantidad = minOf(
                    horas.size,
                    temperaturas.size,
                    lluvia.size
                )

                if (cantidad == 0) {
                    mostrarError()
                    return
                }

                tvDetalleClima.text =
                    "Temperatura y probabilidad de lluvia"

                val resultado = StringBuilder()

                for (i in 0 until cantidad) {

                    val hora = horas[i].substringAfter("T")
                    val temp = temperaturas[i] ?: "--"
                    val prob = lluvia[i] ?: "--"

                    resultado.append(
                        "$hora | $temp °C | Lluvia: $prob%\n"
                    )
                }

                tvDatosExtra.text = resultado.toString()
            }

            override fun onFailure(
                call: Call<ClimaResponse>,
                t: Throwable
            ) {
                if (consultaActiva == "horas") {
                    mostrarError()
                }
            }
        })
    }

    // CONSULTA 3: PRONOSTICO SEMANAL
    private fun obtenerPronosticoSemanal() {

        consultaActiva = "semanal"
        tvTituloClima.text = "Pronóstico de 7 días"
        tvTemperatura.text = "-- °C"
        tvDetalleClima.text = "Consultando..."
        tvDatosExtra.text = "Cargando pronóstico..."
        Glide.with(this).clear(ivClima)

        RetrofitClient.api.obtenerPronosticoSemanal(
            latitud, longitud
        ).enqueue(object : Callback<ClimaResponse> {

            override fun onResponse(
                call: Call<ClimaResponse>,
                response: Response<ClimaResponse>
            ) {
                if (consultaActiva != "semanal") return

                val datos = response.body()?.diario

                if (!response.isSuccessful || datos == null) {
                    mostrarError()
                    return
                }

                val fechas = datos.fechas.orEmpty()
                val maximas =
                    datos.temperaturasMaximas.orEmpty()
                val minimas =
                    datos.temperaturasMinimas.orEmpty()
                val codigos = datos.codigosClima.orEmpty()

                val cantidad = minOf(
                    fechas.size,
                    maximas.size,
                    minimas.size
                )

                if (cantidad == 0) {
                    mostrarError()
                    return
                }

                tvDetalleClima.text =
                    "Temperaturas máximas y mínimas"

                val resultado = StringBuilder()

                for (i in 0 until cantidad) {

                    val fecha = fechas[i]
                    val maxima = maximas[i] ?: "--"
                    val minima = minimas[i] ?: "--"

                    val condicion = descripcionClima(
                        codigos.getOrNull(i)
                    )

                    resultado.append(
                        "$fecha\n" +
                                "Máx: $maxima °C | Mín: $minima °C\n" +
                                "$condicion\n\n"
                    )
                }

                tvDatosExtra.text = resultado.toString()

                cargarIcono(codigos.firstOrNull())
            }

            override fun onFailure(
                call: Call<ClimaResponse>,
                t: Throwable
            ) {
                if (consultaActiva == "semanal") {
                    mostrarError()
                }
            }
        })
    }

    // CONVERTIR LOS CODIGOS DEL CLIMA
    private fun descripcionClima(codigo: Int?): String {

        return when (codigo) {
            0 -> "Cielo despejado"
            1 -> "Mayormente despejado"
            2 -> "Parcialmente nublado"
            3 -> "Nublado"
            45, 48 -> "Niebla"
            in 51..57 -> "Llovizna"
            in 61..67 -> "Lluvia"
            in 71..77 -> "Nieve"
            in 80..82 -> "Chubascos"
            in 85..86 -> "Chubascos de nieve"
            in 95..99 -> "Tormenta"
            else -> "Clima no disponible"
        }
    }

    // MOSTRAR IMAGEN DEL CLIMA CON GLIDE
    private fun cargarIcono(codigo: Int?) {

        val icono = when (codigo) {
            0 -> "01d"
            1 -> "02d"
            2 -> "03d"
            3 -> "04d"
            45, 48 -> "50d"
            in 51..67 -> "10d"
            in 71..77 -> "13d"
            in 80..82 -> "09d"
            in 85..86 -> "13d"
            in 95..99 -> "11d"
            else -> "02d"
        }

        val urlIcono =
            "https://openweathermap.org/img/wn/" +
                    "$icono@2x.png"

        Glide.with(this)
            .load(urlIcono)
            .into(ivClima)
    }

    // MOSTRAR ERRORES DE CONSULTA
    private fun mostrarError() {

        tvTemperatura.text = "-- °C"
        tvDetalleClima.text = "Sin información"

        tvDatosExtra.text =
            "No se pudieron obtener los datos. " +
                    "Revisa tu conexión a Internet."

        Glide.with(this).clear(ivClima)
    }

    // ACTIVIDAD 5: ENVIAR DATOS A DETAILS ACTIVITY
    private fun abrirDetallesClima() {

        val clima = ultimoClima

        if (clima == null) {

            Toast.makeText(
                this,
                "Primero consulta el clima",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val intent = Intent(
            this@PrincipalActivity,
            DetailsActivity::class.java
        )

        // PASAR INFORMACION DE OPEN-METEO
        intent.putExtra(
            "UBICACION",
            "Cuernavaca, Morelos"
        )

        intent.putExtra(
            "TEMPERATURA",
            clima.temperatura ?: Double.NaN
        )

        intent.putExtra(
            "HUMEDAD",
            clima.humedad ?: -1
        )

        intent.putExtra(
            "SENSACION",
            clima.sensacionTermica ?: Double.NaN
        )

        intent.putExtra(
            "ESTADO_CLIMA",
            descripcionClima(clima.codigoClima)
        )

        // ABRIR LA PANTALLA DE DETALLES
        startActivity(intent)
    }
}
