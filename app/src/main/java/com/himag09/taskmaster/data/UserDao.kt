package com.himag09.taskmaster.data


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // La función devuelve el ID del usuario insertado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    /* Para borrar usuario
    * No usamos room.Delete ya que habria que pasar la instancia completa de la entidad user
    * con delete from, tenemos flexibilidad y basta con pasar solo el id del usuario.
    * */
    @Query("DELETE from users WHERE id = :userId")
    suspend fun deleteUser(userId: Int)

    @Query("SELECT * from users WHERE id = :id")
    fun getUser(id: Int): Flow<User>

    // Para el login, como es solo un usuario, basta con esto.
    @Query("SELECT * from users LIMIT 1")
    fun getFirstUser(): Flow<User?>

    @Delete
    suspend fun deleteUser(user: User)
}