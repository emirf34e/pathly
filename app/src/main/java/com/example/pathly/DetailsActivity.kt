package com.example.pathly

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Locale

class DetailsActivity : AppCompatActivity() {

    private lateinit var tvDetalleUbicacion: TextView
    private lateinit var tvDetalleTemperatura: TextView
    private lateinit var tvDetalleEstado: TextView
    private lateinit var tvDetalleHumedad: TextView
    private lateinit var tvDetalleSensacion: TextView
    private lateinit var btnFavorito: ImageButton

    private lateinit var contenedorFavoritos: LinearLayout
    private lateinit var tvSinFavoritos: TextView
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var bottomNavigation : BottomNavigationView

    private lateinit var auth : FirebaseAuth

    private lateinit var credentialManager: CredentialManager


    private var idClima = -1L
    private var ubicacion = ""
    private var temperatura = Double.NaN
    private var humedad = -1
    private var sensacion = Double.NaN
    private var estado = ""
    private var climaFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        // COMPONENTES DEL CLIMA
        tvDetalleUbicacion =
            findViewById(R.id.tvDetalleUbicacion)

        tvDetalleTemperatura =
            findViewById(R.id.tvDetalleTemperatura)

        tvDetalleEstado =
            findViewById(R.id.tvDetalleEstado)

        tvDetalleHumedad =
            findViewById(R.id.tvDetalleHumedad)

        tvDetalleSensacion =
            findViewById(R.id.tvDetalleSensacion)

        btnFavorito =
            findViewById(R.id.btnFavorito)

        // COMPONENTES DE LA LISTA
        contenedorFavoritos =
            findViewById(R.id.contenedorFavoritos)

        tvSinFavoritos =
            findViewById(R.id.tvSinFavoritos)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        auth = FirebaseAuth.getInstance()

        credentialManager = CredentialManager.create(this)

        // BASE DE DATOS
        dbHelper = DatabaseHelper(this)

        recibirClima()
        mostrarClima()
        mostrarClimasFavoritos()

        // BOTON CORAZON
        btnFavorito.setOnClickListener {
            cambiarFavorito()
        }

        menuNavegacion()
    }

    // RECIBIR INFORMACION DEL CLIMA
    private fun recibirClima() {

        ubicacion =
            intent.getStringExtra("UBICACION")?.trim().orEmpty()

        temperatura =
            intent.getDoubleExtra("TEMPERATURA", Double.NaN)

        humedad =
            intent.getIntExtra("HUMEDAD", -1)

        sensacion =
            intent.getDoubleExtra("SENSACION", Double.NaN)

        estado =
            intent.getStringExtra("ESTADO_CLIMA") ?: ""

        // GENERAR IDENTIFICADOR DE LA UBICACION
        idClima = crearIdDeUbicacion(ubicacion)
    }

    // IDENTIFICAR CADA UBICACION
    private fun crearIdDeUbicacion(nombre: String): Long {

        if (nombre.isBlank() ||
            nombre == "Ubicación desconocida") {
            return -1L
        }

        val nombreNormalizado =
            nombre.trim().lowercase(Locale.ROOT)

        return (nombreNormalizado.hashCode().toLong()
                and 0xffffffffL) + 1L
    }

    // MOSTRAR DATOS DEL CLIMA
    private fun mostrarClima() {

        tvDetalleUbicacion.text =
            if (ubicacion.isBlank())
                "Ubicación desconocida"
            else ubicacion

        tvDetalleTemperatura.text =
            if (temperatura.isNaN())
                "-- °C"
            else
                "$temperatura °C"

        tvDetalleEstado.text = estado

        tvDetalleHumedad.text =
            if (humedad == -1)
                "Humedad: --"
            else
                "Humedad: $humedad%"

        tvDetalleSensacion.text =
            if (sensacion.isNaN())
                "Sensación: --"
            else
                "Sensación: $sensacion °C"

        // VERIFICAR SI YA ESTA EN FAVORITOS
        climaFavorito =
            if (idClima != -1L)
                dbHelper.existeClimaFavorito(idClima)
            else
                false

        // EL BOTON NO SE DESACTIVA
        btnFavorito.isEnabled = true

        actualizarCorazon()
    }

    // ACTUALIZAR IMAGEN DEL CORAZON
    private fun actualizarCorazon() {

        if (climaFavorito) {

            btnFavorito.setImageResource(
                R.drawable.favorito
            )

            btnFavorito.contentDescription =
                "Quitar de favoritos"

        } else {

            btnFavorito.setImageResource(
                R.drawable.no_favorito
            )

            btnFavorito.contentDescription =
                "Agregar a favoritos"
        }
    }

    // GUARDAR O ELIMINAR FAVORITO
    private fun cambiarFavorito() {

        if (idClima == -1L) {

            Toast.makeText(
                this,
                "No se recibió una ubicación válida",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (climaFavorito) {

            // ELIMINAR DE FAVORITOS
            val eliminado =
                dbHelper.eliminarClimaFavorito(idClima)

            if (eliminado) {

                climaFavorito = false
                actualizarCorazon()
                mostrarClimasFavoritos()

                Toast.makeText(
                    this,
                    "Clima de $ubicacion eliminado de favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            // GUARDAR EN FAVORITOS
            val guardado = dbHelper.guardarClimaFavorito(
                idClima = idClima,
                ubicacion = ubicacion,
                temperatura = temperatura,
                humedad = humedad,
                sensacion = sensacion,
                estadoClima = estado
            )

            if (guardado) {

                climaFavorito = true
                actualizarCorazon()
                mostrarClimasFavoritos()

                Toast.makeText(
                    this,
                    "Clima de $ubicacion agregado a favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // MOSTRAR LISTA DE CLIMAS FAVORITOS
    private fun mostrarClimasFavoritos() {

        contenedorFavoritos.removeAllViews()

        val favoritos =
            dbHelper.obtenerClimasFavoritos()

        tvSinFavoritos.visibility =
            if (favoritos.isEmpty())
                View.VISIBLE
            else
                View.GONE

        for (clima in favoritos) {

            // TARJETA DE CADA UBICACION
            val tarjeta = MaterialCardView(this).apply {

                radius = dp(16).toFloat()
                cardElevation = dp(3).toFloat()

                setCardBackgroundColor(
                    Color.parseColor("#FFF8EF")
                )

                isClickable = true
                isFocusable = true
            }

            // TEXTO DE LA TARJETA
            val texto = TextView(this).apply {

                val temp =
                    if (clima.temperatura.isNaN())
                        "-- °C"
                    else
                        "${clima.temperatura} °C"

                text = "${clima.ubicacion}   •   $temp\n" +
                        clima.estadoClima

                setTextColor(
                    Color.parseColor("#443025")
                )

                setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    15f
                )

                setPadding(
                    dp(16),
                    dp(14),
                    dp(16),
                    dp(14)
                )
            }

            tarjeta.addView(texto)

            val parametros = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(10)
            }

            contenedorFavoritos.addView(
                tarjeta,
                parametros
            )

            // SELECCIONAR UN CLIMA GUARDADO
            tarjeta.setOnClickListener {

                idClima = clima.idClima
                ubicacion = clima.ubicacion
                temperatura = clima.temperatura
                humedad = clima.humedad
                sensacion = clima.sensacion
                estado = clima.estadoClima

                mostrarClima()

                // REGRESAR AL INICIO DEL SCROLL
                findViewById<ScrollView>(
                    R.id.main
                ).smoothScrollTo(0, 0)

                Toast.makeText(
                    this,
                    "Mostrando clima guardado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun dp(valor: Int): Int {
        return (
                valor * resources.displayMetrics.density
                ).toInt()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }


    private fun menuNavegacion() {

        // SELECCIONAR INICIO
        bottomNavigation.selectedItemId = R.id.nav_inicio

        // EVENTOS DE NAVEGACION
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> {

                    irAInicio()
                    true
                }

                R.id.nav_Salir -> {

                    cerrarSesionGoogle()
                    true
                }

                else -> false
            }
        }


        // CUANDO SE PRESIONA INICIO NUEVAMENTE
        bottomNavigation.setOnItemReselectedListener { item ->

            if (item.itemId == R.id.nav_inicio) {
                irAInicio()
            }
        }
    }

    // REGRESAR A LA PANTALLA PRINCIPAL
    private fun irAInicio() {

        Toast.makeText(
            this,
            "Regresando al inicio",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(
            this,
            PrincipalActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
        finish()
    }

    private fun cerrarSesionGoogle() {

        auth.signOut()

        lifecycleScope.launch {

            try {

                credentialManager.clearCredentialState(
                    androidx.credentials.ClearCredentialStateRequest()
                )

                Toast.makeText(
                    this@DetailsActivity,
                    "Sesión cerrada correctamente",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {

                Toast.makeText(
                    this@DetailsActivity,
                    "Error al limpiar credenciales",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val intent = Intent(
                this@DetailsActivity,
                MainActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }





}