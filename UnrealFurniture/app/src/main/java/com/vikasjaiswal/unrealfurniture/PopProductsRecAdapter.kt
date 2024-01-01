package com.vikasjaiswal.unrealfurniture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PopProductsRecAdapter: RecyclerView.Adapter<PopProductsRecAdapter.ViewHolder>()
{
    lateinit var strikeText : TextView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopProductsRecAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.popularprod_recresource, parent, false)

        strikeText = v.findViewById(R.id.popstrikeText)
        strikeText.paint.isStrikeThruText = true

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 10
    }


    override fun onBindViewHolder(holder: PopProductsRecAdapter.ViewHolder, position: Int) {
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
    {

    }

}