package com.example.rentstyle.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "products")
data class LocalProduct(
    @PrimaryKey val id: String,
    @SerializedName("product_name") val productName: String,
    val category: String,
    val color: String,
    val size: String,
    val city: String,
    @SerializedName("image") val image: String?,
    @SerializedName("rent_price") val rentPrice: Int,
    @SerializedName("count_num_rating") val countNumRating: Int,
    @SerializedName("avg_rating") val avgRating: Float
)
