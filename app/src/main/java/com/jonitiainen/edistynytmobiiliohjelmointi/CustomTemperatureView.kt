package com.jonitiainen.edistynytmobiiliohjelmointi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class CustomTemperatureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    //  muuttuja, joka pitää kirjaa aktiivisesta lämpötilasta
    private var temperature: Int = 0

    // funktio, jolla lämpötila voidaan asettaa esim. fragmentista käsin
    fun changeTemperature(temp: Int) {
        temperature = temp

        if (temperature > 0) {
            paint.color = Color.RED
        } else if (temperature < 0) {
            paint.color = Color.BLUE
        } else {
            paint.color = Color.GREEN
        }

        // Android ei oletuksena piirrä CustomViewiä uusiksi
        // siltä varalta jos data taustalla muuttuu.
        // tämän takia meidän pitää itse ilmoittaa Androidille
        // että CustomView pitää piirtää uusiksi.
        invalidate()
        requestLayout()
    }

    // your helper variables etc. can be here
    init {
        // this is constructor of your component, all initializations go here
        // define the colors!
        paint.color = Color.BLUE
        textPaint.color = Color.WHITE
        textPaint.textSize = 100f
        textPaint.textAlign = Paint.Align.CENTER

        // lihavointi
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // here you can do all the drawing
        // you can do all the drawing through the canvas-object
        // parameters: x-coordinate, y-coordinate, size, color
        canvas.drawCircle(width.toFloat() / 2, width.toFloat() / 2, width.toFloat() / 2, paint)

        // parameters: content, x, y, color
        // pieni 30px offset lisätty y-akseliin, jotta teksti on keskempänä näyttöä
        canvas.drawText(
            "${temperature}℃", width.toFloat() / 2, width.toFloat() / 2 + 30, textPaint
        );
    }

    // CustomViewin oletuskoko
    val size = 200

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Try for a width based on our minimum
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        var w: Int = View.resolveSizeAndState(minw, widthMeasureSpec, 1)

        // if no exact size given (either dp or match_parent)
        // use this one instead as default (wrap_content)
        if (w == 0) {
            w = size * 2
        }

        // Whatever the width ends up being, ask for a height that would let the view
        // get as big as it can
        // val minh: Int = View.MeasureSpec.getSize(w) + paddingBottom + paddingTop
        // in this case, we use the height the same as our width, since it's a circle
        val h: Int = View.resolveSizeAndState(
            View.MeasureSpec.getSize(w), heightMeasureSpec, 0
        )

        setMeasuredDimension(w, h)
    }

// note, if you use this version, in your onDraw-method, remember to use the
// canvas size when drawing the circle. for example:
// canvas.width.toFloat()
}