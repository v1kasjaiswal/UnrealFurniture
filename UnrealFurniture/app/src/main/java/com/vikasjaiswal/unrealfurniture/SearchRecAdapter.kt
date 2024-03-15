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

class SearchRecAdapter(private val onDataChanged: () -> Unit)  : RecyclerView.Adapter<SearchRecAdapter.ViewHolder>() {

    var productIds = listOf<String>()
    var productCategories = listOf<String>()
    var productMainImages = listOf<String>()
    var productNames = listOf<String>()
    var productRealPrices = listOf<Int>()
    var productDiscounts = listOf<Int>()
    var productDiscountedPrices = listOf<Int>()
    var productRatings = listOf<Float>()
    var productRatingCounts = listOf<Int>()
    var productStocks = listOf<Int>()

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    init {
        updateData()
    }

    fun setData(
        ids : List<String>,
        categories : List<String>,
        mainImages : List<String>,
        names : List<String>,
        realPrices : List<Int>,
        discounts : List<Int>,
        discountedPrices : List<Int>,
        ratings : List<Float>,
        ratingCounts : List<Int>,
        stocks : List<Int>
    ){
        productIds = ids
        productCategories = categories
        productMainImages = mainImages
        productNames = names
        productRealPrices = realPrices
        productDiscounts = discounts
        productDiscountedPrices = discountedPrices
        productRatings = ratings
        productRatingCounts = ratingCounts
        productStocks = stocks
        notifyDataSetChanged()
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
        holder.rating.rating = productRatings[position]
        holder.ratingCount.text = "(${productRatingCounts[position]})"

        holder.selectedProductCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductActivity::class.java)
            intent.putExtra("productId", productIds[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun clearData(){
        productIds = emptyList()
        productCategories = emptyList()
        productMainImages = emptyList()
        productNames = emptyList()
        productRealPrices = emptyList()
        productDiscounts = emptyList()
        productDiscountedPrices = emptyList()
        productRatings = emptyList()
        productRatingCounts = emptyList()
        productStocks = emptyList()
    }

    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val result = db.collection("products").get().await()

                clearData()

                for (document in result) {
                    productIds += document.id
                    productMainImages += document.getString("prodMainImage").toString()
                    productNames += document.getString("productName").toString()
                    productCategories += document.getString("productCategory").toString()
                    productRealPrices += document.getLong("productPrice")?.toInt()?: 0
                    productDiscounts += document.getLong("productDiscount")?.toInt()?:0
                    productDiscountedPrices += document.getLong("productDiscountedPrice")?.toInt()?:0
                    productRatings += document.getDouble("prodRating")?.toFloat()?:0f
                    productRatingCounts += document.getLong("prodRatingCount")?.toInt()?:0
                    productStocks += document.getLong("productStock")?.toInt()?:0
                }

                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }
            }catch (e: Exception){
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }

    fun updateData(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("products").get().await()

                val filteredProducts = result.filter { document ->
                    val productName = document.getString("productName").toString()
                    productName.contains(query, ignoreCase = true)
                }

                withContext(Dispatchers.Main) {
                    val productIds = filteredProducts.map { it.id }
                    val productNames = filteredProducts.map { it.getString("productName").toString() }
                    val productCategories = filteredProducts.map { it.getString("productCategory").toString() }
                    val productMainImages = filteredProducts.map { it.getString("prodMainImage").toString() }
                    val productRealPrices = filteredProducts.map { it.getLong("productPrice")?.toInt() ?: 0 }
                    val productDiscounts = filteredProducts.map { it.getLong("productDiscount")?.toInt() ?: 0 }
                    val productDiscountedPrices = filteredProducts.map { it.getLong("productDiscountedPrice")?.toInt() ?: 0 }
                    val productRatings = filteredProducts.map { it.getDouble("prodRating")?.toFloat() ?: 0f }
                    val productRatingCounts = filteredProducts.map { it.getLong("prodRatingCount")?.toInt() ?: 0 }
                    val productStocks = filteredProducts.map { it.getLong("productStock")?.toInt() ?: 0 }

                    setData(
                        productIds,
                        productCategories,
                        productMainImages,
                        productNames,
                        productRealPrices,
                        productDiscounts,
                        productDiscountedPrices,
                        productRatings,
                        productRatingCounts,
                        productStocks
                    )
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }

            } catch (e: Exception) {
                Log.e("TAG111", "Error updating data: ${e.message}", e)
            }
        }
    }

    public fun updateData(sortBy: String, outOfStock: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("products").get().await()

                clearData()

                val sortedProductIds = when (sortBy) {
                    "PriceLowToHigh" -> {
                        result.documents.sortedBy { it.getLong("productPrice")?.toInt() ?: 0 }
                    }
                    "PriceHighToLow" -> {
                        result.documents.sortedByDescending { it.getLong("productPrice")?.toInt() ?: 0 }
                    }
                    "RatingLowToHigh" -> {
                        result.documents.sortedBy { it.getDouble("prodRating")?.toFloat() ?: 0f }
                    }
                    "RatingHighToLow" -> {
                        result.documents.sortedByDescending { it.getDouble("prodRating")?.toFloat() ?: 0f }
                    }
                    "DiscountLowToHigh" -> {
                        result.documents.sortedBy { it.getLong("productDiscount")?.toInt() ?: 0 }
                    }
                    "DiscountHighToLow" -> {
                        result.documents.sortedByDescending { it.getLong("productDiscount")?.toInt() ?: 0 }
                    }
                    else -> {
                        result.documents
                    }
                }
                .map { it.id }

                if (outOfStock) {
                    sortedProductIds.filter { id -> result.documents.first { it.id == id }.getLong("productStock")?.toInt() == 0 }
                }

                productIds = sortedProductIds
                productNames = sortedProductIds.map { id -> result.documents.first { it.id == id }.getString("productName").toString() }
                productMainImages = sortedProductIds.map { id -> result.documents.first { it.id == id }.getString("prodMainImage").toString() }
                productCategories = sortedProductIds.map { id -> result.documents.first { it.id == id }.getString("productCategory").toString() }
                productRealPrices = sortedProductIds.map { id -> result.documents.first { it.id == id }.getLong("productPrice")?.toInt() ?: 0 }
                productDiscounts = sortedProductIds.map { id -> result.documents.first { it.id == id }.getLong("productDiscount")?.toInt() ?: 0 }
                productDiscountedPrices = sortedProductIds.map { id -> result.documents.first { it.id == id }.getLong("productDiscountedPrice")?.toInt() ?: 0 }
                productRatings = sortedProductIds.map { id -> result.documents.first { it.id == id }.getDouble("prodRating")?.toFloat() ?: 0f }
                productRatingCounts = sortedProductIds.map { id -> result.documents.first { it.id == id }.getLong("prodRatingCount")?.toInt() ?: 0 }
                productStocks = sortedProductIds.map { id -> result.documents.first { it.id == id }.getLong("productStock")?.toInt() ?: 0 }

                withContext(Dispatchers.Main) {
                    Log.d("TAG111", "updateData: $productIds")
                    Log.d("ProdNames", "updateData: $productNames")

                    setData(productIds, productCategories, productMainImages, productNames, productRealPrices, productDiscounts, productDiscountedPrices, productRatings, productRatingCounts, productStocks)
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }
            } catch (e: Exception) {
                Log.d("TAG111", "updateData: ${e.message}")
            }
        }
    }

    public fun updateData(sortBy: String, category: String, outOfStock: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("products").get().await()

                val filteredProducts = result.filter { document ->
                    val productCategory = document.getString("productCategory").toString()
                    category == "All" || productCategory == category
                }

                val sortedProducts = when (sortBy) {
                    "PriceLowToHigh" -> filteredProducts.sortedBy { it.getLong("productPrice") ?: 0 }
                    "PriceHighToLow" -> filteredProducts.sortedByDescending { it.getLong("productPrice") ?: 0 }
                    "RatingLowToHigh" -> filteredProducts.sortedBy { it.getDouble("prodRating")?.toFloat() ?: 0f }
                    "RatingHighToLow" -> filteredProducts.sortedByDescending { it.getDouble("prodRating")?.toFloat() ?: 0f }
                    "DiscountLowToHigh" -> filteredProducts.sortedBy { it.getLong("productDiscount") ?: 0 }
                    "DiscountHighToLow" -> filteredProducts.sortedByDescending { it.getLong("productDiscount") ?: 0 }
                    else -> filteredProducts
                }

                val finalProducts = if (outOfStock) {
                    sortedProducts.filter { it.getLong("productStock") == 0L }
                } else {
                    sortedProducts
                }

                withContext(Dispatchers.Main) {
                    val productIds = finalProducts.map { it.id }
                    val productNames = finalProducts.map { it.getString("productName").toString() }
                    val productCategories = finalProducts.map { it.getString("productCategory").toString() }
                    val productMainImages = finalProducts.map { it.getString("prodMainImage").toString() }
                    val productRealPrices = finalProducts.map { it.getLong("productPrice")?.toInt() ?: 0 }
                    val productDiscounts = finalProducts.map { it.getLong("productDiscount")?.toInt() ?: 0 }
                    val productDiscountedPrices = finalProducts.map { it.getLong("productDiscountedPrice")?.toInt() ?: 0 }
                    val productRatings = finalProducts.map { it.getDouble("prodRating")?.toFloat() ?: 0f }
                    val productRatingCounts = finalProducts.map { it.getLong("prodRatingCount")?.toInt() ?: 0 }
                    val productStocks = finalProducts.map { it.getLong("productStock")?.toInt() ?: 0 }

                    setData(
                        productIds,
                        productCategories,
                        productMainImages,
                        productNames,
                        productRealPrices,
                        productDiscounts,
                        productDiscountedPrices,
                        productRatings,
                        productRatingCounts,
                        productStocks
                    )
                    notifyDataSetChanged()
                    onDataChanged.invoke()
                }

            } catch (e: Exception) {
                Log.e("TAG111", "Error updating data: ${e.message}", e)
            }
        }
    }

}