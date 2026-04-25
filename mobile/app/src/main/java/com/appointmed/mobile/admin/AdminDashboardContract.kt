package com.appointmed.mobile.admin

interface AdminDashboardContract {
    interface View {
        fun showUsers(users: List<AdminUserItem>)
        fun showFilteredUsers(users: List<AdminUserItem>)
        fun showStats(totalDoctors: Int, pendingApps: Int)
        fun navigateToProfile()
        fun showError(message: String)
    }

    interface Presenter {
        fun loadData()
        fun filterByTab(tab: String)
        fun onProfileClicked()
        fun onDestroy()
    }
}

data class AdminUserItem(
    val id: Int,
    val name: String,
    val role: String,
    val detail: String,
    val email: String,
    val status: String
)
