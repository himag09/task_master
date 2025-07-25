package com.himag09.taskmaster.data

import androidx.room.TypeConverter
import java.util.Date

/**
 *  Room no sabe como se guardan tipos complejos como Date o Priority.
 *  con esta clase podemos almacenarlos como primitivos(Long, String)
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromPriority(value: String?): Priority? {
        return value?.let { Priority.valueOf(it) }
    }

    @TypeConverter
    fun priorityToString(priority: Priority?): String? {
        return priority?.name
    }
}

