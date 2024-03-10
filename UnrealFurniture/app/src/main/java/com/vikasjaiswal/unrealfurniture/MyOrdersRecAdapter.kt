package com.vikasjaiswal.unrealfurniture

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*

class MyOrdersRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyOrdersRecAdapter.ViewHolder>() {

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var orderIds = listOf<String>()
    var userIds = listOf<String>()
    var userNames = listOf<String>()
    var userEmails = listOf<String>()
    var userPhones = listOf<String>()
    var userAddresses = listOf<String>()
    var deliverNames = listOf<String>()
    var orderStatus = listOf<String>()
    var orderDates = listOf<String>()
    var orderExpectedDates = listOf<String>()
    var paymentMethods = listOf<String>()
    var orderRealPrices = listOf<String>()
    var orderDiscounts = listOf<String>()
    var orderDiscountedPrices = listOf<String>()
    var orderGrandTotals = listOf<String>()
//    I want that products in orders to be added in the nested list of the order data

    var orderProdIds = mutableListOf<List<String>>()
    var orderProdNames = mutableListOf<List<String>>()
    var orderProdImages = mutableListOf<List<String>>()
    var orderProdPrices = mutableListOf<List<String>>()
    var orderProdQuantities = mutableListOf<List<String>>()
    var orderProdDiscounts = mutableListOf<List<String>>()
    var orderProdDiscountedPrices = mutableListOf<List<String>>()

    init {
        updateData()
    }

    fun setData(data : List<String>) {
        orderIds = data
        notifyDataSetChanged()
        onDataChanged.invoke()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var orderId : TextView
        lateinit var orderDate : TextView
        lateinit var orderExpectedDate : TextView
        lateinit var orderStatus : TextView
        lateinit var orderTotal : TextView

        init {
            orderId = itemView.findViewById(R.id.orderId)
            orderDate = itemView.findViewById(R.id.orderDate)
            orderExpectedDate = itemView.findViewById(R.id.orderExpectedDate)
            orderStatus = itemView.findViewById(R.id.orderStatus)
            orderTotal = itemView.findViewById(R.id.orderTotal)
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
        holder.orderId.text = "Order ID: #"+orderIds[position]
        holder.orderDate.text = "Order Date: "+orderDates[position]
        holder.orderExpectedDate.text = "Delivery Date: "+orderExpectedDates[position]
        holder.orderStatus.text = "Order Status: "+orderStatus[position]
        holder.orderTotal.text = "Order Price: ₹"+orderGrandTotals[position]
    }

    private fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            val userId = auth.currentUser?.uid
            val orderRef = db.collection("orders")
            val query = orderRef.whereEqualTo("userId", userId)
            query.get().addOnSuccessListener { documents ->
                if (documents != null) {
                    Log.d("TAG", "DocumentSnapshot data: ${documents.documents}")
                    for (document in documents) {
                        orderIds += document.id
                        userIds += document.getString("userId").toString()
                        userNames += document.getString("userName").toString()
                        userEmails += document.getString("userEmail").toString()
                        userPhones += document.getString("selectedPhone").toString()
                        userAddresses += document.getString("selectedAddress").toString()
                        deliverNames += document.getString("selectedName").toString()
                        orderStatus += document.getString("orderStatus").toString()
                        orderDates += document.getString("orderDate").toString()
                        orderExpectedDates += document.getString("expectedDeliveryDate").toString()
                        paymentMethods += document.getString("paymentMethod").toString()
                        orderRealPrices += document.getString("overAllPrice").toString()
                        orderDiscounts += document.getString("overAllDiscount").toString()
                        orderDiscountedPrices += document.getString("overAllDiscountedPrice")
                            .toString()
                        orderGrandTotals += document.getString("grandTotal").toString()

                        val prodIds = document.get("prodIds") as? List<String> ?: emptyList()
                        val prodNames = document.get("prodNames") as? List<String> ?: emptyList()
                        val prodPrices = document.get("prodPrices") as? List<String> ?: emptyList()
                        val prodQuantities = document.get("prodQuantities") as? List<String> ?: emptyList()
                        val prodDiscounts = document.get("prodDiscounts") as? List<String> ?: emptyList()
                        val prodDiscountedPrices = document.get("prodDiscountedPrices") as? List<String> ?: emptyList()
                        val prodImages = document.get("prodImages") as? List<String> ?: emptyList()

                        // Add nested product data to the lists
                        orderProdIds += prodIds
                        orderProdNames += prodNames
                        orderProdPrices += prodPrices
                        orderProdQuantities += prodQuantities
                        orderProdDiscounts += prodDiscounts
                        orderProdDiscountedPrices += prodDiscountedPrices
                        orderProdImages += prodImages

                        Log.d("OrderProdIds", "DocumentSnapshot data: $orderProdIds")
                        Log.d("OrderProdNames", "DocumentSnapshot data: $orderProdNames")
                        Log.d("OrderProdQuantities", "DocumentSnapshot data: $orderProdQuantities")
                    }
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }
            }
        }
    }

}
