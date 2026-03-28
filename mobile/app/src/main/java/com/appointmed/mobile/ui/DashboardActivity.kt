package com.appointmed.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.util.Prefs

class DashboardActivity : AppCompatActivity() {
    private lateinit var bottomNavProfile: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNavProfile = findViewById(R.id.bottomNavProfile)

        val prefs = Prefs(this)
        if (!prefs.isLoggedIn()) {
            navigateToLogin()
            return
        }

        bottomNavProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val prefs = Prefs(this)
        if (!prefs.isLoggedIn()) {
            navigateToLogin()
            return
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
