package com.blez.beacontransmitter.`interface`

import com.blez.beacontransmitter.data.RegisterStatus
import com.blez.beacontransmitter.data.RegisterUser
import retrofit2.http.Body
import retrofit2.http.POST

interface BeaconAPI {
    @POST("/device")
    fun registerUser(@Body info: RegisterUser): retrofit2.Call<RegisterStatus>
}