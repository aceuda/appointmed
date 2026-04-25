package com.appointmed.mobile.auth

import com.appointmed.mobile.data.model.User

interface LoginContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showMessage(message: String)
        fun navigateToDashboard(user: User)
        fun setRole(role: String)
    }

    interface Presenter {
        fun onLoginClicked(email: String, password: String)
        fun onRoleSelected(role: String)
        fun onDestroy()
    }
}
