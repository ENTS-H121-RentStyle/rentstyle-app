package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class ResponseOrderItem(

	@field:SerializedName("order_status")
	val orderStatus: String? = null,

	@field:SerializedName("order_date")
	val orderDate: String? = null,

	@field:SerializedName("note")
	val note: Any? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("product_id")
	val productId: String? = null,

	@field:SerializedName("rent_price")
	val rentPrice: Int? = null,

	@field:SerializedName("service_fee")
	val serviceFee: Int? = null,

	@field:SerializedName("deposit")
	val deposit: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("Product")
	val product: OrderProduct?,

	@field:SerializedName("rent_duration")
	val rentDuration: Int? = null,

	@field:SerializedName("total_payment")
	val totalPayment: Int? = null,

	@field:SerializedName("return_date")
	val returnDate: String? = null
)

data class OrderProduct(
	@field:SerializedName("product_name")
	val productName: String,

	@field:SerializedName("image")
	val productImage: String
)