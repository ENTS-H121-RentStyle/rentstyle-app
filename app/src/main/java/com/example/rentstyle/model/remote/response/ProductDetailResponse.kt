package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class ProductDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("product_name") val productName: String,
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("category") val category: String,
    @SerializedName("image") val image: String?,
    @SerializedName("color") val color: String,
    @SerializedName("size") val size: String,
    @SerializedName("desc") val desc: String,
    @SerializedName("rent_price") val rentPrice: Int,
    @SerializedName("product_price") val productPrice: Int,
    @SerializedName("Seller") val seller: Seller,
    @SerializedName("Reviews") val reviews: List<Review>
)