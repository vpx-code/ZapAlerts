package com.xvlaze.zapalerts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.xvlaze.zapalerts.databinding.ItemListInterestsBinding
import com.xvlaze.zapalerts.model.InterestCloudObject
import java.util.*

class InterestsAdapter(private val interestsList: ArrayList<InterestCloudObject>) :
    RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder>() {
    private lateinit var listener: IOnItemClickListener

    val initialInterestList = ArrayList<InterestCloudObject>().apply {
        addAll(interestsList)
    }

    fun getFilter(): Filter {
        return interestsFilter
    }

    private val interestsFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList: ArrayList<InterestCloudObject> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                initialInterestList.let { filteredList.addAll(it) }
            } else {
                val query = constraint.toString().trim().lowercase(Locale.ROOT)
                initialInterestList.forEach {
                    if (it.name.lowercase(Locale.ROOT).contains(query)) {
                        filteredList.add(it)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results?.values is ArrayList<*>) {
                interestsList.clear()
                interestsList.addAll(results.values as ArrayList<InterestCloudObject>)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListInterestsBinding.inflate(layoutInflater, parent, false)
        return InterestsViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: InterestsViewHolder, position: Int) {
        val interest = interestsList[position]
        holder.binding.interestTitle.text = interest.name
    }

    override fun getItemCount(): Int = interestsList.size

    interface IOnItemClickListener {
        fun onSourceClicked(position: Int)
        fun onEditButtonClicked(position: Int)
    }

    fun setOnItemClickListener(listener: IOnItemClickListener) {
        this.listener = listener
    }

    class InterestsViewHolder(
        val binding: ItemListInterestsBinding,
        listener: IOnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.source.setOnClickListener {
                listener.onSourceClicked(adapterPosition)
            }

            binding.editInterest.setOnClickListener {
                listener.onEditButtonClicked(adapterPosition)
            }
        }
    }
}