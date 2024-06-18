package com.example.rentstyle.model.remote.response

import com.example.rentstyle.model.Product

data class FavoriteResponse(
    val product_id: String?,
    val user_id: String,
    val id: String,
    val image: String?
) {
    fun toProduct(): Product? {
        val productId = this.product_id ?: return null

        return Product(
            id = productId,
            productId = listOf(productId),
            productName = "", // Isi jika Anda memiliki nama produk
            category = "", // Isi jika Anda memiliki kategori
            color = "", // Isi jika Anda memiliki warna
            size = "", // Isi jika Anda memiliki ukuran
            city = "", // Isi jika Anda memiliki kota
            image = this.image,
            rentPrice = 0, // Isi jika Anda memiliki harga sewa
            countNumRating = 0, // Isi jika Anda memiliki jumlah rating
            avgRating = 0f, // Isi jika Anda memiliki rata-rata rating
            reviews = emptyList() // Isi jika Anda memiliki ulasan
        )
    }
}