package com.appointmed.mobile.dashboard

interface DashboardContract {
    interface View {
        fun navigateToProfile()
        fun navigateToLogin()
    }

    interface Presenter {
        fun checkLoginState()
        fun onProfileClicked()
        fun onDestroy()
    }
}
