package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id") val id: String,
    @SerializedName("order_id") val orderId: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("rating") val rating: Int,
    @SerializedName("review") val review: String,
    @SerializedName("image") val image: String?
)