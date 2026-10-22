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
        private const val COL_FECHA = "fechaNacimiento"
        private const val COL_EDAD = "Edad"
        private const val COL_ALTURA = "Altura"
        private const val COL_PESO = "peso"
    }

    // MÉTODO PARA CREAR LA BASE DE DATOS Y SUS TABLAS
    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_FECHA INTEGER NOT NULL,
                $COL_EDAD INTEGER NOT NULL,
                $COL_ALTURA INTEGER NOT NULL,
                $COL_PESO INTEGER NOT NULL
            )
        """.trimIndent()

        db?.execSQL(crearTablaUsuarios)
    }

    // MÉTODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    // MÉTODO PARA REGISTRAR UN USUARIO (INSERTAR)
    fun registrarUsuario(
        nombre: String,
        correo: String,
        password: String,
        fecha: Int,
        edad: Int,
        altura: Int,
        peso: Int
    ): Boolean {
        val db = writableDatabase

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

    // METODO PARA INICIAR SESION
    fun IniciarSesion(correo: String, password: String): Boolean {
        val db = readableDatabase

        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(
            query_sql, arrayOf(correo, password)
        )

        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado
    }
}