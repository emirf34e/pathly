
package com.example.pathly

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailsActivity : AppCompatActivity() {

    private lateinit var tvDetalleUbicacion: TextView
    private lateinit var tvDetalleTemperatura: TextView
    private lateinit var tvDetalleEstado: TextView
    private lateinit var tvDetalleHumedad: TextView
    private lateinit var tvDetalleSensacion: TextView
    private lateinit var btnFavorito: ImageButton

    private var climaFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

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

        mostrarInformacionClima()
    }

    // METODO PARA OBTENER LA INFORMACION QUE VIENE DEL API
    private fun mostrarInformacionClima() {

        val ubicacion =
            intent.getStringExtra("UBICACION")

        val temperatura =
            intent.getDoubleExtra("TEMPERATURA", Double.NaN)

        val humedad =
            intent.getIntExtra("HUMEDAD", -1)

        val sensacion =
            intent.getDoubleExtra("SENSACION", Double.NaN)

        val estado =
            intent.getStringExtra("ESTADO_CLIMA")

        tvDetalleUbicacion.text = ubicacion

        tvDetalleTemperatura.text =
            if (temperatura.isNaN()) "-- °C"
            else "$temperatura °C"

        tvDetalleEstado.text = estado

        tvDetalleHumedad.text =
            if (humedad == -1) "Humedad: --"
            else "Humedad: $humedad%"

        tvDetalleSensacion.text =
            if (sensacion.isNaN()) "Sensación: --"
            else "Sensación: $sensacion °C"

        // BOTON DE FAVORITOS
        btnFavorito.setOnClickListener {

            climaFavorito = !climaFavorito

            if (climaFavorito) {

                btnFavorito.setImageResource(R.drawable.favorito)

                Toast.makeText(
                    this,
                    "Clima de $ubicacion agregado a favoritos",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                btnFavorito.setImageResource(R.drawable.no_favorito)

                Toast.makeText(
                    this,
                    "Clima de $ubicacion eliminado de favoritos",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

}
