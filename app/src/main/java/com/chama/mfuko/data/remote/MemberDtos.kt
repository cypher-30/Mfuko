package com.chama.mfuko.data.remote

import com.google.gson.annotations.SerializedName

data class MemberStatusDto(
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("amountPaid")
    val amountPaid: Double,
    @SerializedName("totalDue")
    val totalDue: Double,
    @SerializedName("status")
    val status: String
)