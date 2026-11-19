
package com.example.pathly

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class ClimaFavorito(
    val idClima: Long,
    val ubicacion: String,
    val temperatura: Double,
    val humedad: Int,
    val sensacion: Double,
    val estadoClima: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {

        private const val DATABASE_NAME = "T.usuarios.db"
        private const val DATABASE_VERSION = 3

        // TABLA DE USUARIOS
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
        private const val COL_FECHA = "fechaNacimiento"
        private const val COL_EDAD = "Edad"
        private const val COL_ALTURA = "Altura"
        private const val COL_PESO = "peso"

        // TABLA DE CLIMAS FAVORITOS
        private const val TABLE_CLIMA_FAVORITO = "clima"
        private const val COL_ID_CLIMA_FAVORITO = "id"
        private const val COL_ID_CLIMA = "clima_id"
        private const val COL_UBICACION = "ubicacion"
        private const val COL_TEMPERATURA = "temperatura"
        private const val COL_HUMEDAD = "humedad"
        private const val COL_SENSACION = "sensacion"
        private const val COL_ESTADO_CLIMA = "estado"

        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_FECHA INTEGER NOT NULL,
                $COL_EDAD INTEGER NOT NULL,
                $COL_ALTURA INTEGER NOT NULL,
                $COL_PESO INTEGER NOT NULL
            )
        """

        private const val CREATE_CLIMA_FAVORITO = """
            CREATE TABLE IF NOT EXISTS $TABLE_CLIMA_FAVORITO (
                $COL_ID_CLIMA_FAVORITO INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ID_CLIMA INTEGER UNIQUE NOT NULL,
                $COL_UBICACION TEXT,
                $COL_TEMPERATURA REAL,
                $COL_HUMEDAD INTEGER,
                $COL_SENSACION REAL,
                $COL_ESTADO_CLIMA TEXT
            )
        """
    }

    // CREAR BASE DE DATOS
    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(CREATE_TABLE_USUARIOS)
        db.execSQL(CREATE_CLIMA_FAVORITO)
    }

    // ACTUALIZAR BASE DE DATOS
    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        if (oldVersion < 3) {
            db.execSQL(CREATE_CLIMA_FAVORITO)
        }
    }

    // REGISTRAR USUARIO
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

        val resultado = db.insert(
            TABLE_USUARIOS,
            null,
            datos
        )

        return resultado != -1L
    }

    // INICIAR SESION
    fun IniciarSesion(
        correo: String,
        password: String
    ): Boolean {

        val db = readableDatabase

        val querySql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ?
            AND $COL_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(
            querySql,
            arrayOf(correo, password)
        )

        val usuarioEncontrado = cursor.moveToFirst()

        cursor.close()

        return usuarioEncontrado
    }

    // INSERTAR CLIMA FAVORITO
    fun guardarClimaFavorito(
        idClima: Long,
        ubicacion: String,
        temperatura: Double,
        humedad: Int,
        sensacion: Double,
        estadoClima: String
    ): Boolean {

        val db = writableDatabase

        val datos = ContentValues().apply {

            put(COL_ID_CLIMA, idClima)
            put(COL_UBICACION, ubicacion)

            if (temperatura.isNaN())
                putNull(COL_TEMPERATURA)
            else
                put(COL_TEMPERATURA, temperatura)

            if (humedad == -1)
                putNull(COL_HUMEDAD)
            else
                put(COL_HUMEDAD, humedad)

            if (sensacion.isNaN())
                putNull(COL_SENSACION)
            else
                put(COL_SENSACION, sensacion)

            put(COL_ESTADO_CLIMA, estadoClima)
        }

        val resultado = db.insertWithOnConflict(
            TABLE_CLIMA_FAVORITO,
            null,
            datos,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        return resultado != -1L
    }

    // ELIMINAR CLIMA FAVORITO
    fun eliminarClimaFavorito(idClima: Long): Boolean {

        val db = writableDatabase

        val resultado = db.delete(
            TABLE_CLIMA_FAVORITO,
            "$COL_ID_CLIMA = ?",
            arrayOf(idClima.toString())
        )

        return resultado > 0
    }

    // VERIFICAR SI EL CLIMA YA ES FAVORITO
    fun existeClimaFavorito(idClima: Long): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT $COL_ID_CLIMA_FAVORITO
            FROM $TABLE_CLIMA_FAVORITO
            WHERE $COL_ID_CLIMA = ?
            """.trimIndent(),
            arrayOf(idClima.toString())
        )

        val existe = cursor.moveToFirst()

        cursor.close()

        return existe
    }

    // OBTENER TODOS LOS CLIMAS FAVORITOS
    fun obtenerClimasFavoritos(): List<ClimaFavorito> {

        val favoritos = mutableListOf<ClimaFavorito>()

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT
                $COL_ID_CLIMA,
                $COL_UBICACION,
                $COL_TEMPERATURA,
                $COL_HUMEDAD,
                $COL_SENSACION,
                $COL_ESTADO_CLIMA
            FROM $TABLE_CLIMA_FAVORITO
            ORDER BY $COL_ID_CLIMA_FAVORITO DESC
            """.trimIndent(),
            null
        )

        cursor.use {

            while (it.moveToNext()) {

                val clima = ClimaFavorito(

                    idClima = it.getLong(0),

                    ubicacion =
                        it.getString(1) ?: "Ubicación desconocida",

                    temperatura =
                        if (it.isNull(2))
                            Double.NaN
                        else
                            it.getDouble(2),

                    humedad =
                        if (it.isNull(3))
                            -1
                        else
                            it.getInt(3),

                    sensacion =
                        if (it.isNull(4))
                            Double.NaN
                        else
                            it.getDouble(4),

                    estadoClima =
                        it.getString(5) ?: ""
                )

                favoritos.add(clima)
            }
        }

        return favoritos
    }
}
