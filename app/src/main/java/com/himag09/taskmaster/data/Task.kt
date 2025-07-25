package com.himag09.taskmaster.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "tasks",
    // clave foreanea relacionada a user.
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // CASCADE por si se borra el usuario, borramos sus tareas.
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val title: String,
    val description: String?,
    val dueDate: Date,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val creationDate: Date,
    val lastModifiedDate: Date
)

enum class Priority {
    LOW, MEDIUM, HIGH
}