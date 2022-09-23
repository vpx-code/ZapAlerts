package com.xvlaze.zapalerts.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.Menu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.adapters.InterestsAdapter
import com.xvlaze.zapalerts.databinding.ActivityMainBinding
import com.xvlaze.zapalerts.model.InterestCloudObject


class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InterestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isFirstTime()
        viewModel.isFirstTime.observe(this) { isFirstTime ->
            if (isFirstTime) {
                displayHelpDialog()
                viewModel.notFirstTimeAnymore()
            }
        }

        val toolbar = binding.toolBarLayout
        toolbar.inflateMenu(R.menu.top_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.help) -> {
                    displayHelpDialog()
                }
                getString(R.string.log_out) -> {
                    val dialogClickListener =
                        DialogInterface.OnClickListener { dialog, which ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    viewModel.signOut()
                                    dialog.dismiss()
                                    finish()
                                    Intent(this, LoginActivity::class.java).apply {
                                        startActivity(this)
                                    }
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                    dialog.dismiss()
                                }
                            }
                        }

                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                    builder.setMessage(getString(R.string.logout_sure))
                        .setPositiveButton(
                            Html.fromHtml(
                                "<font color='${getColor(R.color.huawei_blue)}'>" +
                                        getString(R.string.yes) +
                                        "</font>",
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            ),
                            dialogClickListener
                        )
                        .setNegativeButton(
                            getString(R.string.no), dialogClickListener
                        ).show()
                }
            }
            true
        }

        recyclerView = binding.recycler
        adapter = InterestsAdapter(arrayListOf())
        recyclerView.adapter = adapter

        viewModel.savedInterests.observe(this) {
            val newIds = it.map { el2 ->
                el2.id
            }

            val adapterIds = adapter.initialInterestList.map { el1 ->
                el1.id
            }

            if (adapterIds.isEmpty() or
                (newIds != adapterIds)
            ) {
                adapter = InterestsAdapter(it as ArrayList<InterestCloudObject>)
                adapter.setOnItemClickListener(object : InterestsAdapter.IOnItemClickListener {
                    override fun onSourceClicked(position: Int) {
                        Intent(this@MainActivity, InterestDetailActivity::class.java).apply {
                            putExtra("name", it[position].name)
                            startActivity(this)
                        }
                    }

                    override fun onEditButtonClicked(position: Int) {
                        val dialog = EditInterestFragment.newInstance(
                            it[position].name
                        )
                        dialog.show(supportFragmentManager, "Edit Interest Fragment")
                    }
                })
                recyclerView.adapter = adapter
            }

            binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    adapter.getFilter().filter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.getFilter().filter(newText)
                    return true
                }
            })
        }

        binding.fab.setOnClickListener {
            Intent(this, InterestsActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun displayHelpDialog() {
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                        Snackbar.make(
                            binding.root,
                            getString(R.string.come_back),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        builder.setMessage(getString(R.string.settings_prompt))
            .setPositiveButton(
                Html.fromHtml(
                    "<font color='${getColor(R.color.huawei_blue)}'>" +
                            getString(R.string.settings) +
                            "</font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ),
                dialogClickListener
            )
            .setNegativeButton(R.string.later, dialogClickListener)
            .setCancelable(false)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

    override fun onDismiss(p0: DialogInterface?) {
        viewModel.getSavedInterestsFromDB()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedInterests()
    }
}