package com.xvlaze.zapalerts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xvlaze.zapalerts.databinding.ItemListInterestsBinding
import com.xvlaze.zapalerts.model.Interest

class InterestsAdapter(private val interestsList: ArrayList<Interest>): RecyclerView.Adapter<InterestsAdapter.InterestsViewHolder>() {
    private lateinit var listener: InterestsAdapter.IOnItemClickListener
    //private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")

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
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: IOnItemClickListener) {
        this.listener = listener
    }

    class InterestsViewHolder(
        val binding: ItemListInterestsBinding,
        listener: IOnItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}