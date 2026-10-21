package com.example.pathly

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pathly.DatabaseHelper

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var etFechaNacimiento: EditText

    private lateinit var etEdad: EditText

    private lateinit var etAltura: EditText

    private lateinit var etPeso: EditText
    private lateinit var bntRegistrar: Button

    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etEdad = findViewById(R.id.etEdad)
        etAltura = findViewById(R.id.etAltura)
        etPeso = findViewById(R.id.etPeso)
        bntRegistrar = findViewById(R.id.btnRegistrar)



        // Crear el evento para registrar un usuario
        dbHelper = DatabaseHelper(this)

        bntRegistrar.setOnClickListener {
            registrarUsuario()
        }

    }

    private fun registrarUsuario() {
        // Obtener la informacion que escribe el usuario
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()
        val fechan = etFechaNacimiento.text.toString().trim()
        val edad = etEdad.text.toString().trim()
        val altura = etAltura.text.toString().trim()
        val peso = etPeso.text.toString().trim()



        // validar que el usuario llene los campos
        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || fechan.isEmpty() || edad.isEmpty()|| altura.isEmpty()|| peso.isEmpty()) {
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_LONG).show()
            return
        }
        val resultado = dbHelper.registrarUsuario(nombre, correo, pass, fechan, edad, altura, peso)

        if (resultado) {
            // Si el resultado es verdadero
            Toast.makeText(this, "Regustro exitoso", Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(this,"Error al registrar", Toast.LENGTH_SHORT).show()
        }

    }

}