package com.appointmed.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.model.RegisterRequest
import com.appointmed.mobile.network.ApiClient
import com.appointmed.mobile.util.NetworkUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var imageRegisterBack: ImageView
    private lateinit var buttonRolePatient: Button
    private lateinit var buttonRoleDoctor: Button
    private lateinit var buttonRegister: Button
    private lateinit var buttonGoLogin: Button
    private lateinit var inputFullName: EditText
    private lateinit var inputEmailRegister: EditText
    private lateinit var inputPhone: EditText
    private lateinit var inputAddress: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var inputBirthDate: EditText
    private lateinit var inputSpecialization: EditText
    private lateinit var inputLicenseNumber: EditText
    private lateinit var inputClinicAddress: EditText
    private lateinit var inputPasswordRegister: EditText
    private lateinit var inputConfirmPassword: EditText
    private lateinit var checkboxTerms: CheckBox
    private lateinit var patientFieldsContainer: LinearLayout
    private lateinit var doctorFieldsContainer: LinearLayout
    private lateinit var progressBarRegister: ProgressBar
    private lateinit var textRegisterError: TextView
    private lateinit var textRegisterMessage: TextView
    private var selectedRole = "PATIENT"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        imageRegisterBack = findViewById(R.id.imageRegisterBack)
        buttonRolePatient = findViewById(R.id.buttonRolePatient)
        buttonRoleDoctor = findViewById(R.id.buttonRoleDoctor)
        buttonRegister = findViewById(R.id.buttonRegister)
        buttonGoLogin = findViewById(R.id.buttonGoLogin)
        inputFullName = findViewById(R.id.inputFullName)
        inputEmailRegister = findViewById(R.id.inputEmailRegister)
        inputPhone = findViewById(R.id.inputPhone)
        inputAddress = findViewById(R.id.inputAddress)
        spinnerGender = findViewById<Spinner>(R.id.spinnerGender)
        inputBirthDate = findViewById(R.id.inputBirthDate)
        inputSpecialization = findViewById(R.id.inputSpecialization)
        inputLicenseNumber = findViewById(R.id.inputLicenseNumber)
        inputClinicAddress = findViewById(R.id.inputClinicAddress)
        inputPasswordRegister = findViewById(R.id.inputPasswordRegister)
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword)
        checkboxTerms = findViewById(R.id.checkboxTerms)
        patientFieldsContainer = findViewById(R.id.patientFieldsContainer)
        doctorFieldsContainer = findViewById(R.id.doctorFieldsContainer)
        progressBarRegister = findViewById(R.id.progressBarRegister)
        textRegisterError = findViewById(R.id.textRegisterError)
        textRegisterMessage = findViewById(R.id.textRegisterMessage)

        buttonRolePatient.setOnClickListener { setRole("PATIENT") }
        buttonRoleDoctor.setOnClickListener { setRole("DOCTOR") }
        buttonRegister.setOnClickListener { attemptRegister() }
        buttonGoLogin.setOnClickListener { finish() }
        imageRegisterBack.setOnClickListener { finish() }

        setRole(selectedRole)
    }

    private fun setRole(role: String) {
        selectedRole = role

        val tabs = listOf(
            buttonRolePatient to "PATIENT",
            buttonRoleDoctor to "DOCTOR"
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

        patientFieldsContainer.visibility = if (role == "PATIENT") View.VISIBLE else View.GONE
        doctorFieldsContainer.visibility = if (role == "DOCTOR") View.VISIBLE else View.GONE
    }

    private fun attemptRegister() {
        val fullName = inputFullName.text.toString().trim()
        val email = inputEmailRegister.text.toString().trim()
        val phone = inputPhone.text.toString().trim()
        val address = inputAddress.text.toString().trim()
        val gender = spinnerGender.selectedItem?.toString()?.takeIf { it != "Select" } ?: ""
        val birthDate = inputBirthDate.text.toString().trim()
        val specialization = inputSpecialization.text.toString().trim()
        val licenseNumber = inputLicenseNumber.text.toString().trim()
        val clinicAddress = inputClinicAddress.text.toString().trim()
        val password = inputPasswordRegister.text.toString().trim()
        val confirmPassword = inputConfirmPassword.text.toString().trim()

        textRegisterError.visibility = View.GONE
        textRegisterMessage.visibility = View.GONE

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in the required fields.")
            return
        }

        if (selectedRole == "PATIENT" && (gender.isEmpty() || birthDate.isEmpty())) {
            showError("Patient registration requires gender and birth date.")
            return
        }

        if (selectedRole == "DOCTOR" && (specialization.isEmpty() || licenseNumber.isEmpty() || clinicAddress.isEmpty())) {
            showError("Doctor registration requires specialization, license number, and clinic address.")
            return
        }

        if (password != confirmPassword) {
            showError("Passwords do not match.")
            return
        }

        if (!checkboxTerms.isChecked) {
            showError("You must accept the Terms and Privacy Policy.")
            return
        }

        if (!NetworkUtils.isOnline(this)) {
            showError("No internet connection. Please connect and try again.")
            return
        }

        setLoading(true)

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password,
            role = selectedRole,
            address = address.ifEmpty { null },
            gender = gender.ifEmpty { null },
            birthDate = birthDate.ifEmpty { null },
            specialization = specialization.ifEmpty { null },
            licenseNumber = licenseNumber.ifEmpty { null },
            phone = phone.ifEmpty { null },
            clinicAddress = clinicAddress.ifEmpty { null }
        )

        ApiClient.create(this).register(request).enqueue(object : Callback<com.appointmed.mobile.model.User> {
            override fun onResponse(call: Call<com.appointmed.mobile.model.User>, response: Response<com.appointmed.mobile.model.User>) {
                setLoading(false)
                if (response.isSuccessful) {
                    showMessage("Registration successful! Please sign in.")
                    inputFullName.setText("")
                    inputEmailRegister.setText("")
                    inputPasswordRegister.setText("")
                    inputConfirmPassword.setText("")
                    checkboxTerms.isChecked = false
                    buttonGoLogin.postDelayed({
                        finish()
                    }, 1500)
                } else {
                    showError(parseError(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<com.appointmed.mobile.model.User>, t: Throwable) {
                setLoading(false)
                showError("Unable to connect to the server. ${t.localizedMessage}")
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonRegister.isEnabled = !isLoading
        buttonGoLogin.isEnabled = !isLoading
        buttonRolePatient.isEnabled = !isLoading
        buttonRoleDoctor.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        textRegisterError.text = message
        textRegisterError.visibility = View.VISIBLE
    }

    private fun showMessage(message: String) {
        textRegisterMessage.text = message
        textRegisterMessage.visibility = View.VISIBLE
    }

    private fun parseError(body: ResponseBody?): String {
        return try {
            val json = body?.string() ?: "Server returned an error."
            val message = JSONObject(json).optString("message")
            if (message.isNotEmpty()) message else "Unable to complete registration."
        } catch (exception: Exception) {
            "Unable to complete registration."
        }
    }
}
