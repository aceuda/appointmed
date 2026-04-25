package com.appointmed.mobile.booking

class BookingConfirmedPresenter(
    private var view: BookingConfirmedContract.View?,
    private val doctorName: String,
    private val specialty: String,
    private val date: String,
    private val time: String
) : BookingConfirmedContract.Presenter {

    private val bookingId = "AM-${(10000..99999).random()}"

    override fun loadBookingDetails() {
        view?.showBookingDetails(doctorName, specialty, date, time, bookingId)
    }

    override fun onAddToCalendarClicked() {
        view?.openCalendar(doctorName, date, time)
    }

    override fun onGoToDashboardClicked() {
        view?.navigateToDashboard()
    }

    override fun onDestroy() {
        view = null
    }
}
