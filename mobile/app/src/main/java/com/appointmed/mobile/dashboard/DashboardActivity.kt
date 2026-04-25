package com.appointmed.mobile.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.auth.LoginActivity
import com.appointmed.mobile.profile.ProfileActivity

class DashboardActivity : AppCompatActivity(), DashboardContract.View {
    private lateinit var bottomNavProfile: LinearLayout
    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        presenter = DashboardPresenter(this, this)

        bottomNavProfile = findViewById(R.id.bottomNavProfile)
        presenter.checkLoginState()

        bottomNavProfile.setOnClickListener {
            presenter.onProfileClicked()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.checkLoginState()
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
