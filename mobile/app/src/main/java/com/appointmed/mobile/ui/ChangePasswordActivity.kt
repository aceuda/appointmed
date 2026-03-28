package com.appointmed.mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.model.User
import com.appointmed.mobile.network.ApiClient
import com.appointmed.mobile.util.NetworkUtils
import com.appointmed.mobile.util.Prefs
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var inputCurrentPassword: EditText
    private lateinit var inputNewPassword: EditText
    private lateinit var inputConfirmPasswordChange: EditText
    private lateinit var buttonChangePasswordSubmit: Button
    private lateinit var progressBarPassword: ProgressBar
    private lateinit var textPasswordError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        inputCurrentPassword = findViewById(R.id.inputCurrentPassword)
        inputNewPassword = findViewById(R.id.inputNewPassword)
        inputConfirmPasswordChange = findViewById(R.id.inputConfirmPasswordChange)
        buttonChangePasswordSubmit = findViewById(R.id.buttonChangePasswordSubmit)
        progressBarPassword = findViewById(R.id.progressBarPassword)
        textPasswordError = findViewById(R.id.textPasswordError)

        val prefs = Prefs(this)
        if (!prefs.isLoggedIn()) {
            finish()
            return
        }

        buttonChangePasswordSubmit.setOnClickListener {
            val current = inputCurrentPassword.text.toString().trim()
            val newPassword = inputNewPassword.text.toString().trim()
            val confirmPassword = inputConfirmPasswordChange.text.toString().trim()

            if (current.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("All password fields are required.")
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                showError("New passwords do not match.")
                return@setOnClickListener
            }

            if (!NetworkUtils.isOnline(this)) {
                showError("No internet connection. Please try again later.")
                return@setOnClickListener
            }

            val user = prefs.getUser()
            if (current != user.password) {
                showError("Current password is incorrect.")
                return@setOnClickListener
            }

            changePassword(user, newPassword)
        }
    }

    private fun changePassword(user: User, newPassword: String) {
        setLoading(true)
        val requestUser = user.copy(password = newPassword)
        ApiClient.create(this).updateUser(user.id, requestUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    Prefs(this@ChangePasswordActivity).saveUser(response.body()!!)
                    Toast.makeText(this@ChangePasswordActivity, "Password changed successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError(parseError(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setLoading(false)
                showError("Unable to change password. ${t.localizedMessage}")
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        progressBarPassword.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonChangePasswordSubmit.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        textPasswordError.text = message
        textPasswordError.visibility = View.VISIBLE
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
