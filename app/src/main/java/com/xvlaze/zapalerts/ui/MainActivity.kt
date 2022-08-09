package com.xvlaze.zapalerts.ui

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.adapters.InterestsAdapter
import com.xvlaze.zapalerts.databinding.ActivityMainBinding
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.repository.CloudDBRepository


class MainActivity : AppCompatActivity(), DialogInterface.OnDismissListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: InterestsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CloudDBRepository.initAGConnectCloudDB(this)

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

        recyclerView = binding.recycler
        adapter = InterestsAdapter(arrayListOf())
        recyclerView.adapter = adapter

        viewModel.getSavedInterests()
        viewModel.savedInterests.observe(this) {
            adapter = InterestsAdapter(it as ArrayList<InterestCloudObject>)
            adapter.setOnItemClickListener(object: InterestsAdapter.IOnItemClickListener {
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

            binding.searchview.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
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

    override fun onDismiss(p0: DialogInterface?) {
        /*// TODO: Dudoso
        val snackbar = Snackbar.make(
            binding.root,
            "Updating Interests...",
            Snackbar.LENGTH_LONG
        )

        snackbar.apply {
            setBackgroundTint(
                ContextCompat.getColor(
                    context,
                    R.color.huawei_blue
                )
            )
            show()
        }*/
        viewModel.getSavedInterests()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedInterests()
    }
}