package com.appointmed.mobile.booking

import java.util.Calendar

class BookAppointmentPresenter(
    private var view: BookAppointmentContract.View?,
    private val doctorName: String,
    private val doctorSpecialty: String
) : BookAppointmentContract.Presenter {

    private val calendar = Calendar.getInstance().apply {
        set(2023, Calendar.OCTOBER, 1)
    }
    private var selectedDay: Int? = null
    private var selectedSlot: String? = null

    private val timeSlots = listOf("09:00 AM", "10:30 AM", "11:00 AM", "01:30 PM", "02:45 PM", "04:00 PM")

    override fun loadInitialData() {
        updateCalendar()
        view?.showTimeSlots(timeSlots)
    }

    private fun updateCalendar() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun

        val monthNames = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        view?.showMonthLabel("${monthNames[month]} $year")
        view?.showCalendar(year, month, daysInMonth, firstDayOfWeek)
    }

    override fun onDateSelected(day: Int) {
        selectedDay = day
        view?.highlightSelectedDate(day)
    }

    override fun onSlotSelected(slot: String) {
        selectedSlot = slot
        view?.highlightSelectedSlot(slot)
    }

    override fun onPreviousMonth() {
        calendar.add(Calendar.MONTH, -1)
        selectedDay = null
        updateCalendar()
    }

    override fun onNextMonth() {
        calendar.add(Calendar.MONTH, 1)
        selectedDay = null
        updateCalendar()
    }

    override fun onContinueClicked(reason: String) {
        if (selectedDay == null) {
            view?.showError("Please select a date.")
            return
        }
        if (selectedSlot == null) {
            view?.showError("Please select a time slot.")
            return
        }

        val monthNames = arrayOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dateStr = "${monthNames[month]} $selectedDay, $year"

        view?.navigateToConfirmation(doctorName, doctorSpecialty, dateStr, selectedSlot!!)
    }

    override fun onCancelClicked() {
        view?.navigateBack()
    }

    override fun onDestroy() {
        view = null
    }
}
