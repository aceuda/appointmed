package com.appointmed.mobile.booking

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R

class BookAppointmentActivity : AppCompatActivity(), BookAppointmentContract.View {

    private lateinit var presenter: BookAppointmentContract.Presenter
    private lateinit var calendarGrid: GridLayout
    private lateinit var timeSlotsContainer: LinearLayout
    private lateinit var monthLabel: TextView
    private lateinit var inputReason: EditText

    private var doctorName = ""
    private var doctorSpecialty = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_appointment)

        doctorName = intent.getStringExtra("doctor_name") ?: "Dr. Sarah Smith"
        doctorSpecialty = intent.getStringExtra("doctor_specialty") ?: "General Physician"

        presenter = BookAppointmentPresenter(this, doctorName, doctorSpecialty)

        calendarGrid = findViewById(R.id.calendarGrid)
        monthLabel = findViewById(R.id.textCurrentMonth)
        inputReason = findViewById(R.id.inputReason)

        // We'll use a LinearLayout wrapped in a FlexboxLayout container
        // Since FlexboxLayout requires the library, we'll use a simpler approach
        timeSlotsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Replace the FlexboxLayout with our container
        val parent = findViewById<android.view.ViewGroup>(R.id.timeSlotsContainer)
        if (parent is android.view.ViewGroup) {
            val parentOfSlots = parent.parent as? android.view.ViewGroup
            val index = parentOfSlots?.indexOfChild(parent) ?: -1
            if (index >= 0 && parentOfSlots != null) {
                parentOfSlots.removeView(parent)
                parentOfSlots.addView(timeSlotsContainer, index, parent.layoutParams)
            }
        }

        // Navigation
        findViewById<ImageView>(R.id.btnBackBooking).setOnClickListener { presenter.onCancelClicked() }
        findViewById<Button>(R.id.btnCancelBooking).setOnClickListener { presenter.onCancelClicked() }
        findViewById<Button>(R.id.btnContinuePayment).setOnClickListener {
            presenter.onContinueClicked(inputReason.text.toString())
        }

        // Calendar navigation
        findViewById<ImageView>(R.id.btnPrevMonth).setOnClickListener { presenter.onPreviousMonth() }
        findViewById<ImageView>(R.id.btnNextMonth).setOnClickListener { presenter.onNextMonth() }

        presenter.loadInitialData()
    }

    override fun showMonthLabel(label: String) {
        monthLabel.text = label
    }

    override fun showCalendar(year: Int, month: Int, daysInMonth: Int, firstDayOfWeek: Int) {
        calendarGrid.removeAllViews()
        val cellSize = resources.displayMetrics.widthPixels / 7 - 24

        // Empty cells before first day
        for (i in 0 until firstDayOfWeek) {
            val empty = TextView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = cellSize; height = cellSize
                }
            }
            calendarGrid.addView(empty)
        }

        // Day cells
        for (day in 1..daysInMonth) {
            val tv = TextView(this).apply {
                text = "$day"
                textSize = 14f
                setTextColor(Color.parseColor("#334155"))
                gravity = android.view.Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = cellSize; height = cellSize
                }
                setOnClickListener { presenter.onDateSelected(day) }
            }
            calendarGrid.addView(tv)
        }
    }

    override fun highlightSelectedDate(day: Int) {
        for (i in 0 until calendarGrid.childCount) {
            val child = calendarGrid.getChildAt(i) as? TextView ?: continue
            if (child.text.toString() == "$day") {
                child.setBackgroundResource(R.drawable.bg_cal_selected)
                child.setTextColor(Color.WHITE)
            } else {
                child.background = null
                child.setTextColor(Color.parseColor("#334155"))
            }
        }
    }

    override fun showTimeSlots(slots: List<String>) {
        timeSlotsContainer.removeAllViews()

        // Create rows of 3 slots each
        var currentRow: LinearLayout? = null
        slots.forEachIndexed { index, slot ->
            if (index % 3 == 0) {
                currentRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { bottomMargin = 8 }
                }
                timeSlotsContainer.addView(currentRow)
            }

            val btn = Button(this).apply {
                text = slot
                textSize = 12f
                isAllCaps = false
                setBackgroundResource(R.drawable.bg_slot_unselected)
                setTextColor(Color.parseColor("#334155"))
                layoutParams = LinearLayout.LayoutParams(0, 44.dpToPx(), 1f).apply {
                    marginEnd = if ((index % 3) < 2) 8.dpToPx() else 0
                }
                setPadding(8.dpToPx(), 0, 8.dpToPx(), 0)
                setOnClickListener { presenter.onSlotSelected(slot) }
                tag = slot
            }
            currentRow?.addView(btn)
        }
    }

    override fun highlightSelectedSlot(slot: String) {
        for (i in 0 until timeSlotsContainer.childCount) {
            val row = timeSlotsContainer.getChildAt(i) as? LinearLayout ?: continue
            for (j in 0 until row.childCount) {
                val btn = row.getChildAt(j) as? Button ?: continue
                if (btn.tag == slot) {
                    btn.setBackgroundResource(R.drawable.bg_slot_selected)
                    btn.setTextColor(Color.WHITE)
                } else {
                    btn.setBackgroundResource(R.drawable.bg_slot_unselected)
                    btn.setTextColor(Color.parseColor("#334155"))
                }
            }
        }
    }

    override fun navigateToConfirmation(doctorName: String, specialty: String, date: String, time: String) {
        val intent = Intent(this, BookingConfirmedActivity::class.java).apply {
            putExtra("doctor_name", doctorName)
            putExtra("doctor_specialty", specialty)
            putExtra("date", date)
            putExtra("time", time)
        }
        startActivity(intent)
        finish()
    }

    override fun navigateBack() {
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
