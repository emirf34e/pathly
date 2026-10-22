package com.example.pathly

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

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

        // Inicializar la base de datos
        dbHelper = DatabaseHelper(this)

        // Crear el evento para registrar un usuario
        bntRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        // Obtener la información que escribe el usuario
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()
        val fechanStr = etFechaNacimiento.text.toString().trim()
        val edadStr = etEdad.text.toString().trim()
        val alturaStr = etAltura.text.toString().trim()
        val pesoStr = etPeso.text.toString().trim()

        // Validar que el usuario llene los campos
        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || fechanStr.isEmpty() || edadStr.isEmpty() || alturaStr.isEmpty() || pesoStr.isEmpty()) {
            Toast.makeText(this, "Complete los campos", Toast.LENGTH_LONG).show()
            return
        }

        // Convertir los valores de texto a Int de forma segura
        val fecha = fechanStr.toIntOrNull()
        val edad = edadStr.toIntOrNull()
        val altura = alturaStr.toIntOrNull()
        val peso = pesoStr.toIntOrNull()

        if (fecha == null || edad == null || altura == null || peso == null) {
            Toast.makeText(this, "Ingrese valores numéricos válidos en fecha, edad, altura y peso", Toast.LENGTH_LONG).show()
            return
        }

        // Llamar al método pasando las variables correctas
        val resultado = dbHelper.registrarUsuario(nombre, correo, pass, fecha, edad, altura, peso)

        if (resultado) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_LONG).show()
            // Opcional: limpiar campos o cerrar la actividad de registro
            finish()
        } else {
            Toast.makeText(this, "Error al registrar (es posible que el correo ya esté registrado)", Toast.LENGTH_SHORT).show()
        }
    }
}