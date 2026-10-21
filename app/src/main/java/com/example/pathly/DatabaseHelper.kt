package com.example.pathly

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(

    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "T.usuarios.db"
        private const val DATABASE_VERSION = 1
        private const val COL_ID = "id"
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
        private const val COL_FECHA = "fechanacimiento"
        private const val COL_EDAD = "Edad"
        private const val COL_ALTURA = "Altura"
        private const val COL_PESO = "peso"

    }

    //METODO PARA CREAR LA BASE DE DATOS Y SUS TABLAS
    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
                $COL_FECHA TEXT NOT NULL,
                $COL_EDAD TEXT NOT NULL,
                $COL_ALTURA TEXT NOT NULL,
                $COL_PESO TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(crearTablaUsuarios)
    }

    //METODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    //METODO PARA REGISTRAR UN USUARIO (INSERTAR)
    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        fecha: String,
        edad: String,
        altura: String,
        peso: String
    ): Boolean {

        //Crear variable con permisos para escribir en la BD
        val db = writableDatabase

        //Craer variable para guardar la informacion
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
            put(COL_FECHA, fecha)
            put(COL_EDAD, edad)
            put(COL_ALTURA, altura)
            put(COL_PESO, peso)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        return resultado != -1L
    }
}
