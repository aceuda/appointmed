package com.appointmed.mobile.admin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appointmed.mobile.R
import com.appointmed.mobile.profile.ProfileActivity

class AdminDashboardActivity : AppCompatActivity(), AdminDashboardContract.View {

    private lateinit var presenter: AdminDashboardContract.Presenter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        presenter = AdminDashboardPresenter(this)

        recyclerView = findViewById(R.id.recyclerAdminUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminUserAdapter(emptyList())
        recyclerView.adapter = adapter

        // Tab filter buttons
        val tabAll = findViewById<Button>(R.id.tabAllUsers)
        val tabDocs = findViewById<Button>(R.id.tabDoctors)
        val tabPats = findViewById<Button>(R.id.tabPatients)

        tabAll.setOnClickListener { setActiveTab(tabAll, listOf(tabDocs, tabPats)); presenter.filterByTab("All") }
        tabDocs.setOnClickListener { setActiveTab(tabDocs, listOf(tabAll, tabPats)); presenter.filterByTab("Doctors") }
        tabPats.setOnClickListener { setActiveTab(tabPats, listOf(tabAll, tabDocs)); presenter.filterByTab("Patients") }

        // Bottom nav
        findViewById<LinearLayout>(R.id.adminNavSettings).setOnClickListener { presenter.onProfileClicked() }

        presenter.loadData()
    }

    private fun setActiveTab(active: Button, others: List<Button>) {
        active.setBackgroundResource(R.drawable.bg_chip_active)
        active.setTextColor(Color.WHITE)
        others.forEach {
            it.setBackgroundResource(R.drawable.bg_chip_inactive)
            it.setTextColor(resources.getColor(R.color.textSecondary, null))
        }
    }

    override fun showUsers(users: List<AdminUserItem>) {
        adapter.updateData(users)
    }

    override fun showFilteredUsers(users: List<AdminUserItem>) {
        adapter.updateData(users)
    }

    override fun showStats(totalDoctors: Int, pendingApps: Int) {
        findViewById<TextView>(R.id.textTotalDoctors).text = "$totalDoctors"
        findViewById<TextView>(R.id.textPendingApps).text = "$pendingApps"
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

    // --- Adapter ---
    inner class AdminUserAdapter(
        private var users: List<AdminUserItem>
    ) : RecyclerView.Adapter<AdminUserAdapter.VH>() {

        fun updateData(newData: List<AdminUserItem>) {
            users = newData
            notifyDataSetChanged()
        }

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val name: TextView = view.findViewById(R.id.textAdminUserName)
            val detail: TextView = view.findViewById(R.id.textAdminUserDetail)
            val email: TextView = view.findViewById(R.id.textAdminUserEmail)
            val status: TextView = view.findViewById(R.id.textAdminUserStatus)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_user, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val user = users[position]
            holder.name.text = user.name
            holder.detail.text = user.detail
            holder.email.text = user.email
            holder.status.text = user.status
            when (user.status) {
                "ACTIVE" -> holder.status.setTextColor(Color.parseColor("#16A34A"))
                "PENDING" -> holder.status.setTextColor(Color.parseColor("#F59E0B"))
                "OFFLINE" -> holder.status.setTextColor(Color.parseColor("#94A3B8"))
                else -> holder.status.setTextColor(Color.parseColor("#64748B"))
            }
        }

        override fun getItemCount() = users.size
    }
}
