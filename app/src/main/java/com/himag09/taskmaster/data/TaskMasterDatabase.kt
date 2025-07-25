package com.himag09.taskmaster.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskMasterDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: TaskMasterDatabase? = null

        fun getDatabase(context: Context): TaskMasterDatabase {
            /**
             * Retornamos la instancia si ya existe, si no existe, creamos
             * la nueva instancia de la base de datos.
             */
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    TaskMasterDatabase::class.java,
                    "task_master_database"
                )
                    // La estrategia que usamos si cambiamos version de la base de datos -> La borramos y la reconstruimos.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}