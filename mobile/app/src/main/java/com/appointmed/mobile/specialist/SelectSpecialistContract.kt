package com.appointmed.mobile.specialist

import com.appointmed.mobile.data.model.User

interface SelectSpecialistContract {
    interface View {
        fun showDoctors(doctors: List<DoctorItem>)
        fun showFilteredDoctors(doctors: List<DoctorItem>, count: Int)
        fun navigateToBooking(doctor: DoctorItem)
        fun navigateToDashboard()
        fun navigateToProfile()
        fun showError(message: String)
    }

    interface Presenter {
        fun loadDoctors()
        fun filterBySpecialty(specialty: String)
        fun searchDoctors(query: String)
        fun onBookClicked(doctor: DoctorItem)
        fun onHomeClicked()
        fun onProfileClicked()
        fun onDestroy()
    }
}

data class DoctorItem(
    val id: Int,
    val name: String,
    val specialty: String,
    val clinic: String,
    val fee: Int,
    val rating: Double,
    val available: Boolean
)
