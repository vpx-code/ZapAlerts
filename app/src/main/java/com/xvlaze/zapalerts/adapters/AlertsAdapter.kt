package com.xvlaze.zapalerts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.R
import com.xvlaze.zapalerts.databinding.ItemListNewsBinding
import com.xvlaze.zapalerts.model.MyApplication.Companion.appContext
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*

class AlertsAdapter(private val newsList: ArrayList<NewsItem>) :
    RecyclerView.Adapter<AlertsAdapter.TimesViewHolder>() {
    private lateinit var listener: IOnItemClickListener
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemListNewsBinding.inflate(layoutInflater, parent, false)
        return TimesViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: TimesViewHolder, position: Int) {
        val new = newsList[position] // FIXME: Fallo Lukoil: A veces el número es "". Investigar.
        val netDate = Date(new.publishTime.toLong() * 1000)
        holder.binding.date.text = sdf.format(netDate)
        holder.binding.source.text = buildString {
            append(appContext.getString(R.string.read_more_at))
            append(getDomainName(new.clickUrl))
        }
        holder.binding.headline.text = new.title
            .replace("&#39;", "'")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
    }

    override fun getItemCount(): Int = newsList.size

    interface IOnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: IOnItemClickListener) {
        this.listener = listener
    }

    private fun getDomainName(url: String?): String {
        return try {
            val uri = URI(url)
            val domain: String = uri.host
            if (domain.startsWith("www.")) domain.substring(4) else domain
        } catch (ex: URISyntaxException) {
            ""
        }
    }

    class TimesViewHolder(
        val binding: ItemListNewsBinding,
        listener: IOnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}