package com.xvlaze.zapalerts.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.adapters.AlertsAdapter
import com.xvlaze.zapalerts.databinding.ActivityInterestsBinding
import com.xvlaze.zapalerts.util.Constants.AlertType.*
import com.xvlaze.zapalerts.util.Constants.InterestType.*


class InterestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInterestsBinding
    private val viewModel: InterestsViewModel by viewModels()
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: AlertsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterestsBinding.inflate(layoutInflater)
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
                        binding.settings.visibility = View.GONE
                        binding.progress.visibility = View.VISIBLE
                        val imm: InputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.searchview.applicationWindowToken, 0)
                        viewModel.doSearch(
                            searchQuery.toString(),
                            binding.settings.getType(),
                            binding.settings.getLang(),
                            binding.settings.getCountry()
                        )

                        viewModel.searchResults.observe(this@InterestsActivity) {
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
                                    when (binding.settings.getFrequency()) {
                                        0 -> {
                                            DAILY
                                        }
                                        1 -> {
                                            WEEKLY
                                        }
                                        2 -> {
                                            REALTIME
                                        }
                                        else -> {
                                            DAILY
                                        }
                                    },
                                    binding.settings.getLang(),
                                    binding.settings.getCountry(),
                                    // FIXME: No debería estar aquí.
                                    when (binding.settings.getType()) {
                                        0 -> Website
                                        1 -> Image
                                        2 -> Video
                                        3 -> News
                                        else -> {
                                            News
                                        }
                                    }
                                )
                                viewModel.isInterestUnique(searchQuery.toString())
                                viewModel.isInterestSaved.observe(this@InterestsActivity) { isSaveSuccessful ->
                                    when {
                                        isSaveSuccessful -> {
                                            finish()
                                        }
                                        else -> {
                                            // Todo: Colores!!
                                            Snackbar.make(
                                                binding.root,
                                                "Ya tienes este interés guardado. Por favor, cambia la búsqueda y vuelve a intentarlo.",
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })

        /*binding.settings.visibility = View.VISIBLE
        binding.settings.customizeOption.setOnClickListener {
            if (isCustomizeMenuVisible) {
                binding.settings.customizeDropdown.visibility = View.GONE
                binding.settings.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            } else {
                binding.settings.customizeDropdown.visibility = View.VISIBLE
                binding.settings.arrowIcon.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            isCustomizeMenuVisible = !isCustomizeMenuVisible
        }
        initializeSpinners()*/
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
        }
    }

    /*private fun initializeSpinners() {
        freqSpinner = binding.settings.freqSpinner
        typeSpinner = binding.settings.typeSpinner
        countrySpinner = binding.settings.countrySpinner
        langSpinner = binding.settings.langSpinner

        val freqAdapter =
            ArrayAdapter.createFromResource(this, R.array.freqs, (R.layout.spinner_item))
        freqAdapter.setDropDownViewResource(R.layout.spinner_item)
        freqSpinner.adapter = freqAdapter

        val typeAdapter =
            ArrayAdapter.createFromResource(this, R.array.types, (R.layout.spinner_item))
        typeAdapter.setDropDownViewResource(R.layout.spinner_item)
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(3) // TODO: No dejar así

        val countryAdapter =
            ArrayAdapter.createFromResource(this, R.array.countries, (R.layout.spinner_item))
        countryAdapter.setDropDownViewResource(R.layout.spinner_item)
        countrySpinner.adapter = countryAdapter
        countrySpinner.setSelection(0)

        val langAdapter =
            ArrayAdapter.createFromResource(this, R.array.languages, (R.layout.spinner_item))
        langAdapter.setDropDownViewResource(R.layout.spinner_item)
        langSpinner.adapter = langAdapter
        viewModel.getPreferredLanguage()
        viewModel.preferredLanguage.observe(this) {
            langSpinner.setSelection(it)
        }
    }*/

    private fun openInBrowser(selectedItem: String) =
        Intent(Intent.ACTION_VIEW, Uri.parse(selectedItem)).apply {
            startActivity(this)
        }

    // FIXME: No va.
    override fun onResume() {
        super.onResume()
        Toast.makeText(this@InterestsActivity, "Resume...", Toast.LENGTH_SHORT).show()
        adapter.notifyDataSetChanged()
    }
}
