package com.example.rentstyle.model.database


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.example.rentstyle.model.local.LocalProduct

@Dao
interface ProductDAO {
    @Query("SELECT * FROM products")
    fun getProducts(): PagingSource<Int, LocalProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<LocalProduct>)

    @Query("DELETE FROM products")
    suspend fun clearAll()

    // Extension function for RoomDatabase to use transactions with coroutines
    suspend fun withTransaction(block: suspend () -> Unit) {
        (this as? RoomDatabase)?.withTransaction {
            block()
        }
    }
}