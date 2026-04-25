package com.appointmed.mobile.profile

import android.content.Context
import com.appointmed.mobile.data.local.Prefs
import com.appointmed.mobile.data.model.User
import com.appointmed.mobile.data.network.ApiClient
import com.appointmed.mobile.util.NetworkUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordPresenter(
    private var view: ChangePasswordContract.View?,
    private val context: Context
) : ChangePasswordContract.Presenter {

    override fun onChangePasswordClicked(currentPassword: String, newPassword: String, confirmPassword: String) {
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            view?.showError("All password fields are required.")
            return
        }

        if (newPassword != confirmPassword) {
            view?.showError("New passwords do not match.")
            return
        }

        if (!NetworkUtils.isOnline(context)) {
            view?.showError("No internet connection. Please try again later.")
            return
        }

        val prefs = Prefs(context)
        val user = prefs.getUser()
        if (currentPassword != user.password) {
            view?.showError("Current password is incorrect.")
            return
        }

        view?.showLoading()

        val requestUser = user.copy(password = newPassword)
        ApiClient.create(context).updateUser(user.id, requestUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                view?.hideLoading()
                if (response.isSuccessful && response.body() != null) {
                    Prefs(context).saveUser(response.body()!!)
                    view?.onPasswordChanged()
                } else {
                    view?.showError(parseError(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Unable to change password. ${t.localizedMessage}")
            }
        })
    }

    override fun onDestroy() {
        view = null
    }

    private fun parseError(body: ResponseBody?): String {
        return try {
            val json = body?.string() ?: "Server returned an error."
            val message = JSONObject(json).optString("message")
            if (message.isNotEmpty()) message else "Unable to update password."
        } catch (exception: Exception) {
            "Unable to update password."
        }
    }
}
