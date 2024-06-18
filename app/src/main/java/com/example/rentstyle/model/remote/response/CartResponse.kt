package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class CartResponse(
    @SerializedName("id") val id: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("Product") val product: Product,
    @SerializedName("product_id") val productId: String,
    @SerializedName("user_id") val userId: String
)
data class Product(
    @SerializedName("product_name") val productName: String,
    @SerializedName("image") val image: String,
    @SerializedName("rent_price") val rentPrice: Int
)
