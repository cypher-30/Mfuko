package com.chama.groupmoneymanager.data.remote

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("token") val token: String
)