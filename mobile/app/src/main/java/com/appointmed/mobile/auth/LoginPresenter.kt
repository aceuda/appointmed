package com.appointmed.mobile.auth

import android.content.Context
import com.appointmed.mobile.data.local.Prefs
import com.appointmed.mobile.data.model.LoginRequest
import com.appointmed.mobile.data.model.User
import com.appointmed.mobile.data.network.ApiClient
import com.appointmed.mobile.util.NetworkUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(
    private var view: LoginContract.View?,
    private val context: Context
) : LoginContract.Presenter {

    private var selectedRole = "PATIENT"

    override fun onRoleSelected(role: String) {
        selectedRole = role
        view?.setRole(role)
    }

    override fun onLoginClicked(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view?.showError("Email and password are required.")
            return
        }

        if (!NetworkUtils.isOnline(context)) {
            view?.showError("No internet connection. Please check your network.")
            return
        }

        view?.showLoading()

        val api = ApiClient.create(context)
        api.login(LoginRequest(email, password, selectedRole)).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                view?.hideLoading()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val token = user.id.toString()
                    Prefs(context).apply {
                        saveToken(token)
                        saveUser(user)
                    }
                    view?.showMessage("Login successful! Welcome ${user.name}")
                    view?.navigateToDashboard(user)
                } else {
                    view?.showError(parseError(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                view?.hideLoading()
                view?.showError("Unable to connect to the server. ${t.localizedMessage}")
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
            if (message.isNotEmpty()) message else "Invalid credentials or server error."
        } catch (exception: Exception) {
            "Invalid credentials or server error."
        }
    }
}
