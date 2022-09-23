package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.adapters.AlertsAdapter
import com.xvlaze.zapalerts.databinding.ActivityInterestsBinding


class InterestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInterestsBinding
    private val viewModel: InterestsViewModel by viewModels()
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: AlertsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.preview.visibility = View.INVISIBLE
        binding.progress.visibility = View.INVISIBLE
        val recyclerView = binding.recycler
        adapter = AlertsAdapter(arrayListOf())
        recyclerView.adapter = adapter

        fab = binding.saveAlert
        putFabOnSearchMode()

        binding.searchview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(searchQuery: Editable?) {
                if (searchQuery.isNullOrBlank()) {
                    putFabOnSearchMode()
                } else {
                    fab.setOnClickListener {
                        binding.progress.visibility = View.VISIBLE
                        binding.preview.visibility = View.VISIBLE

                        val imm: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.searchview.applicationWindowToken, 0)
                        viewModel.doSearch(
                            searchQuery.toString(),
                            binding.settings.getLang(),
                            binding.settings.getCountry()
                        )

                        viewModel.searchResults.observe(this@InterestsActivity) {
                            binding.settings.toggle()
                            binding.progress.visibility = View.INVISIBLE
                            adapter = AlertsAdapter(it)
                            adapter.setOnItemClickListener(object :
                                AlertsAdapter.IOnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    openInBrowser(it[position].clickUrl)
                                }
                            })
                            recyclerView.adapter = adapter

                            fab.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this@InterestsActivity,
                                    R.drawable.ic_baseline_check_24
                                )
                            )
                            fab.setOnClickListener {
                                viewModel.saveInterest(
                                    searchQuery.toString(),
                                    binding.settings.getFrequency(),
                                    binding.settings.getLang(),
                                    binding.settings.getCountry()
                                )
                                viewModel.isInterestUnique(searchQuery.toString())
                                viewModel.isInterestSaved.observe(this@InterestsActivity) { isSaveSuccessful ->
                                    when {
                                        // TODO: Is Interest Unique?
                                        isSaveSuccessful -> {
                                            finish()
                                        }
                                        else -> {
                                            val snackbar = Snackbar.make(
                                                binding.root,
                                                getString(R.string.already_saved),
                                                Snackbar.LENGTH_LONG
                                            )

                                            snackbar.apply {
                                                setBackgroundTint(
                                                    ContextCompat.getColor(
                                                        this@InterestsActivity,
                                                        R.color.danger
                                                    )
                                                )
                                                show()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun putFabOnSearchMode() {
        fab.setImageDrawable(
            ContextCompat.getDrawable(
                this@InterestsActivity,
                R.drawable.ic_search
            )
        )
        fab.setOnClickListener {
            binding.searchview.requestFocus()
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchview, InputMethodManager.SHOW_IMPLICIT)
            binding.preview.visibility = View.VISIBLE
        }
    }

    private fun openInBrowser(selectedItem: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem)).apply {
            startActivity(this)
        }
}
