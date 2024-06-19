package com.example.rentstyle.model.remote.response

import com.example.rentstyle.model.Product
import com.google.gson.annotations.SerializedName

data class FavoriteResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("Product")
    val product: ProductFavorite?
)

data class ProductFavorite(
    @SerializedName("image")
    val image: String?,

    @SerializedName("product_id")
    val productId: String?
)