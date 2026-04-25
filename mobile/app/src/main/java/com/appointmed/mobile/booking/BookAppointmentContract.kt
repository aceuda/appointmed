package com.appointmed.mobile.booking

interface BookAppointmentContract {
    interface View {
        fun showCalendar(year: Int, month: Int, daysInMonth: Int, firstDayOfWeek: Int)
        fun highlightSelectedDate(day: Int)
        fun showTimeSlots(slots: List<String>)
        fun highlightSelectedSlot(slot: String)
        fun showMonthLabel(label: String)
        fun navigateToConfirmation(doctorName: String, specialty: String, date: String, time: String)
        fun navigateBack()
        fun showError(message: String)
    }

    interface Presenter {
        fun loadInitialData()
        fun onDateSelected(day: Int)
        fun onSlotSelected(slot: String)
        fun onPreviousMonth()
        fun onNextMonth()
        fun onContinueClicked(reason: String)
        fun onCancelClicked()
        fun onDestroy()
    }
}
