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
    private var seats: MutableMap<Int, Boolean> = (1..NUMBER_OF_SEATS).map {
        Pair(it, false)
    }.toMap().toMutableMap()

    private var paint: Paint = Paint()
    private var textPaint: Paint = Paint()
    private val _bounds: MutableList<SquareBound> = mutableListOf()
    private val _space = 20f
    private lateinit var listener: (Int) -> Unit

    companion object {
        const val NUMBER_OF_SEATS_ROWS = 11
        const val NUMBER_OF_SEATS = 49
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setOnSeatSelectedListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    fun selectAll() {
        seats = seats.map {
            Pair(it.key, true)
        }.toMap().toMutableMap()

        listener(seats.filter { it.value }.size)
        invalidate()
    }

    fun unselectAll() {
        seats = seats.map {
            Pair(it.key, false)
        }.toMap().toMutableMap()

        listener(seats.filter { it.value }.size)
        invalidate()
    }

    fun setSeats(seats: Map<Int, Boolean>) {
        this.seats = seats.toMutableMap()
    }

    fun getSeats() = seats

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

    private fun initializeSeat(
        seatNumber: Int,
        left: Float,
        right: Float,
        top: Float,
        bottom: Float
    ) {
        setSeatColor(seatNumber)
        addBound(seatNumber, left, right, top, bottom)
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
        this.listener(seats.values.filter { it }.size)
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

    override fun onDraw(canvas: Canvas?) {
        val availableWidth = width - paddingRight - paddingLeft
        val _squareSize = (availableWidth - (_space * (7 - 1))) / 7
        val _textSize = 0.34f * _squareSize
        var startX = paddingLeft.toFloat()
        var startY = (height - 12 * _squareSize - NUMBER_OF_SEATS_ROWS * _space) / 2

        paint.color = Color.GRAY
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = Color.WHITE
        textPaint.textSize = _textSize

        for (i in 0..NUMBER_OF_SEATS_ROWS) {
            for (j in 0..7) {
                val seats = mutableListOf(1, 2, 4, 5).also {
                    if (i == NUMBER_OF_SEATS_ROWS)
                        it.addAll(2, listOf(3))
                }

                if (seats.contains(j)) {
                    val seatNumber = (4 * i + seats.indexOf(j) + 1)
                    val left = startX
                    val right = startX + _squareSize
                    val top = startY
                    val bottom = startY + _squareSize

                    initializeSeat(seatNumber, left, right, top, bottom)
                    canvas?.drawRect(
                        left,
                        top, right,
                        bottom,
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
