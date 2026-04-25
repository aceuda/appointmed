package com.appointmed.mobile.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.data.local.Prefs

class ChangePasswordActivity : AppCompatActivity(), ChangePasswordContract.View {
    private lateinit var inputCurrentPassword: EditText
    private lateinit var inputNewPassword: EditText
    private lateinit var inputConfirmPasswordChange: EditText
    private lateinit var buttonChangePasswordSubmit: Button
    private lateinit var progressBarPassword: ProgressBar
    private lateinit var textPasswordError: TextView

    private lateinit var presenter: ChangePasswordContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        presenter = ChangePasswordPresenter(this, this)

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
            presenter.onChangePasswordClicked(
                currentPassword = inputCurrentPassword.text.toString().trim(),
                newPassword = inputNewPassword.text.toString().trim(),
                confirmPassword = inputConfirmPasswordChange.text.toString().trim()
            )
        }
    }

    override fun showLoading() {
        progressBarPassword.visibility = View.VISIBLE
        buttonChangePasswordSubmit.isEnabled = false
    }

    override fun hideLoading() {
        progressBarPassword.visibility = View.GONE
        buttonChangePasswordSubmit.isEnabled = true
    }

    override fun showError(message: String) {
        textPasswordError.text = message
        textPasswordError.visibility = View.VISIBLE
    }

    override fun onPasswordChanged() {
        Toast.makeText(this, "Password changed successfully.", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
