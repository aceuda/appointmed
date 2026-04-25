package com.appointmed.mobile.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.dashboard.DashboardActivity
import com.appointmed.mobile.data.model.User

class LoginActivity : AppCompatActivity(), LoginContract.View {
    private lateinit var buttonPatient: Button
    private lateinit var buttonDoctor: Button
    private lateinit var buttonAdmin: Button
    private lateinit var buttonLogin: Button
    private lateinit var buttonGoRegister: Button
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var textError: TextView
    private lateinit var textMessage: TextView

    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter = LoginPresenter(this, this)

        buttonPatient = findViewById(R.id.buttonPatient)
        buttonDoctor = findViewById(R.id.buttonDoctor)
        buttonAdmin = findViewById(R.id.buttonAdmin)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonGoRegister = findViewById(R.id.buttonGoRegister)
        inputEmail = findViewById(R.id.inputEmail)
        inputPassword = findViewById(R.id.inputPassword)
        progressBar = findViewById(R.id.progressBar)
        textError = findViewById(R.id.textError)
        textMessage = findViewById(R.id.textMessage)

        buttonPatient.setOnClickListener { presenter.onRoleSelected("PATIENT") }
        buttonDoctor.setOnClickListener { presenter.onRoleSelected("DOCTOR") }
        buttonAdmin.setOnClickListener { presenter.onRoleSelected("ADMIN") }
        buttonLogin.setOnClickListener {
            presenter.onLoginClicked(
                inputEmail.text.toString().trim(),
                inputPassword.text.toString().trim()
            )
        }
        buttonGoRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        presenter.onRoleSelected("PATIENT")
    }

    override fun setRole(role: String) {
        val tabs = listOf(
            buttonPatient to "PATIENT",
            buttonDoctor to "DOCTOR",
            buttonAdmin to "ADMIN"
        )

        for ((button, tabRole) in tabs) {
            if (role == tabRole) {
                button.setBackgroundResource(R.drawable.bg_tab_active)
                button.setTextColor(getColor(R.color.textPrimary))
                button.elevation = 2f
            } else {
                button.setBackgroundResource(R.drawable.bg_tab_inactive)
                button.setTextColor(getColor(R.color.textSecondary))
                button.elevation = 0f
            }
        }
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        buttonLogin.isEnabled = false
        buttonGoRegister.isEnabled = false
        buttonPatient.isEnabled = false
        buttonDoctor.isEnabled = false
        buttonAdmin.isEnabled = false
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        buttonLogin.isEnabled = true
        buttonGoRegister.isEnabled = true
        buttonPatient.isEnabled = true
        buttonDoctor.isEnabled = true
        buttonAdmin.isEnabled = true
    }

    override fun showError(message: String) {
        textError.text = message
        textError.visibility = View.VISIBLE
        textMessage.visibility = View.GONE
    }

    override fun showMessage(message: String) {
        textMessage.text = message
        textMessage.visibility = View.VISIBLE
        textError.visibility = View.GONE
    }

    override fun navigateToDashboard(user: User) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
