package com.xvlaze.zapalerts.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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

        val toolbar = binding.toolBarLayout
        toolbar.inflateMenu(R.menu.top_menu)
        toolbar.setOnMenuItemClickListener {
            when (it.title) {
                getString(R.string.help) -> {

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
                        .setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show()

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