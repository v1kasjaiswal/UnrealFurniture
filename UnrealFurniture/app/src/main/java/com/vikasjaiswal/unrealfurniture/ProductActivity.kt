package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.ankurg.expressview.ExpressView
import com.bumptech.glide.Glide
import co.ankurg.expressview.OnCheckListener
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProductActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    lateinit var skeletonLayout: SkeletonLayout

    private lateinit var goBack: CardView
    private lateinit var open3DView: CardView

    private lateinit var productMainImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productDescription: TextView
    private lateinit var prodcutDiscount: TextView
    private lateinit var productRealPrice: TextView
    private lateinit var productDiscountedPrice: TextView
    private lateinit var productLookImage: ImageView
    private lateinit var productDimenImage: ImageView

    private lateinit var productDimensions: TableLayout
    private lateinit var productRatingBar: RatingBar
    private lateinit var productRatingCount: TextView

    lateinit var addToWishList : ExpressView

    private lateinit var addToCard: TextView
    private lateinit var buyNow: TextView

    private lateinit var ratingReviewsLayoutManager: GridLayoutManager
    private var ratingReviewsAdapter: RatingReviewsRecAdapter? = null

    private lateinit var ratingReviewsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_activity)

        initializeViews()
        setClickListeners()

        skeletonLayout.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            val bundle = intent.extras
            val productId = bundle?.getString("productId")

            loadProductData(productId!!)

            checkProductInWishList()
            skeletonLayout.showOriginal()
        }, 1500)
    }

    private fun initializeViews() {

        skeletonLayout = findViewById(R.id.skeletonLayout)

        goBack = findViewById(R.id.goBack)
        open3DView = findViewById(R.id.open3DARView)

        productMainImage = findViewById(R.id.productMainImage)
        productName = findViewById(R.id.productNameEdit)
        productDescription = findViewById(R.id.productDescriptionEdit)
        prodcutDiscount = findViewById(R.id.productDiscount)
        productRealPrice = findViewById(R.id.productRealPrice)
        productDiscountedPrice = findViewById(R.id.productDiscountedPrice)
        productLookImage = findViewById(R.id.productLookImageEdit)
        productDimenImage = findViewById(R.id.productDimenImageEdit)
        productDimensions = findViewById(R.id.dimenTableLayout)
        productRatingBar = findViewById(R.id.productRatingBarEdit)
        productRatingCount = findViewById(R.id.productRatingCountEdit)

        productRealPrice.paint.isStrikeThruText = true

        addToCard = findViewById(R.id.addToCart)
        buyNow = findViewById(R.id.buyNow)

        addToWishList = findViewById(R.id.addToWishList)

        ratingReviewsRecyclerView = findViewById(R.id.ratingReviewsRecyclerView)
        ratingReviewsLayoutManager = GridLayoutManager(this, 1)
        ratingReviewsRecyclerView.layoutManager = ratingReviewsLayoutManager

        ratingReviewsAdapter = RatingReviewsRecAdapter()
        ratingReviewsRecyclerView.adapter = ratingReviewsAdapter
    }

    private fun setClickListeners() {
        goBack.setOnClickListener { finish() }

        addToWishList.setOnCheckListener (object : OnCheckListener {
            override fun onChecked(view: ExpressView?) {
                addToWishList()
            }

            override fun onUnChecked(view: ExpressView?) {
                removeFromWishList()
            }
        })

        addToCard.setOnClickListener { Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show() }

        buyNow.setOnClickListener { Toast.makeText(this, "Buying Now", Toast.LENGTH_SHORT).show() }
    }

    private fun loadProductData(productId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("products").document(productId).get().await()

                withContext(Dispatchers.Main) {

                    lifecycleScope.launchWhenCreated {
                    Picasso
                        .get()
                        .load(result.getString("prodMainImage").toString())
                        .placeholder(R.drawable.blank)
                        .into(productMainImage)
                    }

                    productName.text = result.getString("productName").toString()
//                    productDescription.text = result.getString("prodDescription").toString()
                    productRealPrice.text = "₹${result.getLong("productPrice").toString()}"
                    prodcutDiscount.text = "${result.getLong("productDiscount").toString()}% ↓"
                    productDiscountedPrice.text = "₹${result.getLong("productDiscountedPrice").toString()}"

                    lifecycleScope.launchWhenCreated {
                        Picasso
                            .get()
                            .load(result.getString("prodLookImage").toString())
                            .placeholder(R.drawable.blank)
                            .into(productLookImage)

                    }

                    lifecycleScope.launchWhenCreated {
                        Picasso
                            .get()
                            .load(result.getString("prodDimenImage").toString())
                            .placeholder(R.drawable.blank)
                            .into(productDimenImage)
                    }

                    val dimenData = result.get("prodDimensions") as Map<*, *>
                    for ((key, value) in dimenData) {
                        val row = TableRow(this@ProductActivity)
                        val param = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
                        row.layoutParams = param

                        val keyView = TextView(this@ProductActivity)
                        keyView.text = key.toString()
                        keyView.setTextColor(ContextCompat.getColor(this@ProductActivity, R.color.dark))
                        keyView.textSize = 15f
                        keyView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        keyView.typeface = ResourcesCompat.getFont(this@ProductActivity, R.font.satoshibold)
                        keyView.setPadding(10, 10, 10, 10)
                        row.addView(keyView)

                        val valueView = TextView(this@ProductActivity)
                        valueView.text = value.toString()
                        valueView.setTextColor(ContextCompat.getColor(this@ProductActivity, R.color.dark))
                        valueView.textSize = 15f
                        valueView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        valueView.typeface = ResourcesCompat.getFont(this@ProductActivity, R.font.satoshibold)
                        valueView.setPadding(10, 10, 10, 10)
                        row.addView(valueView)

                        productDimensions.addView(row)
                    }

//                    productRatingBar.rating = result.getString("prodRating").toString().toFloat()
//                    productRatingCount.text = "(${result.getString("prodRatingCount").toString()})"

//                    val ratingReviews = result.get("prodRatingReviews") as List<*>
//                    ratingReviewsAdapter?.setData(ratingReviews)
                }
            } catch (e: Exception) {
                Log.d("ProductActivity", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addToWishList() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(intent.extras?.getString("productId"))) {
                            Toast.makeText(this@ProductActivity, "Already in WishList", Toast.LENGTH_SHORT).show()
                        } else {
                            userRef.update("wishList", wishList.plus(intent.extras?.getString("productId")))
                                .addOnSuccessListener {
                                    Toast.makeText(this@ProductActivity, "Added to WishList", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Log.d("ProductActivity", "Error: ${it.message}")
                                    Toast.makeText(this@ProductActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }
        }
    }

    private fun removeFromWishList() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(intent.extras?.getString("productId"))) {
                            userRef.update("wishList", wishList.minus(intent.extras?.getString("productId")))
                                .addOnSuccessListener {
                                    Toast.makeText(this@ProductActivity, "Removed from WishList", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Log.d("ProductActivity", "Error: ${it.message}")
                                    Toast.makeText(this@ProductActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this@ProductActivity, "Not in WishList", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun checkProductInWishList() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val wishList = document.get("wishList") as? List<String> ?: emptyList()
                        if (wishList.contains(intent.extras?.getString("productId"))) {
                            addToWishList.isChecked = true
                        }
                    }
                }
            }
        }
    }
}












//        open3DView.setOnClickListener {
//            try {
//                val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
//                val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
//                    .appendQueryParameter(
//                        "file",
//                        "https://firebasestorage.googleapis.com/v0/b/unreal-furniture.appspot.com/o/product3DModels%2Fsofa1.fbx?alt=media&token=c9529c54-144a-4b10-8943-a42ecab4774c"
//                    )
//                    .appendQueryParameter("mode", "3d_only")
//                    .build()
//
//                sceneViewerIntent.data = intentUri
//                sceneViewerIntent.setPackage("com.google.ar.core")
//
//                if (sceneViewerIntent.resolveActivity(packageManager) != null) {
//                    startActivity(sceneViewerIntent)
//                } else {
//                    Toast.makeText(
//                        this@ProductActivity,
//                        "AR Scene Viewer not installed or supported on this device.",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } catch (e: Exception) {
//                Log.d("ProductActivity", "Error: ${e.message}")
//                Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }

