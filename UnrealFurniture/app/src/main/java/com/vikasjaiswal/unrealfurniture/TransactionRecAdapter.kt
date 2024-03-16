package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class TransactionRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<TransactionRecAdapter.ViewHolder>() {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    var orderIds = listOf<String>()
    var orderStatus = listOf<String>()
    var orderDates = listOf<String>()
    var orderExpectedDates = listOf<String>()
    var orderGrandTotals = listOf<String>()

    private var listener: ListenerRegistration? = null

    init {
        // Initially load data
        updateData("Order Placed")
    }

    fun setData(data: List<String>) {
        orderIds = data
        notifyDataSetChanged()
        onDataChanged.invoke()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var orderId: TextView
        lateinit var orderDate: TextView
        lateinit var orderExpectedDate: TextView
        lateinit var orderStatus: TextView
        lateinit var orderTotal: TextView

        lateinit var myOrderCardView: CardView

        init {
            orderId = itemView.findViewById(R.id.orderId)
            orderDate = itemView.findViewById(R.id.orderDate)
            orderExpectedDate = itemView.findViewById(R.id.orderExpectedDate)
            orderStatus = itemView.findViewById(R.id.orderStatus)
            orderTotal = itemView.findViewById(R.id.orderTotal)

            myOrderCardView = itemView.findViewById(R.id.myOrder)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.myorders_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return orderIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderId.text = "Order ID: #${orderIds[position]}"
        holder.orderDate.text = "Order Date: ${orderDates[position]}"
        holder.orderExpectedDate.text = "Delivery Date: ${orderExpectedDates[position]}"
        holder.orderStatus.text = "Order Status: ${orderStatus[position]}"
        holder.orderTotal.text = "Order Price: ₹${orderGrandTotals[position]}"

        holder.myOrderCardView.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", orderIds[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateData(status: String) {
        // Ensure to stop listening before starting again
        stopListening()
        // Start listening for changes in orders collection where orderStatus is equal to status
        listener = db.collection("orders")
            .whereEqualTo("orderStatus", status)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("TransactionRecAdapter", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    orderIds = snapshots.documents.map { it.id }
                    orderStatus = snapshots.documents.map { it.getString("orderStatus") ?: "" }
                    orderDates = snapshots.documents.map { it.getString("orderDate") ?: "" }
                    orderExpectedDates = snapshots.documents.map { it.getString("expectedDeliveryDate")?.substring(0, 10) ?: "" }
                    orderGrandTotals = snapshots.documents.map { it.getString("grandTotal")?.toString() ?: "" }

                    notifyDataSetChanged()
                    onDataChanged.invoke()
                } else {
                    Log.d("TransactionRecAdapter", "Current data: null")
                }
            }
    }

    fun stopListening() {
        listener?.remove()
    }
}
