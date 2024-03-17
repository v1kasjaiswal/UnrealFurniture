package com.vikasjaiswal.unrealfurniture

import android.content.Intent
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
    var prices = listOf<Int>()
    var discounts = listOf<Int>()
    var discountedPrice = listOf<Int>()
    var ratings = listOf<String>()
    var quantity = mutableListOf<Int>()

    var ratingCounts = listOf<Int>()

    var overAllRealPrice = 0
    var overAllDiscountedPrice = 0
    var overAllQuantity = 0
    var overAllDiscount = 0

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
        holder.prodName.text = names[position]
        holder.prodPrice.text = "₹"+prices[position]
        holder.prodDiscount.text = discounts[position].toString()+"% ↓"
        holder.prodDiscountedPrice.text = "₹"+discountedPrice[position]
        holder.prodRating.rating = ratings[position].toFloat()
        holder.prodRatingCount.text = ratingCounts[position].toString()
        holder.prodQuantity.text = quantity[position].toString()

        Picasso.get().load(mainImages[position]).placeholder(R.drawable.blank).into(holder.prodMainImage)

        holder.incrementQuantity.setOnClickListener {
            var pquantity = holder.prodQuantity.text.toString().toInt()
            if (pquantity <= 9){
                pquantity++
            }
            quantity[position] = pquantity
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
            quantity[position] = pquantity
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
                            for (i in cartList) {
                                val prodRef = db.collection("products").document(i)
                                prodRef.get().addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        prodIds = prodIds.plus(document.id)
                                        mainImages = mainImages.plus(document.get("prodMainImage").toString())
                                        names = names.plus(document.get("productName").toString())
                                        prices = prices.plus(document.get("productPrice").toString().toInt())
                                        discounts = discounts.plus(document.get("productDiscount").toString().toInt())
                                        discountedPrice = discountedPrice.plus(document.get("productDiscountedPrice").toString().toInt())
                                        quantity.add(1)
                                        ratings = ratings.plus(document.get("prodRating").toString())
                                        ratingCounts = ratingCounts.plus(document.get("prodRatingCount").toString().toInt())
                                    }

                                    Log.d("MAInIMage", mainImages.toString())
                                    Log.d("Names", names.toString())
                                    Log.d("Prices", prices.toString())
                                    Log.d("Discounts", discounts.toString())
                                    Log.d("DiscountedPrices", discountedPrice.toString())
                                    Log.d("Ratings", ratings.toString())
                                    Log.d("RatingCounts", ratingCounts.toString())
                                    Log.d("Quantity", quantity.toString())

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
                                    ratings = ratings.minus(ratings[position])
                                    ratingCounts = ratingCounts.minus(ratingCounts[position])
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
                        clearCart()
                    }
                    .addOnFailureListener { e ->
                        Log.d("CartListActivity", "Error deleting wishes: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.d("CartListActivity", "Error deleting wishes: ${e.message}")
            }
        }
    }

    public fun calculateCartPrice() {
        var totalRealPrice = 0
        var totalDiscountedPrice = 0
        var totalQuantity = 0

        for (i in prodIds.indices) {
            val realPrice = prices[i].toInt() * quantity[i].toInt()
            val discountedPrice = discountedPrice[i].toInt() * quantity[i].toInt()

            totalRealPrice += realPrice
            totalDiscountedPrice += discountedPrice
            totalQuantity += quantity[i].toInt()

            Log.d("Product $i Real Price", realPrice.toString())
            Log.d("Product $i Discounted Price", discountedPrice.toString())
        }

        val totalDiscount = if (totalRealPrice != 0) {
            ((totalRealPrice - totalDiscountedPrice) / totalRealPrice.toFloat()) * 100
        } else {
            0
        }

        overAllRealPrice = totalRealPrice
        overAllDiscountedPrice = totalDiscountedPrice
        overAllQuantity = totalQuantity
        overAllDiscount = totalDiscount.toInt()


        onDataChanged.invoke()
    }

    public fun clearCart(){
        prodIds = emptyList()
        mainImages = emptyList()
        names = emptyList()
        prices = emptyList()
        discounts = emptyList()
        discountedPrice = emptyList()
        ratings = emptyList()
        ratingCounts = emptyList()
        quantity = mutableListOf<Int>()
        calculateCartPrice()
        notifyDataSetChanged()
        onDataChanged.invoke()
    }
}