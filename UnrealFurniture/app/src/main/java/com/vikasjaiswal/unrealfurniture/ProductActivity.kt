package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
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
import com.colormoon.readmoretextview.ReadMoreTextView
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.util.regex.Pattern

class ProductActivity : AppCompatActivity() {

    lateinit var networkReceiver : CheckConnectivity

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    lateinit var skeletonLayout: SkeletonLayout

    private lateinit var goBack: CardView
    private lateinit var open3DView: CardView
    private lateinit var ModelUrl : String

    private lateinit var productMainImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productDescription: ReadMoreTextView
    private lateinit var prodcutDiscount: TextView
    private lateinit var productRealPrice: TextView
    private lateinit var productDiscountedPrice: TextView
    private lateinit var productLookImage: ImageView
    private lateinit var productDimenImage: ImageView
    private lateinit var productStock : TextView

    private lateinit var productDimensions: TableLayout
    private lateinit var productRatingBar: RatingBar
    private lateinit var productRatingCount: TextView

    private lateinit var productRatingReviewEdit : TextView

    private lateinit var editProductDetails : CardView

    lateinit var addToWishList : ExpressView

    private lateinit var addToCard: TextView
    private lateinit var buyNow: TextView

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var ratingReviewsLayoutManager: GridLayoutManager
    private var ratingReviewsAdapter: RatingReviewsRecAdapter? = null

    private lateinit var ratingReviewsRecyclerView: RecyclerView

    var mSession: Session? = null
    private var M_USER_REQUEST_INSTALL = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_activity)

        networkReceiver = CheckConnectivity()

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
        productStock = findViewById(R.id.inStock)

        productRatingReviewEdit = findViewById(R.id.productRatingReviewsEdit)

        editProductDetails = findViewById(R.id.editProductDetails)

        productRealPrice.paint.isStrikeThruText = true

        addToCard = findViewById(R.id.addToCart)
        buyNow = findViewById(R.id.buyNow)

        addToWishList = findViewById(R.id.addToWishList)

        ratingReviewsRecyclerView = findViewById(R.id.ratingReviewsRecyclerView)
        ratingReviewsLayoutManager = GridLayoutManager(this, 1)
        ratingReviewsRecyclerView.layoutManager = ratingReviewsLayoutManager

        ratingReviewsAdapter = RatingReviewsRecAdapter{

        }

        ratingReviewsRecyclerView.adapter = ratingReviewsAdapter

        ratingReviewsAdapter?.updateData(intent.extras?.getString("productId").toString())

        if (auth.currentUser?.email == "unrealadmin@gmail.com"){
            addToCard.isEnabled = false
            buyNow.isEnabled = false
            addToWishList.isEnabled = false
            editProductDetails.visibility = View.VISIBLE
        }

        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.editproductdetails_bottomresource, null)
        bottomSheetDialog.setContentView(bottomSheetView)
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

        addToCard.setOnClickListener {
            addToCart()
        }

        buyNow.setOnClickListener {
            buyNow()
        }

        open3DView.setOnClickListener {

            if (!CameraPermissionHelper.hasCameraPermission(this)) {
                CameraPermissionHelper.requestCameraPermission(this)
                return@setOnClickListener
            }

            try {
                if (mSession == null) {
                    when (ArCoreApk.getInstance()
                        .requestInstall(this, M_USER_REQUEST_INSTALL)) {
                        ArCoreApk.InstallStatus.INSTALLED -> {
                            mSession = Session(this)
                        }
                        ArCoreApk.InstallStatus.INSTALL_REQUESTED -> M_USER_REQUEST_INSTALL = false
                        else -> M_USER_REQUEST_INSTALL = false
                    }
                }
            } catch (e: UnavailableUserDeclinedInstallationException) {
                Toast.makeText(this, "Please Install AR Core", Toast.LENGTH_LONG).show()
            } catch (e: UnavailableArcoreNotInstalledException) {
                Toast.makeText(
                    this,
                    "AR Core Not Installed",
                    Toast.LENGTH_LONG
                ).show()
            }

            val sceneViewerIntent = Intent(Intent.ACTION_VIEW)
            sceneViewerIntent.data =
                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                    .appendQueryParameter(
                        "file",
                        ModelUrl
                    ).appendQueryParameter("title", productName.text.toString()).build()
            sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox")
            startActivity(sceneViewerIntent)
        }


        editProductDetails.setOnClickListener {
            bottomSheetDialog.show()

            val editProductCategory = bottomSheetDialog.findViewById<AutoCompleteTextView>(R.id.editProductCategory)
            editProductCategory?.setAdapter(
                ArrayAdapter(
                    this@ProductActivity,
                    android.R.layout.simple_list_item_1,
                    resources.getStringArray(R.array.product_categories)
                )
            )

            val editProductName = bottomSheetDialog.findViewById<EditText>(R.id.editProductName)
            val editProductDescription = bottomSheetDialog.findViewById<EditText>(R.id.editProductDescription)
            val editProductPrice = bottomSheetDialog.findViewById<EditText>(R.id.editProductPrice)
            val editProductDiscount = bottomSheetDialog.findViewById<EditText>(R.id.editProductDiscount)
            val editProductStock = bottomSheetDialog.findViewById<EditText>(R.id.editProductStock)

            val updateProduct = bottomSheetDialog.findViewById<Button>(R.id.updateProduct)

            CoroutineScope(Dispatchers.IO).launch {
                val result = db.collection("products").document(intent.extras?.getString("productId").toString()).get().await()
                withContext(Dispatchers.Main) {
                    editProductCategory?.setText(result.getString("productCategory"))
                    editProductName?.setText(result.getString("productName"))
                    editProductDescription?.setText(result.getString("productDescription"))
                    editProductPrice?.setText(result.getLong("productPrice").toString())
                    editProductDiscount?.setText(result.getLong("productDiscount").toString())
                    editProductStock?.setText(result.getLong("productStock").toString())
                }
            }

            updateProduct?.setOnClickListener {
                val productCategory = editProductCategory?.text.toString()
                val productName = editProductName?.text.toString()
                val productDescription = editProductDescription?.text.toString()
                val productPrice = editProductPrice?.text.toString()
                val productDiscount = editProductDiscount?.text.toString()
                val productStock = editProductStock?.text.toString()

                if (productCategory.isBlank()){
                    editProductCategory?.error = "Category Required"
                    editProductCategory?.requestFocus()
                    return@setOnClickListener
                }

                if (productName.isBlank() || !Pattern.matches("^(?!\\s)[a-zA-Z0-9\\s]{2,}$", productName)){
                    editProductName?.error = "Name Required"
                    editProductName?.requestFocus()
                    return@setOnClickListener
                }

                if (productDescription.isBlank() || productDescription.length < 10){
                    editProductDescription?.error = "Description Required"
                    editProductDescription?.requestFocus()
                    return@setOnClickListener
                }

                if (productPrice.isBlank() || productPrice.toInt() <= 0){
                    editProductPrice?.error = "Price Required"
                    editProductPrice?.requestFocus()
                    return@setOnClickListener
                }

                if (productDiscount.isBlank() || productDiscount.toInt() <= 0 || productDiscount.toInt() >= 100){
                    editProductDiscount?.error = "Discount Required"
                    editProductDiscount?.requestFocus()
                    return@setOnClickListener
                }

                if (productStock.isBlank() || productStock.toInt() <= 0){
                    editProductStock?.error = "Stock Required"
                    editProductStock?.requestFocus()
                    return@setOnClickListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val result = db.collection("products").document(intent.extras?.getString("productId").toString()).get().await()
                        db.collection("products").document(intent.extras?.getString("productId").toString()).update(
                            mapOf(
                                "productCategory" to productCategory,
                                "productName" to productName,
                                "productDescription" to productDescription,
                                "productPrice" to productPrice.toInt(),
                                "productDiscount" to productDiscount.toInt(),
                                "productStock" to productStock.toInt()
                            )
                        ).addOnSuccessListener {
                            runOnUiThread {
                                Toast.makeText(this@ProductActivity, "Product Updated", Toast.LENGTH_SHORT).show()
                                bottomSheetDialog.dismiss()
                            }
                        }.addOnFailureListener {
                            runOnUiThread {
                                Toast.makeText(this@ProductActivity, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Log.d("ProductActivity", "Error: ${e.message}")
                            Toast.makeText(this@ProductActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }
        }
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
                    productDescription.text = result.getString("productDescription").toString()
                    productRealPrice.text = "₹${result.getLong("productPrice").toString()}"
                    prodcutDiscount.text = "${result.getLong("productDiscount").toString()}% ↓"
                    productDiscountedPrice.text = "₹${result.getLong("productDiscountedPrice").toString()}"
                    productRatingBar.rating = result.getDouble("prodRating")!!.toFloat()
                    productRatingCount.text = "("+result.getLong("prodRatingCount").toString()+")"

                    ModelUrl = result.getString("prod3DModel").toString()

                    if (result.getLong("productStock")!! <= 0L){
                        productStock.text = "Out of Stock"
                        productStock.setTextColor(ContextCompat.getColor(this@ProductActivity, R.color.red))
                    }else{
                        productStock.text = "In Stock"
                        productStock.setTextColor(ContextCompat.getColor(this@ProductActivity, R.color.dark))
                    }

                    productRatingReviewEdit.text = "${result.getDouble("prodRating")!!.toFloat()} ★ (${result.getLong("prodRatingCount").toString()})"

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

//                    val ratingReviews = result.get("prodRatingReviews") as List<*>
//                    ratingReviewsAdapter?.setData(ratingReviews)
                }
            } catch (e: Exception) {
                Log.d("ProductActivity", "Error: ${e.toString()}")
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

    private fun buyNow(){
        db.collection("users").document(auth.currentUser?.uid.toString()).collection("addresses").get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                MaterialAlertDialogBuilder(this@ProductActivity)
                    .setTitle("No Address Found")
                    .setMessage("Please add an address to continue")
                    .setNeutralButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else {
                val prodid = intent.extras?.getString("productId")

                val intent = Intent(this@ProductActivity, CheckoutActivity::class.java)
                intent.putExtra("type", "buyNow")
                intent.putExtra("productId", prodid)
                startActivity(intent)
            }
        }
    }

    private fun addToCart(){
        CoroutineScope(Dispatchers.IO).launch {
            val user = auth.currentUser
            if (user != null) {
                val userRef = db.collection("users").document(user.uid)
                userRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        val cart = document.get("cartList") as? List<String> ?: emptyList()
                        if (cart.contains(intent.extras?.getString("productId"))) {
                            Toast.makeText(this@ProductActivity, "Already in Cart", Toast.LENGTH_SHORT).show()
                        } else {
                            userRef.update("cartList", cart.plus(intent.extras?.getString("productId")))
                                .addOnSuccessListener {
                                    Toast.makeText(this@ProductActivity, "Added to Cart", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(
                this,
                "Permission Required",
                Toast.LENGTH_LONG
            ).show()
//            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
//                CameraPermissionHelper.launchPermissionSettings(this)
//            }
        }
    }
}












