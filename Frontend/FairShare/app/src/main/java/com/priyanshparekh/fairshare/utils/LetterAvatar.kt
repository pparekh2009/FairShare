package com.priyanshparekh.fairshare.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable

class LetterAvatar(context: Context, color: Int, var pLetters: String, paddingInDp: Int) : ColorDrawable(color) {
    var paint: Paint = Paint()
    var rBounds: Rect = Rect()

    private var ONE_DP = 0.0f
    private val pResources: Resources = context.resources
    private val pPadding: Int
    var pSize: Int = 0
    var pMesuredTextWidth: Float = 0f

    var pBoundsTextWidth: Int = 0
    var pBoundsTextHeight: Int = 0

    init {
        ONE_DP = 1 * pResources.displayMetrics.density
        this.pPadding = Math.round(paddingInDp * ONE_DP)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        paint.isAntiAlias = true

        do {
            paint.textSize = (++pSize).toFloat()
            paint.getTextBounds(pLetters, 0, pLetters.length, rBounds)
        } while ((rBounds.height() < (canvas.height - pPadding)) && (paint.measureText(pLetters) < (canvas.width - pPadding)))

        paint.textSize = pSize.toFloat()
        pMesuredTextWidth = paint.measureText(pLetters)
        pBoundsTextHeight = rBounds.height()

        val xOffset = ((canvas.width - pMesuredTextWidth) / 2)
        val yOffset = (pBoundsTextHeight + (canvas.height - pBoundsTextHeight) / 2).toFloat()
        paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD))
        paint.color = -0x1
        canvas.drawText(pLetters, xOffset, yOffset, paint)
    }
}