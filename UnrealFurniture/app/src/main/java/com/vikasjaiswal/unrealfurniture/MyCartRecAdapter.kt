package com.vikasjaiswal.unrealfurniture

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat

class MyCartRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyCartRecAdapter.ViewHolder>() {

    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    var uid = auth.currentUser!!.uid

    var prodIds = listOf<String>()
    var mainImages = listOf<String>()
    var names = listOf<String>()
    var prices = listOf<String>()
    var discounts = listOf<String>()
    var discountedPrice = listOf<String>()
    var ratings = listOf<String>()
    var quantity = mutableListOf<String>()

    var ratingCounts = listOf<String>()

    var overAllRealPrice = 0
    var overAllDiscountedPrice = 0
    var overAllQuantity = 0
    var overAllDiscount = 0f

    var name = ""
    var phone = ""
    var email = ""
    var address = ""

    init {
        updateData()
    }

    fun setData(data: List<String>) {
        prodIds = data
        notifyDataSetChanged()
        onDataChanged.invoke()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var prodMainImage: ImageView
        lateinit var prodName: TextView
        lateinit var prodPrice: TextView
        lateinit var prodDiscount: TextView
        lateinit var prodDiscountedPrice : TextView
        lateinit var prodRating: RatingBar
        lateinit var prodRatingCount: TextView

        lateinit var incrementQuantity : ImageView
        lateinit var decrementQuantity : ImageView
        lateinit var prodQuantity : TextView

        lateinit var removeCart : ImageView

        init {
            prodMainImage = itemView.findViewById(R.id.cartProdMainImage)
            prodName = itemView.findViewById(R.id.cartProdName)
            prodPrice = itemView.findViewById(R.id.cartProdPrice)
            prodDiscountedPrice = itemView.findViewById(R.id.cartProdDiscountedPrice)
            prodDiscount = itemView.findViewById(R.id.cartProdDiscount)
            prodRating = itemView.findViewById(R.id.cartProdRating)
            prodRatingCount = itemView.findViewById(R.id.cartProdRatingCount)

            incrementQuantity = itemView.findViewById(R.id.incrementQuantity)
            decrementQuantity = itemView.findViewById(R.id.decrementQuantity)
            prodQuantity = itemView.findViewById(R.id.prodQuantity)

            removeCart = itemView.findViewById(R.id.removeCart)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.mycart_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return prodIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(mainImages[position]).placeholder(R.drawable.blank).into(holder.prodMainImage)
        holder.prodName.text = names[position]
        holder.prodPrice.text = "₹"+prices[position]
        holder.prodDiscount.text = discounts[position]+"% ↓"
        holder.prodDiscountedPrice.text = "₹"+discountedPrice[position]

        holder.prodQuantity.text = quantity[position]

        holder.incrementQuantity.setOnClickListener {
            var pquantity = holder.prodQuantity.text.toString().toInt()
            if (pquantity <= 9){
                pquantity++
            }
            quantity[position] = pquantity.toString()
            calculateCartPrice()
            notifyDataSetChanged()
            onDataChanged.invoke()
            holder.prodQuantity.text = quantity.toString()
        }

        holder.decrementQuantity.setOnClickListener {
            var pquantity = holder.prodQuantity.text.toString().toInt()
            if (pquantity > 1) {
                pquantity--
            }
            quantity[position] = pquantity.toString()
            calculateCartPrice()
            notifyDataSetChanged()
            onDataChanged.invoke()
            holder.prodQuantity.text = quantity.toString()
        }

        holder.removeCart.setOnClickListener {
            removeCart(position)
        }
    }

    private fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cartList = document.get("cartList") as? List<String> ?: emptyList()
                        if (cartList.isNotEmpty()) {
                            prodIds = cartList
                            for (i in cartList) {
                                val prodRef = db.collection("products").document(i)
                                prodRef.get().addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        mainImages = mainImages.plus(document.get("prodMainImage").toString())
                                        names = names.plus(document.get("productName").toString())
                                        prices = prices.plus(document.get("productPrice").toString())
                                        discounts = discounts.plus(document.get("productDiscount").toString())
                                        discountedPrice = discountedPrice.plus(document.get("productDiscountedPrice").toString())
                                        quantity.add("1")

//                                        ratings = ratings.plus(document.get("rating").toString())
//                                        ratingCounts = ratingCounts.plus(document.get("ratingCount").toString())
                                    }
                                    calculateCartPrice()
                                    notifyDataSetChanged()
                                    onDataChanged.invoke()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun removeCart(position: Int){
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cartList = document.get("cartList") as? List<String> ?: emptyList()
                        if (cartList.contains(prodIds[position])) {
                            userRef.update("cartList", cartList.minus(prodIds[position]))
                                .addOnSuccessListener {
                                    prodIds = prodIds.minus(prodIds[position])
                                    mainImages = mainImages.minus(mainImages[position])
                                    names = names.minus(names[position])
                                    prices = prices.minus(prices[position])
                                    discounts = discounts.minus(discounts[position])
                                    discountedPrice = discountedPrice.minus(discountedPrice[position])
                                    quantity.removeAt(position)
                                    calculateCartPrice()
                                    notifyDataSetChanged()
                                    onDataChanged.invoke()
                                }
                                .addOnFailureListener {
                                    Log.d("ProductActivity", "Error: Product not removed from cart")
                                }
                        } else {
                            Log.d("ProductActivity", "Error: Product not found in cart")
                        }
                    }
                }
            }
        }
    }

    public fun emptyCartList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRef = db.collection("users").document(uid)
                userRef.update("cartList", emptyList<String>())
                    .addOnSuccessListener {
                        // Clear the local wish list and notify the adapter
                        prodIds = emptyList()
                        mainImages = emptyList()
                        names = emptyList()
                        prices = emptyList()
                        discounts = emptyList()
                        discountedPrice = emptyList()
                        quantity = mutableListOf<String>()
                        calculateCartPrice()
                        notifyDataSetChanged()
                        onDataChanged.invoke()
                    }
                    .addOnFailureListener { e ->
                        Log.d("CartListActivity", "Error deleting wishes: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.d("CartListActivity", "Error deleting wishes: ${e.message}")
            }
        }
    }

    public fun calculateCartPrice(){
        for (i in 0 until prodIds.size){

            Log.d("MyCartRecAdapter", "calculateCartPrice: ${prices[i]} * ${quantity[i]} = ${prices[i].toInt() * quantity[i].toInt()}")

            overAllRealPrice = prices[i].toInt() * quantity[i].toInt()
            overAllDiscountedPrice = discountedPrice[i].toInt() * quantity[i].toInt()
            overAllQuantity = quantity[i].toInt()
            overAllDiscount = ((overAllRealPrice - overAllDiscountedPrice).toFloat() / overAllRealPrice.toFloat()) * 100
        }
    }

    public fun checkOutProducts(){
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cartList = document.get("cartList") as? List<String> ?: emptyList()
                        if (cartList.isNotEmpty()) {
                            val orderRef = db.collection("orders")
                            val order = hashMapOf(
                                "userId" to user.uid,
                                "userName" to name,
                                "orderPhone" to phone,
                                "userEmail" to email,
                                "orderAddress" to address,
                                "orderList" to cartList,
                                "orderListPrice" to prices,
                                "orderListDiscountedPrice" to discountedPrice,
                                "orderListDiscount" to discounts,
                                "orderQuantity" to quantity,
                                "orderRealPrice" to overAllRealPrice.toString(),
                                "orderDiscountedPrice" to overAllDiscountedPrice.toString(),
                                "orderDiscount" to overAllDiscount.toString(),
                                "orderDate" to DateFormat.getDateTimeInstance().format(System.currentTimeMillis()),
                                "orderStatus" to "Order Placed",
                                "expectedDelivery" to "3-5 days"
                            )
                            orderRef.add(order)
                                .addOnSuccessListener {
                                    userRef.update("cartList", emptyList<String>())
                                        .addOnSuccessListener {
                                            // Clear the local wish list and notify the adapter
                                            prodIds = emptyList()
                                            mainImages = emptyList()
                                            names = emptyList()
                                            prices = emptyList()
                                            discounts = emptyList()
                                            discountedPrice = emptyList()
                                            quantity = mutableListOf<String>()
                                            calculateCartPrice()
                                            notifyDataSetChanged()
                                            onDataChanged.invoke()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.d("CartListActivity", "Error deleting wishes: ${e.message}")
                                        }
                                }
                                .addOnFailureListener {
                                    Log.d("MyCartRecAdapter", "Error: Order not placed")
                                }
                        }
                    }
                }
            }
        }
    }
}
