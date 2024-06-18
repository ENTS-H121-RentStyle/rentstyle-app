package com.example.rentstyle.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val productId: String,
    val userId: String
)
