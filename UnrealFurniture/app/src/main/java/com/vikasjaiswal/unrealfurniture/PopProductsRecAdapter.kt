package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import co.ankurg.expressview.ExpressView
import co.ankurg.expressview.OnCheckListener
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PopProductsRecAdapter : RecyclerView.Adapter<PopProductsRecAdapter.ViewHolder>() {

    var productIds = listOf<String>()
    var productMainImages = listOf<String>()
    var productNames = listOf<String>()
    var productRealPrices = listOf<String>()
    var productDiscounts = listOf<String>()
    var productDiscountedPrices = listOf<String>()
    var productRatings = listOf<String>()
    var productRatingCounts = listOf<String>()

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    init {
        updateData()
    }

    fun setData(data: List<String>) {
        productIds = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var selectedProductCard : CardView

        lateinit var prodMainImages : ImageView
        lateinit var prodName: TextView
        lateinit var realPrice: TextView
        lateinit var discount: TextView
        lateinit var discountedPrice: TextView
        lateinit var rating: RatingBar
        lateinit var ratingCount: TextView

        lateinit var addToWishList : ExpressView

        lateinit var skeletonLayout: SkeletonLayout

        init {
            selectedProductCard = itemView.findViewById(R.id.selectedProductCard)

            prodMainImages = itemView.findViewById(R.id.popProdMainImage)
            prodName = itemView.findViewById(R.id.popProdName)
            realPrice = itemView.findViewById(R.id.popProductRealPrice)
            discount = itemView.findViewById(R.id.popProdDiscount)
            discountedPrice = itemView.findViewById(R.id.popProdDiscountedPrice)
            rating = itemView.findViewById(R.id.popProdRating)
            ratingCount = itemView.findViewById(R.id.popProdRatingCount)

            addToWishList = itemView.findViewById(R.id.addToWishList)

            skeletonLayout = itemView.findViewById(R.id.skeletonLayout)

            realPrice.paint.isStrikeThruText = true

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.popprod_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return productIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.skeletonLayout.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            holder.skeletonLayout.showOriginal()
        }, 1000)

        Picasso.get()
            .load(productMainImages[position])
            .placeholder(R.drawable.blank)
            .into(holder.prodMainImages)

        holder.prodName.text = productNames[position]
        holder.realPrice.text = "₹"+productRealPrices[position]
        holder.discount.text = productDiscounts[position]+"% ↓"
        holder.discountedPrice.text = "₹"+productDiscountedPrices[position]
        holder.rating.rating = productRatings[position].toFloat()
        holder.ratingCount.text = "("+productRatingCounts[position]+")"

        holder.selectedProductCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductActivity::class.java)
            intent.putExtra("productId", productIds[position])
            holder.itemView.context.startActivity(intent)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(productIds[position])) {
                            holder.addToWishList.isChecked = true
                        }
                        else {
                            holder.addToWishList.isChecked = false
                        }
                    }
                }
            }
        }


        holder.addToWishList.setOnCheckListener (object : OnCheckListener {
            override fun onChecked(view: ExpressView?) {
                addToWishList(position)
            }

            override fun onUnChecked(view: ExpressView?) {
                removeFromWishList(position)
            }
        })
    }
    
    private fun addToWishList(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(productIds[position])) {
                            Log.d("ProductActivity", "Already in WishList")
                        }
                        else {
                            userRef.update("wishList", wishList.plus(productIds[position]))
                                .addOnSuccessListener {
                                    Log.d("ProductActivity", "Added to WishList")
                                }
                                .addOnFailureListener {
                                    Log.d("ProductActivity", "Error: ${it.message}")
                                }
                        }
                    }
                }
            }
        }
    }

    private fun removeFromWishList(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(productIds[position])) {
                            userRef.update("wishList", wishList.minus(productIds[position]))
                                .addOnSuccessListener {
                                    Log.d("ProductActivity", "Removed from WishList")
                                }
                                .addOnFailureListener {
                                    Log.d("ProductActivity", "Error: ${it.message}")
                                }
                        }
                        else {
                            Log.d("ProductActivity", "Not in WishList")
                        }
                    }
                }
            }
        }
    }

    private fun updateData(){
        CoroutineScope(Dispatchers.IO).launch {
            try{
            val result = db.collection("products")
                .orderBy("prodRating", Query.Direction.DESCENDING)
                .orderBy("prodRatingCount", Query.Direction.DESCENDING)
                .get().await()

            for (document in result) {
                productIds += document.id
                Log.d("TAG111", "updateData: ${document.id}")
                productMainImages += document.getString("prodMainImage").toString()
                productNames += document.getString("productName").toString()
                Log.d("TAG111", "updateData: ${document.getString("productName").toString()}")
                productRealPrices += document.getLong("productPrice")?.toString()?: "0"
                productDiscounts += document.getLong("productDiscount")?.toString()?:"0"
                productDiscountedPrices += document.getLong("productDiscountedPrice")?.toString()?:"0"
                productRatings += document.getDouble("prodRating")?.toString()?:"0"
                productRatingCounts += document.getLong("prodRatingCount")?.toString()?:"0"
            }

            withContext(Dispatchers.Main){
                notifyDataSetChanged()
            }
            }catch (e: Exception){
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }

}
