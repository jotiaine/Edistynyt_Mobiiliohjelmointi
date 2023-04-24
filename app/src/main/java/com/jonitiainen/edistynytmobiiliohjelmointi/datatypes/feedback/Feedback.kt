package com.jonitiainen.edistynytmobiiliohjelmointi.datatypes.feedback

import com.google.gson.annotations.SerializedName


data class Feedback (

  @SerializedName("id"       ) var id       : Int?    = null,
  @SerializedName("name"     ) var name     : String? = null,
  @SerializedName("location" ) var location : String? = null,
  @SerializedName("value"    ) var value    : String? = null

)
{
  // yliajetaan luokan toString-metodi
  // jotta listView käyttää tätä Feedbackin tulostamiseen
  // muutoin ListView tulostaa kaikki sisällöt, mikä näyttää huonolta
  // ja saattaa muutenkin näyttää liikaa tietoa käyttöliittymässä
  override fun toString(): String {
    return "${name}: ${location}"
  }
}