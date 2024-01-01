package com.vikasjaiswal.unrealfurniture

// DecorProdRecAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DecorProdRecAdapter : RecyclerView.Adapter<DecorProdRecAdapter.ViewHolder>() {

    private var itemCount = 4 // Initial number of items
    private var isLoading = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.decorprod_recresource, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // No need to implement anything here if you don't have specific data binding logic
        // If you have actual data, you might want to bind it to the views here
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Declare and initialize your view components here if needed
        private val titleText: TextView = itemView.findViewById(R.id.decorstrikeText)

        init {
            // Any additional initialization logic for the view components can go here
        }
    }

    fun setLoaded() {
        isLoading = false
    }

    fun setItemCount(newItemCount: Int) {
        itemCount = newItemCount
        notifyDataSetChanged()
    }

    fun loadMoreData() {
        if (!isLoading) {
            // Load more data here (e.g., next 10 cards)
            // For example, you might increase the item count
            isLoading = true
            setItemCount(itemCount + 4)
            setLoaded()
        }
    }
}
