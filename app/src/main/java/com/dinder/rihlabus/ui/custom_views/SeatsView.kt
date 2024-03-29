package com.dinder.rihlabus.ui.custom_views // ktlint-disable experimental:package-name

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dinder.rihlabus.R
import com.dinder.rihlabus.common.Constants.NUMBER_OF_SEATS_ROWS
import com.dinder.rihlabus.data.model.SquareBound
import com.dinder.rihlabus.utils.NetworkUtils
import com.dinder.rihlabus.utils.SeatState
import com.dinder.rihlabus.utils.SeatUtils
import com.google.android.material.snackbar.Snackbar

enum class SeatViewCapability {
    SELECT_ONLY,
    BOOK_AND_CONFIRM
}

fun getCapabilityFromAttrs(capability: Int): SeatViewCapability {
    return when (capability) {
        0 -> SeatViewCapability.SELECT_ONLY
        else -> SeatViewCapability.BOOK_AND_CONFIRM
    }
}

class SeatsView : View {
    private var seats: MutableMap<String, SeatState> = SeatUtils.emptySeats.toMutableMap()

    private var paint: Paint = Paint()
    private var textPaint: Paint = Paint()
    private val _bounds: MutableList<SquareBound> = mutableListOf()
    private val _space = 20f
    private var onSeatSelectedListener: ((Int) -> Unit)? = null
    private var onShowPassengerDetails: ((Int) -> Unit)? = null
    private var onPaymentConfirmation: ((Int) -> Unit)? = null
    private var onBookSeat: ((Int) -> Unit)? = null
    private lateinit var capability: SeatViewCapability
    private var viewOnly: Boolean = false

    fun init(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SeatsView,
            0,
            0
        ).apply {
            try {
                capability =
                    getCapabilityFromAttrs(
                        getInteger(R.styleable.SeatsView_selectionCapability, 0)
                    )
            } finally {
                recycle()
            }
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    fun setIsViewOnly(isViewOnly: Boolean) {
        viewOnly = isViewOnly
    }

    fun setOnSeatSelectedListener(listener: (Int) -> Unit) {
        this.onSeatSelectedListener = listener
    }

    fun setOnShowPassengerDetailsListener(listener: (Int) -> Unit) {
        this.onShowPassengerDetails = listener
    }

    fun setOnPaymentConfirmationListener(listener: (Int) -> Unit) {
        this.onPaymentConfirmation = listener
    }

    fun setOnBookSeatListener(listener: (Int) -> Unit) {
        this.onBookSeat = listener
    }

    fun selectAll() {
        seats = seats.map {
            Pair(it.key, SeatState.SELECTED)
        }.toMap().toMutableMap()

        onSeatSelectedListener?.invoke(seats.values.filter { it == SeatState.SELECTED }.size)
        invalidate()
    }

    fun unselectAll() {
        seats = seats.map {
            Pair(it.key, SeatState.UN_SELECTED)
        }.toMap().toMutableMap()

        onSeatSelectedListener?.invoke(seats.values.filter { it == SeatState.SELECTED }.size)
        invalidate()
    }

    fun setSeats(seats: Map<String, SeatState>) {
        this.seats = seats.toMutableMap()
        invalidate()
    }

    fun getSeats() = seats

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!viewOnly) {
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
        }
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
        when (capability) {
            SeatViewCapability.SELECT_ONLY -> {
                if (seats["$seatNumber"] == SeatState.SELECTED) {
                    unSelectSeat(seatNumber)
                } else {
                    selectSeat(seatNumber)
                }
            }
            SeatViewCapability.BOOK_AND_CONFIRM -> {
                if (!NetworkUtils.isNetworkConnected(context)) {
                    Snackbar.make(
                        this,
                        resources.getString(R.string.no_network),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    when (seats["$seatNumber"]) {
                        SeatState.PAID -> onShowPassengerDetails?.invoke(seatNumber)
                        SeatState.PAYMENT_CONFIRMATION -> onPaymentConfirmation?.invoke(seatNumber)
                        SeatState.PRE_BOOK -> onShowPassengerDetails?.invoke(seatNumber)
                        SeatState.UNBOOKED -> onBookSeat?.invoke(seatNumber)
                        else -> Unit
                    }
                }
            }
        }

        this.onSeatSelectedListener?.invoke(seats.values.filter { it == SeatState.SELECTED }.size)
    }

    private fun selectSeat(seatNumber: Int) {
        seats["$seatNumber"] = SeatState.SELECTED
    }

    private fun unSelectSeat(seatNumber: Int) {
        seats["$seatNumber"] = SeatState.UN_SELECTED
    }

    private fun getSeatColor(seatNumber: Int): Int {
        when (capability) {
            SeatViewCapability.SELECT_ONLY -> {
                return if (seats["$seatNumber"] == SeatState.SELECTED) {
                    resources.getColor(R.color.green, context.theme)
                } else {
                    Color.GRAY
                }
            }
            SeatViewCapability.BOOK_AND_CONFIRM -> {
                return when (seats["$seatNumber"]) {
                    SeatState.PAID -> resources.getColor(R.color.green, context.theme)

                    SeatState.PAYMENT_CONFIRMATION -> resources.getColor(
                        R.color.orange,
                        context.theme
                    )

                    SeatState.PRE_BOOK -> resources.getColor(R.color.teal_200, context.theme)

                    SeatState.UNBOOKED -> Color.GRAY

                    else -> Color.LTGRAY
                }
            }
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
                    if (i == NUMBER_OF_SEATS_ROWS) {
                        it.addAll(2, listOf(3))
                    }
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
                        top,
                        right,
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
