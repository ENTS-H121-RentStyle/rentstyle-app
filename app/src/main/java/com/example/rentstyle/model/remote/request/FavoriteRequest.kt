package com.example.rentstyle.model.remote.request

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    val product_id: String,
    val user_id: String,
    @SerializedName("id") val id: String? = null // `id` bersifat opsional
)
