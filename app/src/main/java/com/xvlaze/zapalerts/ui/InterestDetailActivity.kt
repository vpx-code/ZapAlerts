package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.adapters.AlertsAdapter
import com.xvlaze.zapalerts.databinding.ActivityInterestDetailBinding

class InterestDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInterestDetailBinding
    private val viewModel: InterestDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterestDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val upperBlob = binding.upperBlob
        val lowerBlob = binding.lowerBlob
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                upperBlob.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blob_night))
                lowerBlob.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blob_night))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                upperBlob.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blob))
                lowerBlob.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blob))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                upperBlob.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blob))
                lowerBlob.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.blob))
            }
        }

        val recyclerView = binding.recycler
        var adapter = AlertsAdapter(arrayListOf())
        recyclerView.adapter = adapter

        val name = intent.getStringExtra("name")
        binding.title.text = name

        viewModel.searchInterest(name!!)
        viewModel.foundInterest.observe(this) {
            viewModel.doSearch(
                it.name,
                it.language,
                it.country
            )

            viewModel.searchResults.observe(this) { response ->
                adapter = AlertsAdapter(response)
                adapter.setOnItemClickListener(object: AlertsAdapter.IOnItemClickListener {
                    override fun onItemClick(position: Int) {
                        openInBrowser(response[position].clickUrl)
                    }
                })
                recyclerView.adapter = adapter
            }
        }
    }

    private fun openInBrowser(selectedItem: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem)).apply {
            startActivity(this)
        }
}