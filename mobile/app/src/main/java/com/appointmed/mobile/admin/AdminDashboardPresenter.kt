package com.appointmed.mobile.admin

class AdminDashboardPresenter(
    private var view: AdminDashboardContract.View?
) : AdminDashboardContract.Presenter {

    private val allUsers = listOf(
        AdminUserItem(1, "Dr. Sarah Smith", "Doctor", "Cardiologist • MD", "sarah.smith@med.com", "ACTIVE"),
        AdminUserItem(2, "John Doe", "Patient", "Patient • ID: #8291", "j.doe@example.com", "PENDING"),
        AdminUserItem(3, "Dr. Michael Vance", "Doctor", "Neurologist • PhD", "m.vance@med.com", "ACTIVE"),
        AdminUserItem(4, "Jane Roe", "Patient", "Patient • ID: #9021", "jane.roe@mail.com", "OFFLINE"),
        AdminUserItem(5, "Dr. Ana Cruz", "Doctor", "Pediatrician • MD", "a.cruz@med.com", "ACTIVE"),
        AdminUserItem(6, "Mark Lee", "Patient", "Patient • ID: #7845", "m.lee@mail.com", "ACTIVE")
    )

    override fun loadData() {
        val doctorCount = allUsers.count { it.role == "Doctor" }
        view?.showStats(doctorCount, 48)
        view?.showUsers(allUsers)
    }

    override fun filterByTab(tab: String) {
        val filtered = when (tab) {
            "Doctors" -> allUsers.filter { it.role == "Doctor" }
            "Patients" -> allUsers.filter { it.role == "Patient" }
            else -> allUsers
        }
        view?.showFilteredUsers(filtered)
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }

    override fun onDestroy() {
        view = null
    }
}
