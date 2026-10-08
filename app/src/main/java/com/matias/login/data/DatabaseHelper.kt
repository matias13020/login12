package com.matias.login.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class SensorDevice(
    val id: Int,
    val name: String,
    val value: String,
    val status: Boolean
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "iot_login.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_SENSORS = "sensors"
        const val COLUMN_SENSOR_ID = "id"
        const val COLUMN_SENSOR_NAME = "name"
        const val COLUMN_SENSOR_VALUE = "value"
        const val COLUMN_SENSOR_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createUsersTable)

        val createSensorsTable = ("CREATE TABLE $TABLE_SENSORS (" +
                "$COLUMN_SENSOR_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_SENSOR_NAME TEXT, " +
                "$COLUMN_SENSOR_VALUE TEXT, " +
                "$COLUMN_SENSOR_STATUS INTEGER)")
        db.execSQL(createSensorsTable)

        // Insert default initial IoT devices
        val cv1 = ContentValues().apply {
            put(COLUMN_SENSOR_NAME, "Sensor Temperatura Sala")
            put(COLUMN_SENSOR_VALUE, "24.5 °C")
            put(COLUMN_SENSOR_STATUS, 1)
        }
        db.insert(TABLE_SENSORS, null, cv1)

        val cv2 = ContentValues().apply {
            put(COLUMN_SENSOR_NAME, "Bomba de Agua (Relé)")
            put(COLUMN_SENSOR_VALUE, "Activo")
            put(COLUMN_SENSOR_STATUS, 1)
        }
        db.insert(TABLE_SENSORS, null, cv2)

        val cv3 = ContentValues().apply {
            put(COLUMN_SENSOR_NAME, "Sensor Humedad Suelo")
            put(COLUMN_SENSOR_VALUE, "58 %")
            put(COLUMN_SENSOR_STATUS, 0)
        }
        db.insert(TABLE_SENSORS, null, cv3)

        // Default admin user
        val adminCv = ContentValues().apply {
            put(COLUMN_USERNAME, "admin")
            put(COLUMN_PASSWORD, "1234")
        }
        db.insert(TABLE_USERS, null, adminCv)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SENSORS")
        onCreate(db)
    }

    fun registerUser(user: String, pass: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user)
            put(COLUMN_PASSWORD, pass)
        }
        val result = db.insert(TABLE_USERS, null, values)
        return result != -1L
    }

    fun checkUser(user: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(user, pass),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun getAllSensors(): List<SensorDevice> {
        val list = mutableListOf<SensorDevice>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SENSORS", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENSOR_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENSOR_NAME))
                val value = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENSOR_VALUE))
                val statusInt = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENSOR_STATUS))
                list.add(SensorDevice(id, name, value, statusInt == 1))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateSensorStatus(id: Int, status: Boolean) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SENSOR_STATUS, if (status) 1 else 0)
        }
        db.update(TABLE_SENSORS, values, "$COLUMN_SENSOR_ID = ?", arrayOf(id.toString()))
    }
}
