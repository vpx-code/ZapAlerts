package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.adapters.InterestsAdapter
import com.xvlaze.zapalerts.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            MainViewModel.MyViewModelFactory(application)
        ).get(
            MainViewModel::class.java)

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
        var adapter = InterestsAdapter(arrayListOf())
        recyclerView.adapter = adapter

        viewModel.getSavedInterests()
        viewModel.savedInterests.observe(this) {
            adapter = InterestsAdapter(it)
            adapter.setOnItemClickListener(object: InterestsAdapter.IOnItemClickListener {
                override fun onSourceClicked(position: Int) {
                    Intent(this@MainActivity, InterestDetailActivity::class.java).apply {
                        putExtra("name", it[position].name)
                        startActivity(this)
                    }
                }

                override fun onEditButtonClicked(position: Int) {
                    EditInterestFragment().show(supportFragmentManager, "Edit Interest Fragment")
                }
            })
            recyclerView.adapter = adapter
        }

        binding.fab.setOnClickListener {
            Intent(this, InterestsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedInterests()
    }
}