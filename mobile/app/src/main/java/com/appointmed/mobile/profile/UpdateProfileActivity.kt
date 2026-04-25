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
import com.appointmed.mobile.data.model.User
import com.appointmed.mobile.data.network.ApiClient
import com.appointmed.mobile.util.NetworkUtils
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var inputUpdateName: EditText
    private lateinit var inputUpdateEmail: EditText
    private lateinit var inputUpdateAvatar: EditText
    private lateinit var buttonSaveProfile: Button
    private lateinit var progressBarUpdate: ProgressBar
    private lateinit var textUpdateError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        inputUpdateName = findViewById(R.id.inputUpdateName)
        inputUpdateEmail = findViewById(R.id.inputUpdateEmail)
        inputUpdateAvatar = findViewById(R.id.inputUpdateAvatar)
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile)
        progressBarUpdate = findViewById(R.id.progressBarUpdate)
        textUpdateError = findViewById(R.id.textUpdateError)

        val prefs = Prefs(this)
        if (!prefs.isLoggedIn()) {
            finish()
            return
        }

        val user = prefs.getUser()
        inputUpdateName.setText(user.name)
        inputUpdateEmail.setText(user.email)
        inputUpdateAvatar.setText(user.avatarUrl ?: "")

        buttonSaveProfile.setOnClickListener { submitUpdate(user) }
    }

    private fun submitUpdate(user: User) {
        textUpdateError.visibility = View.GONE

        val name = inputUpdateName.text.toString().trim()
        val email = inputUpdateEmail.text.toString().trim()
        val avatar = inputUpdateAvatar.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            showError("Name and email cannot be empty.")
            return
        }

        if (!NetworkUtils.isOnline(this)) {
            showError("No internet connection. Please try again later.")
            return
        }

        setLoading(true)
        val requestUser = user.copy(name = name, email = email, avatarUrl = avatar.ifEmpty { null })

        ApiClient.create(this).updateUser(user.id, requestUser).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                setLoading(false)
                if (response.isSuccessful && response.body() != null) {
                    Prefs(this@UpdateProfileActivity).saveUser(response.body()!!)
                    Toast.makeText(this@UpdateProfileActivity, "Profile updated successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showError(parseError(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                setLoading(false)
                showError("Unable to update profile. ${t.localizedMessage}")
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        progressBarUpdate.visibility = if (isLoading) View.VISIBLE else View.GONE
        buttonSaveProfile.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        textUpdateError.text = message
        textUpdateError.visibility = View.VISIBLE
    }

    private fun parseError(body: ResponseBody?): String {
        return try {
            val json = body?.string() ?: "Server returned an error."
            val message = JSONObject(json).optString("message")
            if (message.isNotEmpty()) message else "Unable to update profile."
        } catch (exception: Exception) {
            "Unable to update profile."
        }
    }
}
