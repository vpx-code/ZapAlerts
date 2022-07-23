package com.xvlaze.zapalerts.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.xvlaze.zapalerts.R

class SettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val isCollapsible: Boolean
    private var isCustomizeMenuVisible = false

    private lateinit var freqSpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var countrySpinner: Spinner
    private lateinit var langSpinner: Spinner

    init {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.view_interest_customizer, this, true)
        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.SettingsView)
        isCollapsible = styledAttributes.getBoolean(R.styleable.SettingsView_isCollapsible, true)
        styledAttributes.recycle()

        val dropdownArrow = view.findViewById<ImageView>(R.id.arrowIcon)
        dropdownArrow.visibility = if (isCollapsible) View.VISIBLE else View.GONE

        val dropdownMenu = findViewById<LinearLayout>(R.id.customize_dropdown)

        findViewById<TextView>(R.id.customize_option).setOnClickListener {
            if (isCustomizeMenuVisible) {
                dropdownMenu.visibility = View.GONE
                dropdownArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            } else {
                dropdownMenu.visibility = View.VISIBLE
                dropdownArrow.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }
            isCustomizeMenuVisible = !isCustomizeMenuVisible
        }
        initializeSpinners()
    }

    private fun initializeSpinners() {
        freqSpinner = findViewById(R.id.freq_spinner)
        typeSpinner = findViewById(R.id.type_spinner)
        countrySpinner = findViewById(R.id.country_spinner)
        langSpinner = findViewById(R.id.lang_spinner)

        val freqAdapter =
            ArrayAdapter.createFromResource(context, R.array.freqs, (R.layout.spinner_item))
        freqAdapter.setDropDownViewResource(R.layout.spinner_item)
        freqSpinner.adapter = freqAdapter
        freqSpinner.setSelection(0)

        val typeAdapter =
            ArrayAdapter.createFromResource(context, R.array.types, (R.layout.spinner_item))
        typeAdapter.setDropDownViewResource(R.layout.spinner_item)
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(3)

        val countryAdapter =
            ArrayAdapter.createFromResource(context, R.array.countries, (R.layout.spinner_item))
        countryAdapter.setDropDownViewResource(R.layout.spinner_item)
        countrySpinner.adapter = countryAdapter
        countrySpinner.setSelection(0)


        val langAdapter =
            ArrayAdapter.createFromResource(context, R.array.languages, (R.layout.spinner_item))
        langAdapter.setDropDownViewResource(R.layout.spinner_item)
        langSpinner.adapter = langAdapter
        /*viewModel.getPreferredLanguage()
        viewModel.preferredLanguage.observe(this) {
            langSpinner.setSelection(it)
        }*/
        langSpinner.setSelection(0)
    }

    fun setFreqSelection(selection: Int) {
        freqSpinner.setSelection(selection)
    }

    fun setTypeSelection(selection: Int) {
        typeSpinner.setSelection(selection)
    }

    fun setCountrySelection(selection: Int) {
        countrySpinner.setSelection(selection)
    }

    fun setLangSelection(selection: Int) {
        langSpinner.setSelection(selection)
    }

    fun getFrequency(): Int = freqSpinner.selectedItemPosition
    fun getType(): Int = typeSpinner.selectedItemPosition
    fun getCountry(): Int = countrySpinner.selectedItemPosition
    fun getLang(): Int = langSpinner.selectedItemPosition
}