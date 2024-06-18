package com.example.rentstyle.model.remote.response

import com.example.rentstyle.model.Product
import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("currentPage") val currentPage: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("products") val products: List<Product>
)
