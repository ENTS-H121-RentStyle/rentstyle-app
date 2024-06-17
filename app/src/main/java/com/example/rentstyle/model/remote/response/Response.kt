package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class ProductResponseData(
	@field:SerializedName("product_name")
	val productName: String? = null,

	@field:SerializedName("seller_id")
	val sellerId: String? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("desc")
	val desc: String? = null,

	@field:SerializedName("rent_price")
	val rentPrice: String? = null,

	@field:SerializedName("product_price")
	val productPrice: String? = null,
)

data class SellerResponseData(
	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("seller_name")
	val sellerName: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)

data class UserResponseData(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("birth_date")
	val birthDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
