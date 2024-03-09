package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class CheckoutAddressRecAdapter : RecyclerView.Adapter<CheckoutAddressRecAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.checkoutaddress_recresource, parent, false)

        val addressCardView = v.findViewById<MaterialCardView>(R.id.addressCardView)

        addressCardView.setOnClickListener {
            if (addressCardView.isChecked==true){
                addressCardView.isChecked = false
            }else{
                addressCardView.isChecked = true
            }

        }

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Load data for each item here if needed
    }
}