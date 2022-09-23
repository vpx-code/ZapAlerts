package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.xvlaze.zapalerts.adapters.AlertsAdapter
import com.xvlaze.zapalerts.databinding.ActivityInterestDetailBinding

class InterestDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInterestDetailBinding
    private val viewModel: InterestDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterestDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.recycler
        var adapter = AlertsAdapter(arrayListOf())
        recyclerView.adapter = adapter

        val name = intent.getStringExtra("name")
        binding.collapsingToolbar.title = name

        viewModel.searchInterest(name!!)
        viewModel.foundInterest.observe(this) { foundInterest ->
            viewModel.doSearch(
                foundInterest.name,
                foundInterest.language.toInt(),
                foundInterest.country.toInt()
            )

            viewModel.searchResults.observe(this) { response ->
                val fromNotification = intent.getBooleanExtra("fromNotification", false)
                viewModel.getSavedDate(foundInterest.frequency.toInt(), fromNotification)
                viewModel.savedDate.observe(this) { savedDate ->
                    adapter = if (savedDate == null) {
                        AlertsAdapter(response)
                    } else {
                        AlertsAdapter(response, savedDate)
                    }

                    adapter.setOnItemClickListener(object : AlertsAdapter.IOnItemClickListener {
                        override fun onItemClick(position: Int) {
                            openInBrowser(response[position].clickUrl)
                        }
                    })
                    recyclerView.adapter = adapter
                }
            }
        }
    }

    private fun openInBrowser(selectedItem: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem)).apply {
            startActivity(this)
        }
}