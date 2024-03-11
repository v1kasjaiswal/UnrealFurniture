package com.vikasjaiswal.unrealfurniture

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.random.Random

class MyWishListRecAdapter(private val onDataChanged: () -> Unit) : RecyclerView.Adapter<MyWishListRecAdapter.ViewHolder>() {

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
    var ratingCounts = listOf<String>()

        init {
            updateData()
        }

        fun setData(data: List<String>) {
            prodIds = data
            notifyDataSetChanged()
            onDataChanged.invoke()
        }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var wishMainImage: ImageView
        lateinit var wishName: TextView
        lateinit var wishPrice: TextView
        lateinit var wishDiscount: TextView
        lateinit var wishDiscountedPrice: TextView
        lateinit var wishRating: RatingBar
        lateinit var wishRatingCount: TextView

        lateinit var removeWish: ImageView

        init {
            wishMainImage = itemView.findViewById(R.id.wishMainImage)
            wishName = itemView.findViewById(R.id.wishName)
            wishPrice = itemView.findViewById(R.id.wishPrice)
            wishDiscount = itemView.findViewById(R.id.wishDiscount)
            wishDiscountedPrice = itemView.findViewById(R.id.wishDiscountedPrice)
            wishRating = itemView.findViewById(R.id.wishRating)
            wishRatingCount = itemView.findViewById(R.id.wishRatingCount)

            removeWish = itemView.findViewById(R.id.wishRemove)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.mywishlist_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mainImages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.wishName.text = names[position]
        holder.wishPrice.text = "₹" + prices[position]
        holder.wishDiscount.text = discounts[position] + "% off"
        holder.wishDiscountedPrice.text = "₹" + discountedPrice[position]
        holder.wishRating.rating = ratings[position].toFloat()
        holder.wishRatingCount.text = ratingCounts[position]

        Picasso.get().load(mainImages[position]).into(holder.wishMainImage)

        holder.removeWish.setOnClickListener {
            removeWish(position)
        }
    }

    private fun removeWish(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(prodIds[position])) {
                            userRef.update("wishList", wishList.minus(prodIds[position]))
                                .addOnSuccessListener {
                                    prodIds = prodIds.minus(prodIds[position])
                                    mainImages = mainImages.minus(mainImages[position])
                                    names = names.minus(names[position])
                                    prices = prices.minus(prices[position])
                                    discounts = discounts.minus(discounts[position])
                                    ratings = ratings.minus(ratings[position])
                                    ratingCounts = ratingCounts.minus(ratingCounts[position])
                                    discountedPrice = discountedPrice.minus(discountedPrice[position])
                                    notifyDataSetChanged()
                                    onDataChanged.invoke()
                                }
                                .addOnFailureListener {
                                    Log.d("ProductActivity", "Error: ${it.message}")
                                }
                        } else {
                            Log.d("ProductActivity", "Error: Product not found in wishList")
                        }
                    }
                }
            }
        }
    }

    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.isNotEmpty()) {
                            prodIds = wishList
                            for (i in wishList) {
                                val prodRef = db.collection("products").document(i)
                                prodRef.get().addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        mainImages = mainImages.plus(
                                            document.get("prodMainImage").toString()
                                        )
                                        names = names.plus(document.get("productName").toString())
                                        prices =
                                            prices.plus(document.get("productPrice").toString())
                                        discounts = discounts.plus(
                                            document.get("productDiscount").toString()
                                        )
                                        discountedPrice = discountedPrice.plus(
                                            document.get("productDiscountedPrice").toString()
                                        )
                                        ratings = ratings.plus(document.get("prodRating").toString())
                                        ratingCounts = ratingCounts.plus(document.get("prodRatingCount").toString())
                                    }
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

    public fun emptyWishList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRef = db.collection("users").document(uid)
                userRef.update("wishList", emptyList<String>())
                    .addOnSuccessListener {
                        // Clear the local wish list and notify the adapter
                        prodIds = emptyList()
                        mainImages = emptyList()
                        names = emptyList()
                        prices = emptyList()
                        discounts = emptyList()
                        ratings = emptyList()
                        ratingCounts = emptyList()
                        discountedPrice = emptyList()
                        notifyDataSetChanged()
                        onDataChanged.invoke()
                    }
                    .addOnFailureListener { e ->
                        Log.d("WishListActivity", "Error deleting wishes: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.d("WishListActivity", "Error deleting wishes: ${e.message}")
            }
        }
    }

    public fun moveAllToCart() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()

                        if (wishList.isNotEmpty()) {
                            val cartList = document.get("cartList") as? List<String> ?: emptyList()

                            // Filter out duplicates before adding to the cart
                            val uniqueWishList = wishList.filter { !cartList.contains(it) }

                            if (uniqueWishList.isNotEmpty()) {
                                val newCartList = cartList.plus(uniqueWishList)
                                userRef.update("cartList", newCartList)
                                    .addOnSuccessListener {
                                        userRef.update("wishList", emptyList<String>())
                                            .addOnSuccessListener {
                                                // Clear the local wish list and notify the adapter
                                                prodIds = emptyList()
                                                mainImages = emptyList()
                                                names = emptyList()
                                                prices = emptyList()
                                                discounts = emptyList()
                                                ratings = emptyList()
                                                ratingCounts = emptyList()
                                                discountedPrice = emptyList()
                                                notifyDataSetChanged()
                                                onDataChanged.invoke()
                                            }
                                            .addOnFailureListener { e ->
                                                Log.d("WishListActivity", "Error deleting wishes: ${e.message}")
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.d("WishListActivity", "Error moving wishes to cart: ${e.message}")
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

}
