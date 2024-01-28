package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class PopProductsRecAdapter : RecyclerView.Adapter<PopProductsRecAdapter.ViewHolder>() {

    private var itemCountLimit = 20
    private var currentItemCount = 4
    var isLoading = false
    private var isLoadMoreCoroutineRunning = false

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val strikeText: TextView = itemView.findViewById(R.id.popstrikeText)
        init {
            strikeText.paint.isStrikeThruText = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.popularprod_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return currentItemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load data for each item here if needed
    }

    fun loadMoreItems() {
        if (!isLoading && currentItemCount < itemCountLimit && !isLoadMoreCoroutineRunning) {
            isLoading = true
            isLoadMoreCoroutineRunning = true
            CoroutineScope(Dispatchers.Main).launch {
                val newItemsCount = minOf(2, itemCountLimit - currentItemCount)
                val result = doBackgroundWorkAsync(newItemsCount)
                currentItemCount += result
                notifyItemRangeInserted(currentItemCount - result, result)
                isLoading = false
                isLoadMoreCoroutineRunning = false
            }
        }
    }

    private suspend fun doBackgroundWorkAsync(newItemsCount: Int): Int {
        return withContext(Dispatchers.IO) {
            delay(500) // Simulating some background work
            newItemsCount
        }
    }

    fun getCurrentItemCount(): Int {
        return currentItemCount
    }
}
