package com.blez.beacontransmitter.data

import com.google.gson.annotations.SerializedName

data class RegisterStatus(
    @SerializedName("message")
    val message: String,
    @SerializedName("serial")
    val serial: Int
)