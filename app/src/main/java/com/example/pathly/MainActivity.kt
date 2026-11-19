package com.example.pathly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.launch
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnInicio: Button
    private lateinit var dbHelper: DatabaseHelper

    //Crear atributos para las credenciales de los proveedores
    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager : CredentialManager
    private lateinit var btnGoogle : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnInicio = findViewById(R.id.btnIniciar)

        dbHelper = DatabaseHelper(this)

        btnGoogle = findViewById(R.id.btnGoogle)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        // Crear evento para iniciar sesion
        btnInicio.setOnClickListener {
            // TODO: agregar el metodo de inicio de sesion
            iniciarSesion()
        }

        btnGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

    }

    //METODO PARA INICIAR SESION
    private fun iniciarSesion() {

        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = dbHelper.IniciarSesion(correo, pass)

        if (resultado) {
            Toast.makeText(this, "Bienvenido usuario", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)

            finish()

        } else {
            Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
        }

    }

    // 3. MÉTODO PARA AUTENTICAR EN FIREBSAE
    private fun autenticarFirebase(idToken : String){


        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            //validar que el inicio de sesion del usuario haya sido correcto
            if(inicio.isSuccessful){

                //crear una variable que guarde la info del usuario
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, PrincipalActivity :: class.java)
                startActivity(intent)
                finish()

            }else{
                Toast.makeText(this, "Error al iniciar con Firebase", Toast.LENGTH_SHORT).show()

            }

        }

    }
    // 4. MÉTODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential){

        if(credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

            try {

                //VAIRABLE QUE PERMITA IDENTIFICAR LA CREDENCIAL DE USUAROIO
                val credencialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data
                )

                autenticarFirebase(credencialGoogle.idToken)

            }catch (e : GoogleIdTokenParsingException){
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // 5. MÉTODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle(){

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        lifecycleScope.launch {

            try {

                //VERIFICAR SI LAS CRDECNCIALES DE USUSARIO PUEDEN SER UTILIZADAS
                val resultado = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = solicitud
                )

                //porcesar credencial para ver si el usuario entra o no entra
                procesarCredencial(resultado.credential)

            }catch (e : GetCredentialException){
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.javaClass.simpleName} - ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            }

        }


    }