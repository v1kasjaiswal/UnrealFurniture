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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SearchRecAdapter : RecyclerView.Adapter<SearchRecAdapter.ViewHolder>() {

    var productIds = listOf<String>()
    var productMainImages = listOf<String>()
    var productNames = listOf<String>()
    var productRealPrices = listOf<String>()
    var productDiscounts = listOf<String>()
    var productDiscountedPrices = listOf<String>()
    var productRatings = listOf<String>()
    var productRatingCounts = listOf<String>()
    var productStock = listOf<String>()

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    init {
        updateData()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var selectedProductCard : CardView

        lateinit var prodMainImage: ImageView
        lateinit var prodName: TextView
        lateinit var realPrice: TextView
        lateinit var discount: TextView
        lateinit var discountedPrice: TextView
        lateinit var rating: RatingBar
        lateinit var ratingCount: TextView

        lateinit var skeleton: Skeleton
        init {
            selectedProductCard = itemView.findViewById(R.id.checkoutProdCard)

            prodMainImage = itemView.findViewById(R.id.checkoutProdMainImage)
            prodName = itemView.findViewById(R.id.checkoutProdName)
            realPrice = itemView.findViewById(R.id.checkoutProdRealPrice)
            discount = itemView.findViewById(R.id.checkoutProdQuantity)
            discountedPrice = itemView.findViewById(R.id.checkoutProdDiscountedPrice)
            rating = itemView.findViewById(R.id.checkoutProdRatingBar)
            ratingCount = itemView.findViewById(R.id.checkoutProdRatingCounts)

            skeleton = itemView.findViewById(R.id.skeletonLayout)

            realPrice.paint.isStrikeThruText = true

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.featprod_recresource, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return productIds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.skeleton.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            holder.skeleton.showOriginal()
        }, 1000)

        Picasso.get()
            .load(productMainImages[position])
            .placeholder(R.drawable.blank)
            .into(holder.prodMainImage)

        holder.prodName.text = productNames[position]
        holder.realPrice.text = "₹${productRealPrices[position]}"
        holder.discount.text = "${productDiscounts[position]}% ↓"
        holder.discountedPrice.text = "₹${productDiscountedPrices[position]}"
        holder.rating.rating = productRatings[position].toFloat()
        holder.ratingCount.text = "(${productRatingCounts[position]})"

        holder.selectedProductCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductActivity::class.java)
            intent.putExtra("productId", productIds[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val result = db.collection("products").get().await()

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
                    productStock += document.getLong("productStock").toString()
                }

                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                }
            }catch (e: Exception){
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }

    fun updateData(query: String) {
        val filteredProductIds = productIds.filter { productNameContainsQuery(it, query) }
        val filteredProductMainImages = filteredProductIds.map { productMainImages[productIds.indexOf(it)] }
        val filteredProductNames = filteredProductIds.map { productNames[productIds.indexOf(it)] }
        val filteredProductRealPrices = filteredProductIds.map { productRealPrices[productIds.indexOf(it)] }
        val filteredProductDiscounts = filteredProductIds.map { productDiscounts[productIds.indexOf(it)] }
        val filteredProductDiscountedPrices = filteredProductIds.map { productDiscountedPrices[productIds.indexOf(it)] }
        val filteredProductRatings = filteredProductIds.map { productRatings[productIds.indexOf(it)] }
        val filteredProductRatingCounts = filteredProductIds.map { productRatingCounts[productIds.indexOf(it)] }
        val filteredProductStock = filteredProductIds.map { productStock[productIds.indexOf(it)] }

        productIds = filteredProductIds
        productMainImages = filteredProductMainImages
        productNames = filteredProductNames
        productRealPrices = filteredProductRealPrices
        productDiscounts = filteredProductDiscounts
        productDiscountedPrices = filteredProductDiscountedPrices
        productRatings = filteredProductRatings
        productRatingCounts = filteredProductRatingCounts
        productStock = filteredProductStock

        notifyDataSetChanged()
    }

    private fun productNameContainsQuery(productId: String, query: String): Boolean {
        val productName = productNames[productIds.indexOf(productId)]
        return productName.contains(query, ignoreCase = true)
    }

    public fun updateData(sortBy : String, category : String, outOfStock : Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val result = db.collection("products").get().await()

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
                    productStock += document.getLong("productStock").toString()
                }

                val sortedProductIds = when(sortBy){
                    "PriceLowToHigh" -> {
                        productIds.sortedBy { productRealPrices[productIds.indexOf(it)].toInt() }
                    }
                    "PriceHighToLow" -> {
                        productIds.sortedByDescending { productRealPrices[productIds.indexOf(it)].toInt() }
                    }
                    "RatingLowToHigh" -> {
                        productIds.sortedBy { productRatings[productIds.indexOf(it)].toFloat() }
                    }
                    "RatingHighToLow" -> {
                        productIds.sortedByDescending { productRatings[productIds.indexOf(it)].toFloat() }
                    }
                    "DiscountLowToHigh" -> {
                        productIds.sortedBy { productDiscounts[productIds.indexOf(it)].toInt() }
                    }
                    "DiscountHighToLow" -> {
                        productIds.sortedByDescending { productDiscounts[productIds.indexOf(it)].toInt() }
                    }
                    else -> {
                        productIds
                    }
                }

                productIds = sortedProductIds

                if (category != "All Categories"){
                    productIds = productIds.filter { category == db.collection("products").document(it).get().await().getString("productCategory").toString() }
                }

                if (outOfStock){
                    productIds = productIds.filter { productStock[productIds.indexOf(it)].toInt() == 0 }
                }

                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                }

            }
            catch (e: Exception) {
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }

    public fun updateData(sortBy : String, outOfStock : Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val result = db.collection("products").get().await()

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
                    productStock += document.getLong("productStock").toString()
                }

                val sortedProductIds = when(sortBy){
                    "PriceLowToHigh" -> {
                        productIds.sortedBy { productRealPrices[productIds.indexOf(it)].toInt() }
                    }
                    "PriceHighToLow" -> {
                        productIds.sortedByDescending { productRealPrices[productIds.indexOf(it)].toInt() }
                    }
                    "RatingLowToHigh" -> {
                        productIds.sortedBy { productRatings[productIds.indexOf(it)].toFloat() }
                    }
                    "RatingHighToLow" -> {
                        productIds.sortedByDescending { productRatings[productIds.indexOf(it)].toFloat() }
                    }
                    "DiscountLowToHigh" -> {
                        productIds.sortedBy { productDiscounts[productIds.indexOf(it)].toInt() }
                    }
                    "DiscountHighToLow" -> {
                        productIds.sortedByDescending { productDiscounts[productIds.indexOf(it)].toInt() }
                    }
                    else -> {
                        productIds
                    }
                }

                productIds = sortedProductIds

                if (outOfStock){
                    productIds = productIds.filter { productStock[productIds.indexOf(it)].toInt() == 0 }
                }

                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                }

            }
            catch (e: Exception){
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }

}