package com.appointmed.mobile.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.model.User
import com.appointmed.mobile.network.ApiClient
import com.appointmed.mobile.util.NetworkUtils
import com.appointmed.mobile.util.Prefs
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var imageProfileBack: ImageView
    private lateinit var imageProfileAvatar: ImageView
    private lateinit var avatarSelector: FrameLayout
    private lateinit var inputProfileName: EditText
    private lateinit var inputProfileEmail: EditText
    private lateinit var textProfilePatientId: TextView
    private lateinit var inputPhone: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputBirthDate: EditText
    private lateinit var spinnerBloodType: Spinner
    private lateinit var buttonSaveChanges: Button
    private lateinit var buttonDiscardChanges: Button
    private lateinit var buttonLogout: Button

    // Change password fields
    private lateinit var inputCurrentPassword: EditText
    private lateinit var inputNewPassword: EditText
    private lateinit var inputConfirmNewPassword: EditText
    private lateinit var buttonUpdatePassword: Button
    private lateinit var textPasswordError: TextView
    private lateinit var textPasswordSuccess: TextView
    private lateinit var progressBarPassword: ProgressBar

    private var selectedAvatarData: String? = null

    private val avatarPicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { processSelectedAvatar(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageProfileBack = findViewById(R.id.imageProfileBack)
        imageProfileAvatar = findViewById(R.id.imageProfileAvatar)
        avatarSelector = findViewById(R.id.avatarSelector)
        inputProfileName = findViewById(R.id.inputProfileName)
        inputProfileEmail = findViewById(R.id.inputProfileEmail)
        textProfilePatientId = findViewById(R.id.textProfilePatientId)
        inputPhone = findViewById(R.id.inputPhone)
        inputAddress = findViewById(R.id.inputAddress)
        inputBirthDate = findViewById(R.id.inputBirthDate)
        spinnerBloodType = findViewById(R.id.spinnerBloodType)
        buttonSaveChanges = findViewById(R.id.buttonSaveChanges)
        buttonDiscardChanges = findViewById(R.id.buttonDiscardChanges)
        buttonLogout = findViewById(R.id.buttonLogout)

        // Change password bindings
        inputCurrentPassword = findViewById(R.id.inputCurrentPassword)
        inputNewPassword = findViewById(R.id.inputNewPassword)
        inputConfirmNewPassword = findViewById(R.id.inputConfirmNewPassword)
        buttonUpdatePassword = findViewById(R.id.buttonUpdatePassword)
        textPasswordError = findViewById(R.id.textPasswordError)
        textPasswordSuccess = findViewById(R.id.textPasswordSuccess)
        progressBarPassword = findViewById(R.id.progressBarPassword)

        val prefs = Prefs(this)
        if (!prefs.isLoggedIn()) {
            navigateToLogin()
            return
        }

        populateFields()

        avatarSelector.setOnClickListener {
            avatarPicker.launch("image/*")
        }

        buttonSaveChanges.setOnClickListener {
            saveDetails()
        }

        buttonDiscardChanges.setOnClickListener {
            populateFields()
            Toast.makeText(this, "Changes discarded.", Toast.LENGTH_SHORT).show()
        }

        imageProfileBack.setOnClickListener { finish() }

        buttonLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        buttonUpdatePassword.setOnClickListener {
            attemptPasswordChange()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                Prefs(this).clear()
                Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun attemptPasswordChange() {
        textPasswordError.visibility = View.GONE
        textPasswordSuccess.visibility = View.GONE

        val currentPassword = inputCurrentPassword.text.toString().trim()
        val newPassword = inputNewPassword.text.toString().trim()
        val confirmPassword = inputConfirmNewPassword.text.toString().trim()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showPasswordError("All password fields are required.")
            return
        }

        if (newPassword.length < 4) {
            showPasswordError("New password must be at least 4 characters.")
            return
        }

        if (newPassword != confirmPassword) {
            showPasswordError("New passwords do not match.")
            return
        }

        val prefs = Prefs(this)
        val user = prefs.getUser()

        if (currentPassword != user.password) {
            showPasswordError("Current password is incorrect.")
            return
        }

        if (!NetworkUtils.isOnline(this)) {
            showPasswordError("No internet connection. Please try again later.")
            return
        }

        setPasswordLoading(true)

        val requestUser = user.copy(password = newPassword)
        ApiClient.create(this).updateUser(user.id, requestUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setPasswordLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    Prefs(this@ProfileActivity).saveUser(response.body()!!)
                    showPasswordSuccess("Password changed successfully!")
                    inputCurrentPassword.setText("")
                    inputNewPassword.setText("")
                    inputConfirmNewPassword.setText("")
                } else {
                    showPasswordError("Unable to update password. Please try again.")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setPasswordLoading(false)
                showPasswordError("Unable to connect to server. ${t.localizedMessage}")
            }
        })
    }

    private fun setPasswordLoading(isLoading: Boolean) {
        progressBarPassword.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonUpdatePassword.isEnabled = !isLoading
    }

    private fun showPasswordError(message: String) {
        textPasswordError.text = message
        textPasswordError.visibility = View.VISIBLE
    }

    private fun showPasswordSuccess(message: String) {
        textPasswordSuccess.text = message
        textPasswordSuccess.visibility = View.VISIBLE
    }

    private fun populateFields() {
        val prefs = Prefs(this)
        val user = prefs.getUser()

        inputProfileName.setText(user.name)
        inputProfileEmail.setText(user.email)
        textProfilePatientId.text = getString(R.string.profile_patient_id, user.id)
        inputPhone.setText(prefs.getPatientPhone() ?: "")
        inputAddress.setText(prefs.getPatientAddress() ?: "")
        inputBirthDate.setText(prefs.getPatientBirthDate() ?: "")

        val savedBloodType = prefs.getPatientBloodType() ?: ""
        val bloodTypes = resources.getStringArray(R.array.blood_type_options)
        val selectedIndex = bloodTypes.indexOf(savedBloodType).takeIf { it >= 0 } ?: 0
        spinnerBloodType.setSelection(selectedIndex)

        if (user.avatarData?.isNotEmpty() == true) {
            try {
                val avatarBytes = Base64.decode(user.avatarData, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.size)
                imageProfileAvatar.setImageBitmap(bitmap)
            } catch (exception: Exception) {
                imageProfileAvatar.setImageResource(android.R.drawable.ic_menu_myplaces)
            }
        } else {
            imageProfileAvatar.setImageResource(android.R.drawable.ic_menu_myplaces)
        }
    }

    private fun saveDetails() {
        val name = inputProfileName.text.toString().trim()
        val email = inputProfileEmail.text.toString().trim()
        val phone = inputPhone.text.toString().trim()
        val address = inputAddress.text.toString().trim()
        val birthDate = inputBirthDate.text.toString().trim()
        val bloodType = spinnerBloodType.selectedItem.toString().takeIf { it != "Select" } ?: ""

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and email are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.isEmpty() || address.isEmpty() || birthDate.isEmpty() || bloodType.isEmpty()) {
            Toast.makeText(this, "Please fill in all personal details.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!NetworkUtils.isOnline(this)) {
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show()
            return
        }

        setProfileLoading(true)

        val prefs = Prefs(this)
        val user = prefs.getUser()
        val requestUser = user.copy(
            name = name,
            email = email,
            avatarData = selectedAvatarData ?: user.avatarData,
            avatarUrl = null
        )

        ApiClient.create(this).updateUser(user.id, requestUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setProfileLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    val updatedUser = response.body()!!
                    Prefs(this@ProfileActivity).saveUser(updatedUser)
                    Prefs(this@ProfileActivity).savePatientDetails(phone, address, birthDate, bloodType)
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                    populateFields()
                } else {
                    Toast.makeText(this@ProfileActivity, "Unable to update profile. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setProfileLoading(false)
                Toast.makeText(this@ProfileActivity, "Unable to update profile. ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setProfileLoading(isLoading: Boolean) {
        buttonSaveChanges.isEnabled = !isLoading
        buttonDiscardChanges.isEnabled = !isLoading
    }

    override fun onResume() {
        super.onResume()
        val prefs = Prefs(this)
        if (!prefs.isLoggedIn()) {
            navigateToLogin()
            return
        }
        populateFields()
    }

    private fun processSelectedAvatar(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                selectedAvatarData = Base64.encodeToString(bytes, Base64.DEFAULT)
                imageProfileAvatar.setImageURI(uri)
            }
        } catch (exception: Exception) {
            Toast.makeText(this, "Unable to load selected image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
