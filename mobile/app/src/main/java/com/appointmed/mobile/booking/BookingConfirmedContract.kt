package com.appointmed.mobile.booking

interface BookingConfirmedContract {
    interface View {
        fun showBookingDetails(doctorName: String, specialty: String, date: String, time: String, bookingId: String)
        fun navigateToDashboard()
        fun openCalendar(doctorName: String, date: String, time: String)
    }

    interface Presenter {
        fun loadBookingDetails()
        fun onAddToCalendarClicked()
        fun onGoToDashboardClicked()
        fun onDestroy()
    }
}
