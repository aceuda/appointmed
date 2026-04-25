package com.appointmed.mobile.specialist

class SelectSpecialistPresenter(
    private var view: SelectSpecialistContract.View?
) : SelectSpecialistContract.Presenter {

    private val allDoctors = listOf(
        DoctorItem(1, "Dr. Marcus Thorne", "Cardiologist", "St. Luke's Medical Center", 1200, 4.9, true),
        DoctorItem(2, "Dr. Elena Rodriguez", "Dermatologist", "Makati Medical Center", 1500, 4.8, true),
        DoctorItem(3, "Dr. Julian Sison", "Pediatrician", "Children's Hospital Manila", 900, 5.0, true),
        DoctorItem(4, "Dr. Sarah Lim", "General Physician", "The Medical City", 800, 4.7, false),
        DoctorItem(5, "Dr. Michael Chen", "Cardiologist", "Asian Hospital", 1400, 4.6, true),
        DoctorItem(6, "Dr. Ana Reyes", "Dermatologist", "Manila Doctors Hospital", 1300, 4.9, true),
        DoctorItem(7, "Dr. Robert Cruz", "Neurologist", "St. Luke's BGC", 1800, 4.8, true),
        DoctorItem(8, "Dr. Patricia Santos", "Pediatrician", "Philippine General Hospital", 700, 4.5, true)
    )

    private var currentFilter = "All"
    private var currentQuery = ""

    override fun loadDoctors() {
        view?.showDoctors(allDoctors)
        view?.showFilteredDoctors(allDoctors, allDoctors.size)
    }

    override fun filterBySpecialty(specialty: String) {
        currentFilter = specialty
        applyFilters()
    }

    override fun searchDoctors(query: String) {
        currentQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = allDoctors
        if (currentFilter != "All") {
            filtered = filtered.filter { it.specialty.contains(currentFilter, ignoreCase = true) }
        }
        if (currentQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(currentQuery, ignoreCase = true) ||
                it.specialty.contains(currentQuery, ignoreCase = true)
            }
        }
        view?.showFilteredDoctors(filtered, filtered.size)
    }

    override fun onBookClicked(doctor: DoctorItem) {
        if (doctor.available) {
            view?.navigateToBooking(doctor)
        } else {
            view?.showError("This doctor is fully booked. You can join the waitlist.")
        }
    }

    override fun onHomeClicked() {
        view?.navigateToDashboard()
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }

    override fun onDestroy() {
        view = null
    }
}
