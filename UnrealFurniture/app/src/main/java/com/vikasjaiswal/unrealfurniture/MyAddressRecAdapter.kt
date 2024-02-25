package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAddressRecAdapter : RecyclerView.Adapter<MyAddressRecAdapter.ViewHolder>() {

    private var itemCountLimit = 4

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.myaddress_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemCountLimit
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load data for each item here if needed
    }
}
