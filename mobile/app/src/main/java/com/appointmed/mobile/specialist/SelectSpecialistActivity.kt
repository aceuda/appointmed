package com.appointmed.mobile.specialist

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appointmed.mobile.R
import com.appointmed.mobile.booking.BookAppointmentActivity
import com.appointmed.mobile.dashboard.DashboardActivity
import com.appointmed.mobile.profile.ProfileActivity

class SelectSpecialistActivity : AppCompatActivity(), SelectSpecialistContract.View {

    private lateinit var presenter: SelectSpecialistContract.Presenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DoctorAdapter
    private lateinit var resultCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_specialist)

        presenter = SelectSpecialistPresenter(this)

        recyclerView = findViewById(R.id.recyclerDoctors)
        resultCount = findViewById(R.id.textResultCount)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = DoctorAdapter(emptyList()) { doctor -> presenter.onBookClicked(doctor) }
        recyclerView.adapter = adapter

        // Back button
        findViewById<ImageView>(R.id.btnBackSpecialist).setOnClickListener { finish() }

        // Search
        findViewById<EditText>(R.id.inputSearchDoctor).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { presenter.searchDoctors(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Filter chips
        findViewById<Button>(R.id.chipAll).setOnClickListener {
            setActiveChip(it as Button)
            presenter.filterBySpecialty("All")
        }
        findViewById<Button>(R.id.chipCardio).setOnClickListener {
            setActiveChip(it as Button)
            presenter.filterBySpecialty("Cardiologist")
        }
        findViewById<Button>(R.id.chipDerma).setOnClickListener {
            setActiveChip(it as Button)
            presenter.filterBySpecialty("Dermatologist")
        }

        // Bottom nav
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener { presenter.onHomeClicked() }
        findViewById<LinearLayout>(R.id.navProfile).setOnClickListener { presenter.onProfileClicked() }

        presenter.loadDoctors()
    }

    private fun setActiveChip(active: Button) {
        val chips = listOf(
            findViewById<Button>(R.id.chipAll),
            findViewById<Button>(R.id.chipCardio),
            findViewById<Button>(R.id.chipDerma)
        )
        chips.forEach { chip ->
            if (chip == active) {
                chip.setBackgroundResource(R.drawable.bg_chip_active)
                chip.setTextColor(resources.getColor(android.R.color.white, null))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_inactive)
                chip.setTextColor(resources.getColor(R.color.textSecondary, null))
            }
        }
    }

    override fun showDoctors(doctors: List<DoctorItem>) {
        adapter.updateData(doctors)
    }

    override fun showFilteredDoctors(doctors: List<DoctorItem>, count: Int) {
        adapter.updateData(doctors)
        resultCount.text = "$count RESULTS"
    }

    override fun navigateToBooking(doctor: DoctorItem) {
        val intent = Intent(this, BookAppointmentActivity::class.java)
        intent.putExtra("doctor_name", doctor.name)
        intent.putExtra("doctor_specialty", doctor.specialty)
        intent.putExtra("doctor_fee", doctor.fee)
        intent.putExtra("doctor_clinic", doctor.clinic)
        startActivity(intent)
    }

    override fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    // ---- RecyclerView Adapter ----
    inner class DoctorAdapter(
        private var doctors: List<DoctorItem>,
        private val onBookClick: (DoctorItem) -> Unit
    ) : RecyclerView.Adapter<DoctorAdapter.VH>() {

        fun updateData(newData: List<DoctorItem>) {
            doctors = newData
            notifyDataSetChanged()
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.textDoctorName)
            val specialty: TextView = view.findViewById(R.id.textDoctorSpecialty)
            val clinic: TextView = view.findViewById(R.id.textDoctorClinic)
            val fee: TextView = view.findViewById(R.id.textDoctorFee)
            val rating: TextView = view.findViewById(R.id.textDoctorRating)
            val btnBook: Button = view.findViewById(R.id.btnBookDoctor)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor_card, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val doc = doctors[position]
            holder.name.text = doc.name
            holder.specialty.text = doc.specialty
            holder.clinic.text = doc.clinic
            holder.fee.text = "₱${String.format("%,d", doc.fee)}"
            holder.rating.text = "★ ${doc.rating}"

            if (doc.available) {
                holder.btnBook.text = "Book"
                holder.btnBook.setBackgroundResource(R.drawable.bg_btn_book)
                holder.btnBook.setTextColor(holder.itemView.resources.getColor(android.R.color.white, null))
                holder.btnBook.isEnabled = true
            } else {
                holder.btnBook.text = "Waitlist"
                holder.btnBook.setBackgroundResource(R.drawable.bg_chip_inactive)
                holder.btnBook.setTextColor(holder.itemView.resources.getColor(R.color.textSecondary, null))
                holder.btnBook.isEnabled = true
            }
            holder.btnBook.setOnClickListener { onBookClick(doc) }
        }

        override fun getItemCount() = doctors.size
    }
}
