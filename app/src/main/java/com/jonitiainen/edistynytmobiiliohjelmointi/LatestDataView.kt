package com.jonitiainen.edistynytmobiiliohjelmointi

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView

class LatestDataView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var maxRows = 5

    // rest of the basic methods here from the template above
    // alkuvaiheessa tämä riittää kun käytetään compound controlia
    // huom: onDraw ja onMeasure ym. ovat jo valmiiksi tehty LinearLayoutissa
    init {
        // vaihdetaan LinearLayoutin orientation VERTICALiksi
        this.orientation = VERTICAL

        // jotta aloituskorkeus saadaaan heti alussa oikeaksi, voidaan tehdä seuraavaa:
        // tehdään koodin muistiin uusi TextView, jota ei käytetä missään
        // kerrotaan tämä luku maxRows-muuttujalla eli 5
        // lisätään päälle mahdolliset LinearLayoutin omat lisäkorkeudet (padding ym.)
        // lasketaan kaikki yhteen => tarvittava korkeus LinearLayoutille

        // tehdään uusi TextView muistiin ja käsketään Androidia mittaamaan se tällä näytöllä
        var someTextView: TextView = TextView(context)
        someTextView.measure(0, 0)
        var rowHeight = someTextView.measuredHeight

        this.measure(0, 0)

        var additionalHeight = this.measuredHeight + (maxRows * rowHeight)
        this.minimumHeight = additionalHeight
    }

    // this function can be called where it's needed, init() or an Activity.
    fun addData(message: String) {
        // ennen kuin lisätään uusi TextView, huolehditaan siitä
        // että LinearLayoutissa ei ole ylimääräisiä TextViewejä (max 5kpl)
        while (this.childCount >= maxRows) {
            this.removeViewAt(0)
        }

        // uusi textView
        var newTextView: TextView = TextView(context) as TextView
        newTextView.setText(message)
        newTextView.setBackgroundColor(Color.BLACK)
        newTextView.setTextColor(Color.YELLOW)
        this.addView(newTextView)

        // fade in animation
        val animation = AnimationUtils.loadAnimation(context, R.anim.custom_fade)
        // starting the animation
        newTextView.startAnimation(animation)
    }
}