package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class PrefResponse(
	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("size")
	val size: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("category")
	val category: String? = null
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