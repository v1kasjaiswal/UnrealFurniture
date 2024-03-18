package com.vikasjaiswal.unrealfurniture

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat

class CheckoutActivity : AppCompatActivity() {

    lateinit var networkReceiver : CheckConnectivity

    private lateinit var checkoutAddressLayoutManager: LinearLayoutManager
    private lateinit var checkoutProductLayoutManager: GridLayoutManager

    private var checkoutAddressAdapter: CheckoutAddressRecAdapter? = null
    private var checkoutProductAdapter: CheckoutProductRecAdapter? = null

    private lateinit var checkoutAddressRecyclerView: RecyclerView
    private lateinit var checkoutProductRecyclerView: RecyclerView

    lateinit var overAllRealPrice : TextView
    lateinit var overAllDiscountedPrice : TextView
    lateinit var overAllDiscount : TextView
    lateinit var grandTotal : TextView

    lateinit var paymentMethod : AutoCompleteTextView

    lateinit var confirmOrder : Button

    lateinit var goBack : CardView

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    var user = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.checkout_activity)

        networkReceiver = CheckConnectivity()

        checkoutAddressRecyclerView = findViewById(R.id.checkoutAddressRecyclerView)
        checkoutProductRecyclerView = findViewById(R.id.checkoutProductRecyclerView)

        checkoutAddressLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        checkoutProductLayoutManager = GridLayoutManager(this, 2)

        checkoutAddressRecyclerView.layoutManager = checkoutAddressLayoutManager
        checkoutProductRecyclerView.layoutManager = checkoutProductLayoutManager

        checkoutAddressAdapter = CheckoutAddressRecAdapter{
            updateEmptyViewVisibility()
        }
        checkoutProductAdapter = CheckoutProductRecAdapter{
            updateEmptyViewVisibility()
        }

        checkoutAddressRecyclerView.adapter = checkoutAddressAdapter
        checkoutProductRecyclerView.adapter = checkoutProductAdapter

        paymentMethod = findViewById(R.id.paymentMethod)

        confirmOrder = findViewById(R.id.confirmOrder)

        goBack = findViewById(R.id.goBack)

        paymentMethod.setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.payment_methods)
            )
        )

        goBack.setOnClickListener {
            finish()
        }

        var type = intent.getStringExtra("type")
        var prodId = intent.extras?.getString("productId")

        Log.d("TAXG", "$prodId  onCreate: $type")

        Log.d("TAXG", "onCreate: $type")
        if (type == "buyNow" && prodId!= null){
            CoroutineScope(Dispatchers.IO).launch {
                db.collection("products").document(prodId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            var prodIds = ArrayList<String>()
                            var prodImages = ArrayList<String>()
                            var prodNames = ArrayList<String>()
                            var prodPrices = ArrayList<Int>()
                            var prodDiscounts = ArrayList<Int>()
                            var prodDiscountedPrices = ArrayList<Int>()
                            var prodQuantities = ArrayList<Int>()
                            var prodRatings = ArrayList<String>()
                            var prodRatingCounts = ArrayList<Int>()

                            prodIds.add(document.id)
                            prodImages.add(document.data?.get("prodMainImage").toString())
                            prodNames.add(document.data?.get("productName").toString())
                            prodPrices.add(document.data?.get("productPrice").toString().toInt())
                            prodDiscounts.add(document.data?.get("productDiscount").toString().toInt())
                            prodDiscountedPrices.add(document.data?.get("productDiscountedPrice").toString().toInt())
                            prodQuantities.add(1)
                            prodRatings.add(document.data?.get("prodRating").toString())
                            prodRatingCounts.add(document.data?.get("prodRatingCount").toString().toInt())

                            checkoutProductAdapter?.setData(prodIds, prodImages, prodNames, prodPrices, prodDiscounts, prodDiscountedPrices, prodQuantities, prodRatings, prodRatingCounts)

                            overAllRealPrice = findViewById(R.id.overAllRealPrice)
                            overAllDiscountedPrice = findViewById(R.id.overAllDiscountedPrice)
                            overAllDiscount = findViewById(R.id.overAllDiscount)
                            grandTotal = findViewById(R.id.grandTotal)

                            overAllRealPrice.text = "₹${prodPrices[0]}"
                            overAllDiscountedPrice.text = "₹${prodDiscountedPrices[0]}"
                            overAllDiscount.text = "${prodDiscounts[0]}% ↓"
                            var total = prodDiscountedPrices[0].toInt() + 500
                            grandTotal.text = "₹$total"
                        } else {
                            Log.d("TAG", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("TAG", "get failed with ", exception)
                    }
            }
        }

        Log.d("TAXGGG", "onCreate: $type")

        if (type == "cart"){

            val prodIds = intent.getStringArrayListExtra("prodIds") ?: ArrayList()
            val prodImages = intent.getStringArrayListExtra("prodImages") ?: ArrayList()
            val prodNames = intent.getStringArrayListExtra("prodNames") ?: ArrayList()
            val prodPrices = intent.getIntegerArrayListExtra("prodPrices") ?: ArrayList()
            val prodDiscounts = intent.getIntegerArrayListExtra("prodDiscounts") ?: ArrayList()
            val prodDiscountedPrices = intent.getIntegerArrayListExtra("prodDiscountedPrices") ?: ArrayList()
            val prodQuantities = intent.getIntegerArrayListExtra("prodQuantities") ?: ArrayList()
            val prodRatings = intent.getStringArrayListExtra("prodRatings") ?: ArrayList()
            Log.d("TAXGGGtyt", "onCreate: $prodRatings")
            val prodRatingCounts = intent.getIntegerArrayListExtra("prodRatingCounts") ?: ArrayList()
            val overAllRealPrice = intent.getIntExtra("overAllRealPrice", 0)
            val overAllDiscountedPrice = intent.getIntExtra("overAllDiscountedPrice", 0)
            val overAllDiscount = intent.getIntExtra("overAllDiscount", 0)

            Log.d("TAXGGG", "onCreate: $prodIds")
            Log.d("TAXGGG", "onCreate: $prodImages")

            checkoutProductAdapter?.setData(prodIds, prodImages, prodNames, prodPrices, prodDiscounts, prodDiscountedPrices, prodQuantities, prodRatings, prodRatingCounts)

            this.overAllRealPrice = findViewById(R.id.overAllRealPrice)
            this.overAllDiscountedPrice = findViewById(R.id.overAllDiscountedPrice)
            this.overAllDiscount = findViewById(R.id.overAllDiscount)
            this.grandTotal = findViewById(R.id.grandTotal)

            this.overAllRealPrice.text = "₹$overAllRealPrice"
            this.overAllDiscountedPrice.text = "₹$overAllDiscountedPrice"
            this.overAllDiscount.text = "$overAllDiscount% ↓"
            var total = overAllDiscountedPrice + 500
            this.grandTotal.text = "₹$total"
        }

        confirmOrder.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Confirm Order")
                .setMessage("Are you sure you want to confirm the order?")
                .setPositiveButton("Yes") { dialog, which ->

                    val prodIds = checkoutProductAdapter?.prodIds ?: emptyList()
                    val prodQuantities = checkoutProductAdapter?.prodQuantities ?: emptyList()

                    var outOfStockProducts = mutableListOf<String>()

                    var count = 0 // Counter to track number of products checked

                    for (i in prodIds.indices) {
                        db.collection("products").document(prodIds[i]).get()
                            .addOnSuccessListener { document ->
                                val quantity = document.get("productStock") as? Long ?: 0
                                val requestedQuantity = prodQuantities.getOrNull(i) ?: 0

                                if (requestedQuantity > quantity.toInt()) {
                                    outOfStockProducts.add(prodIds[i])
                                }

                                count++ // Increment counter

                                // Check if all products have been checked
                                if (count == prodIds.size) {
                                    if (outOfStockProducts.isNotEmpty()) {
                                        // Show alert for out of stock products
                                        MaterialAlertDialogBuilder(this)
                                            .setTitle("Out of Stock")
                                            .setMessage("Some of the products are out of stock!")
                                            .setPositiveButton("Ok") { dialog, which ->
                                                dialog.dismiss()
                                            }
                                            .show()
                                    } else {
                                        // All products are in stock, proceed with order confirmation
                                        confirmOrder()
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("TAG", "Error checking product stock", e)
                            }
                    }
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    private fun updateEmptyViewVisibility() {

    }

    private fun confirmOrder(){
        CoroutineScope(Dispatchers.IO).launch {
            if (checkoutAddressAdapter!!.itemCount!=0){
                var userName = user?.displayName
                var selectedEmail = user?.email
                var selectedName = checkoutAddressAdapter!!.selectedName
                var selectedPhone = checkoutAddressAdapter!!.selectedPhone
                var selectedAddres = checkoutAddressAdapter!!.selectedAddress
                var paymentMethod = paymentMethod.text.toString()
                var prodIds = checkoutProductAdapter!!.prodIds
                var prodNames = checkoutProductAdapter!!.prodNames
                var prodImages = checkoutProductAdapter!!.prodImages
                var prodPrices = checkoutProductAdapter!!.prodPrices
                var prodDiscounts = checkoutProductAdapter!!.prodDiscounts
                var prodDiscountedPrices = checkoutProductAdapter!!.prodDiscountedPrices
                var prodQuantities = checkoutProductAdapter!!.prodQuantities
                var prodRatings = checkoutProductAdapter!!.prodRatings
                var prodRatingCounts = checkoutProductAdapter!!.prodRatingCounts
                var overAllPrice = overAllRealPrice.text.toString().replace("₹","")
                var overAllDiscountedPrice = overAllDiscountedPrice.text.toString().replace("₹","")
                var overAllDiscount = overAllDiscount.text.toString().replace("% ↓", "")
                var grandTotal = grandTotal.text.toString().replace("₹","")
                var orderStatus = "Order Placed"
                var orderDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(System.currentTimeMillis())
                var expectedDeliveryDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(System.currentTimeMillis() + 604800000)

                if (paymentMethod == "" || paymentMethod.isBlank()){
                    withContext(Dispatchers.Main){
                        MaterialAlertDialogBuilder(this@CheckoutActivity)
                            .setTitle("Payment Method")
                            .setMessage("Please select a payment method!")
                            .setPositiveButton("Ok") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    return@launch
                }

                var order = hashMapOf(
                    "userId" to user?.uid,
                    "userName" to userName,
                    "userEmail" to selectedEmail,
                    "selectedName" to selectedName,
                    "selectedPhone" to selectedPhone,
                    "selectedAddres" to selectedAddres,
                    "paymentMethod" to paymentMethod,
                    "prodIds" to prodIds,
                    "prodNames" to prodNames,
                    "prodImages" to prodImages,
                    "prodPrices" to prodPrices,
                    "prodDiscounts" to prodDiscounts,
                    "prodDiscountedPrices" to prodDiscountedPrices,
                    "prodQuantities" to prodQuantities,
                    "prodRatings" to prodRatings,
                    "prodRatingCounts" to prodRatingCounts,
                    "overAllPrice" to overAllPrice,
                    "overAllDiscountedPrice" to overAllDiscountedPrice,
                    "overAllDiscount" to overAllDiscount,
                    "grandTotal" to grandTotal,
                    "orderStatus" to orderStatus,
                    "orderDate" to orderDate,
                    "expectedDeliveryDate" to expectedDeliveryDate
                )

                db.collection("orders")
                    .add(order)
                    .addOnSuccessListener {
                        db.collection("users").document(user!!.uid)
                            .update("cartList", emptyList<String>())
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully written!")

                                for (i in 0 until prodIds.size) {
                                    db.collection("products").document(prodIds[i]).get()
                                        .addOnSuccessListener {
                                            var quantity = it.get("productStock").toString().toInt() - prodQuantities[i]
                                            db.collection("products").document(prodIds[i])
                                                .update("productStock", quantity)
                                                .addOnSuccessListener {
                                                    Log.d(
                                                        "TAG",
                                                        "DocumentSnapshot successfully written!"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("TAG", "Error writing document", e)
                                                }
                                            Log.d("TAG", "DocumentSnapshot successfully written!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("TAG", "Error writing document", e)
                                        }
                                }

                                orderPlacedNotification()

                                MaterialAlertDialogBuilder(this@CheckoutActivity)
                                    .setView(R.layout.orderconfirm_dialog)
                                    .setTitle("Order Placed")
                                    .setMessage("Your order has been placed successfully!")
                                    .setPositiveButton("Ok") { dialog, which ->
                                        dialog.dismiss()
                                        finish()
                                    }
                                    .show()
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error writing document", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("TAG", "Error adding document", e)
                        MaterialAlertDialogBuilder(this@CheckoutActivity)
                            .setTitle("Order Failed")
                            .setMessage("Your order has been failed to place!")
                            .setPositiveButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
            }
        }
    }

    private fun orderReceivedNotification(){
        CoroutineScope(Dispatchers.IO).launch {
            var token = db.collection("users")
                .whereEqualTo("useremail", "unrealadmin@gmail.com")
                .get()
                .await()
                .documents[0]
                .get("token").toString()

            var jsonObject = JSONObject()
            var jsonObjectData = JSONObject()

            jsonObjectData.put("title", "Order Received")
            jsonObjectData.put("body", "Hey! Admin a new order has been received successfully!")
            jsonObjectData.put("notificationType", "orderReceived")

            jsonObject.put("to", token)
            jsonObject.put("data", jsonObjectData)

            processNotification(jsonObject)
        }
    }

    private fun orderPlacedNotification(){
        CoroutineScope(Dispatchers.IO).launch {
            var token = db.collection("users").document(user!!.uid).get().await().get("token").toString()

            var jsonObject = JSONObject()
            var jsonObjectData = JSONObject()

            jsonObjectData.put("title", "Order Placed!")
            jsonObjectData.put("body", "Hey! ${auth.currentUser?.displayName} your order has been placed successfully!")

            jsonObject.put("to", token)
            jsonObject.put("data", jsonObjectData)

            processNotification(jsonObject)
        }
    }

    private fun processNotification(jsonObject: JSONObject) {
        val mediaType = MediaType.parse("application/json; charset=utf-8")
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val body = RequestBody.create(mediaType, jsonObject.toString())
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Authorization", "Bearer ${getString(R.string.fcm_servertoken)}")
                    .addHeader("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    Log.d("TAG", "processNotification: Notification sent successfully")
                } else {
                    Log.d("TAG", "processNotification: Failed to send notification ${response.toString()}")
                }

            } catch (e: IOException) {
                Log.e("TAG", "processNotification: ${e.message}", e)
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
}
