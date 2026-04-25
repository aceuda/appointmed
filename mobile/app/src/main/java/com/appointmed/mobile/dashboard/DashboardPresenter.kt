package com.appointmed.mobile.dashboard

import android.content.Context
import com.appointmed.mobile.data.local.Prefs

class DashboardPresenter(
    private var view: DashboardContract.View?,
    private val context: Context
) : DashboardContract.Presenter {

    override fun checkLoginState() {
        val prefs = Prefs(context)
        if (!prefs.isLoggedIn()) {
            view?.navigateToLogin()
        }
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }

    override fun onDestroy() {
        view = null
    }
}
