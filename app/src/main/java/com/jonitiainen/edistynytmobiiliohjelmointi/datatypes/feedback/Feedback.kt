package com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.feedback

import com.google.gson.annotations.SerializedName


data class Feedback (

  @SerializedName("id"       ) var id       : Int?    = null,
  @SerializedName("name"     ) var name     : String? = null,
  @SerializedName("location" ) var location : String? = null,
  @SerializedName("value"    ) var value    : String? = null

)