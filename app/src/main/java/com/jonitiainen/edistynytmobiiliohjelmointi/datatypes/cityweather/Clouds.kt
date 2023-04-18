package com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.cityweather

import com.google.gson.annotations.SerializedName


data class Clouds (

  @SerializedName("all" ) var all : Int? = null

)