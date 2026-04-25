package com.appointmed.mobile.booking

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appointmed.mobile.R
import com.appointmed.mobile.dashboard.DashboardActivity

class BookingConfirmedActivity : AppCompatActivity(), BookingConfirmedContract.View {

    private lateinit var presenter: BookingConfirmedContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_confirmed)

        val doctorName = intent.getStringExtra("doctor_name") ?: "Dr. Sarah Smith"
        val specialty = intent.getStringExtra("doctor_specialty") ?: "General Cardiology"
        val date = intent.getStringExtra("date") ?: "October 5, 2023"
        val time = intent.getStringExtra("time") ?: "10:00 AM"

        presenter = BookingConfirmedPresenter(this, doctorName, specialty, date, time)

        // Buttons
        findViewById<Button>(R.id.btnAddToCalendar).setOnClickListener {
            presenter.onAddToCalendarClicked()
        }
        findViewById<Button>(R.id.btnGoToDashboard).setOnClickListener {
            presenter.onGoToDashboardClicked()
        }
        findViewById<ImageView>(R.id.btnBackConfirmed).setOnClickListener {
            presenter.onGoToDashboardClicked()
        }
        findViewById<Button>(R.id.btnViewDirections).setOnClickListener {
            // Open maps
            val mapIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("geo:0,0?q=City+General+Hospital"))
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        presenter.loadBookingDetails()
    }

    override fun showBookingDetails(doctorName: String, specialty: String, date: String, time: String, bookingId: String) {
        findViewById<TextView>(R.id.textConfirmDoctorName).text = doctorName
        findViewById<TextView>(R.id.textConfirmSpecialty).text = specialty
        findViewById<TextView>(R.id.textConfirmDateTime).text = "$date • $time"
        findViewById<TextView>(R.id.textConfirmSubtitle).text =
            "Your appointment with $doctorName has been successfully scheduled. We've sent a confirmation to your email."
        findViewById<TextView>(R.id.textBookingId).text = bookingId
    }

    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun openCalendar(doctorName: String, date: String, time: String) {
        val calIntent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "Appointment with $doctorName")
            putExtra(CalendarContract.Events.DESCRIPTION, "Medical appointment")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "City General Hospital, Room 402")
        }
        startActivity(calIntent)
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
