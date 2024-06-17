package com.example.rentstyle.model.remote.response

import com.google.gson.annotations.SerializedName

data class User(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("name")
    val name: String
)

data class Pref(
    @field:SerializedName("user_id")
    val userId: String,

    @field:SerializedName("category")
    val category: List<String>,

    @field:SerializedName("color")
    val color: List<String>,

    @field:SerializedName("size")
    val size: String
)
