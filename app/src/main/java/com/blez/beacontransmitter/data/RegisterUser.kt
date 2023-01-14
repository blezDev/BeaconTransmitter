package com.blez.beacontransmitter.data

import com.google.gson.annotations.SerializedName

data class RegisterUser(
    @SerializedName("email")
    val email : String,
    @SerializedName("name")
    val name : String,
    @SerializedName("empid")
    val empid : String,
    @SerializedName("contacts")
    val contacts : List<String>,
    @SerializedName("bloodGroup")
    val bloodGroup : String
)
