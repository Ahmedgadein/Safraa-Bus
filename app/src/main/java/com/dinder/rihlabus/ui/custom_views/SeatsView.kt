package com.dinder.rihlabus.ui.custom_views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dinder.rihlabus.data.model.SquareBound

class SeatsView : View {
    private var seats: MutableMap<Int, Boolean> = (1..50).toList().map {
        Pair(it, false)
    }.toMap().toMutableMap()

    private lateinit var paint: Paint
    private lateinit var textPaint: Paint
    private val _bounds: MutableList<SquareBound> = mutableListOf()


    val _space = 20f
    var startX = 0f
    var startY = 0f
    var _squareSize = 0f
    var _textSize = 0f

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val eventAction = event?.action


        // Click Coordinates
        val x = event?.x
        val y = event?.y


        when (eventAction) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_UP -> {
                val index: Int? = getClickEventSeatNumber(x!!, y!!)
                Log.i("SeatView", "Coordinates: $x, $y")
                Log.i("SeatView", "Index: $index")
                if (index != null) {
                    onSeatClicked(index)
                }
            }
            MotionEvent.ACTION_MOVE -> {}
        }


        invalidate()
        return true
    }

    private fun init() {
        val availableWidth = width - paddingRight - paddingLeft
        _squareSize = (availableWidth - (_space * (7 - 1))) / 7
        _textSize = 0.34f * _squareSize
        startX = paddingLeft.toFloat()
        startY = (height - 12 * _squareSize - 11 * _space) / 2

        paint = Paint()
        textPaint = Paint()
        paint.color = Color.GRAY

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        textPaint.textSize = _textSize
    }

    fun setSeats(seats: Map<Int, Boolean>) {
        this.seats = seats.toMutableMap()
    }

    fun getSeats() = seats

    private fun initializeSeat(seatNumber: Int) {
        setSeatColor(seatNumber)
        addBound(seatNumber, startX, startX + _squareSize, startY, startY + _squareSize)
    }

    private fun addBound(index: Int, left: Float, right: Float, top: Float, bottom: Float) {
        val bound = SquareBound(index, left, right, top, bottom)
        _bounds.add(bound)
    }

    private fun getClickEventSeatNumber(x: Float, y: Float): Int? {
        var index: Int? = null
        _bounds.forEach { bound ->
            val withinHeight = bound.top <= y && bound.bottom >= y
            val withinWidth = bound.left <= x && bound.right >= x
            if (withinHeight && withinWidth) {
                index = bound.index
                return@forEach
            }
        }
        return index
    }

    private fun onSeatClicked(seatNumber: Int) {
        if (seats[seatNumber]!!) {
            unSelectSeat(seatNumber)
        } else {
            selectSeat(seatNumber)
        }
    }

    private fun selectSeat(seatNumber: Int) {
        seats[seatNumber] = true
    }

    private fun unSelectSeat(seatNumber: Int) {
        seats[seatNumber] = false
    }

    private fun getSeatColor(seatNumber: Int): Int {
        if (!seats.keys.contains(seatNumber)) {
            return Color.GRAY
        }

        return if (seats[seatNumber]!!) {
            Color.GREEN
        } else {
            Color.GRAY
        }
    }

    private fun setSeatColor(seatNumber: Int) {
        paint.color = getSeatColor(seatNumber)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var totalWidth = widthSize
        if(heightMode == MeasureSpec.EXACTLY){
            totalWidth = (heightSize / 1.72).toInt()
        }

        setMeasuredDimension(totalWidth, height)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        init()
    }

    override fun dispatchDraw(canvas: Canvas?) {
        init()
        super.dispatchDraw(canvas)
    }

    override fun onDraw(canvas: Canvas?) {
        for (i in 0..11) {
            for (j in 0..7) {
                val seats = mutableListOf(1, 2, 4, 5).also {
                    if (i == 11)
                        it.addAll(2, listOf(3))
                }

                if (seats.contains(j)) {
                    val seatNumber = (4 * i + seats.indexOf(j) + 1)
                    initializeSeat(seatNumber)
                    canvas?.drawRect(
                        startX,
                        startY,
                        startX + _squareSize,
                        startY + _squareSize,
                        paint
                    )


                    canvas!!.drawText(
                        seatNumber.toString(),
                        startX + _squareSize / 2,
                        (startY + _squareSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2),
                        textPaint
                    )
                }
                startX += _squareSize + _space
            }
            startX = paddingLeft.toFloat()
            startY += _squareSize + _space
        }
    }
}
