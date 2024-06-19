package com.example.rentstyle.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE (favId = :favId and userId = :userId)")
    suspend fun deleteFavorite(favId:String, userId: String)

    @Query("SELECT * FROM favorites WHERE (productId = :productId and userId = :userId)")
    suspend fun getFavorite(productId: String, userId: String) : Favorite?

    @Query("SELECT favId FROM favorites WHERE userId = :userId")
    suspend fun getAllFavoriteId(userId: String): List<String>?

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)
}
