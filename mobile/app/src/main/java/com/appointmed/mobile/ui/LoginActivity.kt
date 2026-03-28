package com.appointmed.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.model.LoginRequest
import com.appointmed.mobile.network.ApiClient
import com.appointmed.mobile.util.Prefs
import com.appointmed.mobile.model.User
import com.appointmed.mobile.util.NetworkUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
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
    private var selectedRole = "PATIENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

        buttonPatient.setOnClickListener { setRole("PATIENT") }
        buttonDoctor.setOnClickListener { setRole("DOCTOR") }
        buttonAdmin.setOnClickListener { setRole("ADMIN") }
        buttonLogin.setOnClickListener { attemptLogin() }
        buttonGoRegister.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }

        setRole(selectedRole)
    }

    private fun setRole(role: String) {
        selectedRole = role

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

    private fun attemptLogin() {
        textError.visibility = View.GONE
        textMessage.visibility = View.GONE

        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Email and password are required.")
            return
        }

        if (!NetworkUtils.isOnline(this)) {
            showError("No internet connection. Please check your network.")
            return
        }

        setLoading(true)

        val api = ApiClient.create(this)
        api.login(LoginRequest(email, password, selectedRole)).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    val token = user.id.toString()
                    Prefs(this@LoginActivity).apply {
                        saveToken(token)
                        saveUser(user)
                    }
                    showMessage("Login successful! Welcome ${user.name}")
                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showError(parseError(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setLoading(false)
                showError("Unable to connect to the server. ${t.localizedMessage}")
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonLogin.isEnabled = !isLoading
        buttonGoRegister.isEnabled = !isLoading
        buttonPatient.isEnabled = !isLoading
        buttonDoctor.isEnabled = !isLoading
        buttonAdmin.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        textError.text = message
        textError.visibility = View.VISIBLE
    }

    private fun showMessage(message: String) {
        textMessage.text = message
        textMessage.visibility = View.VISIBLE
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

