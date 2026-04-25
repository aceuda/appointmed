package com.appointmed.mobile.profile

interface ChangePasswordContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onPasswordChanged()
    }

    interface Presenter {
        fun onChangePasswordClicked(currentPassword: String, newPassword: String, confirmPassword: String)
        fun onDestroy()
    }
}
