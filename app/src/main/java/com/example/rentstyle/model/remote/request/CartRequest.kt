package com.example.rentstyle.model.remote.request

import com.google.gson.annotations.SerializedName

data class CartRequest(
    @SerializedName("duration") val duration: Int,
    @SerializedName("product_id") val product_id: String,
    @SerializedName("user_id") val user_id: String
)
