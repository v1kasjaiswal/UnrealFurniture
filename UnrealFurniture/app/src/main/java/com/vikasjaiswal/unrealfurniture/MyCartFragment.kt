package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyCartFragment : Fragment() {

    private lateinit var myCartLayoutManager: GridLayoutManager

    private var myCartAdapter: MyCartRecAdapter? = null

    private lateinit var myCartRecyclerView: RecyclerView

    private lateinit var myCartAnimation : LottieAnimationView
    private lateinit var emptyCartTextView: TextView

    private lateinit var myCartRealPrice : TextView
    private lateinit var myCartDiscountedPrice : TextView
    private lateinit var myCartDiscount : TextView

    lateinit var checkOutCart : Button

    private lateinit var skeleton : SkeletonLayout

    var db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()

    lateinit var emptyCart : ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mycart_fragment, container, false)

        myCartRecyclerView = view.findViewById(R.id.myCartRecyclerView)

        skeleton = view.findViewById(R.id.skeletonLayout)

        emptyCart = view.findViewById(R.id.emptyCart)

        checkOutCart = view.findViewById(R.id.checkOutCart)

        myCartLayoutManager = GridLayoutManager(context, 1)

        myCartRecyclerView.layoutManager = myCartLayoutManager

        myCartAdapter = MyCartRecAdapter{
            updateEmptyViewVisibility()
        }

        skeleton.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, 1000)

        myCartRecyclerView.adapter = myCartAdapter

        myCartAnimation = view.findViewById(R.id.myCartAnimation)
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView)

        myCartRealPrice = view.findViewById(R.id.cartRealPrice)
        myCartDiscountedPrice = view.findViewById(R.id.cartDiscountedPrice)
        myCartDiscount = view.findViewById(R.id.cartDiscount)

        myCartRealPrice.paint.isStrikeThruText = true

        emptyCart.setOnClickListener {
            if (myCartAdapter!!.itemCount != 0) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Empty Cart")
                    .setMessage("Are you sure you want to empty your cart?")
                    .setPositiveButton("Yes") { dialog, which ->
                        myCartAdapter!!.emptyCartList()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            else{
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Empty Cart")
                    .setMessage("Your cart is already empty!")
                    .setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        checkOutCart.setOnClickListener {
            checkOutProducts()
        }

        updateEmptyViewVisibility()

        return view
    }

    private fun updateEmptyViewVisibility() {
        if (myCartAdapter!!.itemCount == 0) {
            myCartAnimation.visibility = View.VISIBLE
            emptyCartTextView.visibility = View.VISIBLE

            myCartRealPrice.text = "₹0"
            myCartDiscountedPrice.text = "₹0"
            myCartDiscount.text = "0% off"
        } else {
            myCartAnimation.visibility = View.GONE
            emptyCartTextView.visibility = View.GONE

            myCartRealPrice.text = "₹"+myCartAdapter!!.overAllRealPrice.toString()
            myCartDiscountedPrice.text = "₹"+myCartAdapter!!.overAllDiscountedPrice.toString()
            myCartDiscount.text = myCartAdapter!!.overAllDiscount.toString()+"% off"
        }
    }

    private fun checkOutProducts(){
        if (myCartAdapter!!.itemCount != 0) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Check Out")
                .setMessage("Are you sure you want to check out?")
                .setPositiveButton("Yes") { dialog, which ->
                    proceedToCheckOut()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
        else{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Check Out")
                .setMessage("Your cart is empty!")
                .setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun proceedToCheckOut(){
        CoroutineScope(Dispatchers.IO).launch {
            val result = db.collection("users")
                .document(auth.currentUser?.uid.toString())
                .collection("addresses")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty){
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Check Out")
                            .setMessage("You need to add an address to check out!")
                            .setPositiveButton("Ok") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    else{
                        if (myCartAdapter!!.overAllQuantity > 0 && myCartAdapter!!.overAllQuantity <= 10){

                            val intent = Intent(context, CheckoutActivity::class.java)
                            intent.putExtra("type", "cart")
                            intent.putExtra("prodIds", ArrayList(myCartAdapter!!.prodIds))
                            intent.putExtra("prodImages", ArrayList(myCartAdapter!!.mainImages))
                            intent.putExtra("prodNames", ArrayList(myCartAdapter!!.names))
                            intent.putExtra("prodPrices", ArrayList(myCartAdapter!!.prices))
                            intent.putExtra("prodDiscounts", ArrayList(myCartAdapter!!.discounts))
                            intent.putExtra("prodDiscountedPrices", ArrayList(myCartAdapter!!.discountedPrice))
                            intent.putExtra("prodQuantities", ArrayList(myCartAdapter!!.quantity))
                            intent.putExtra("prodRatings", ArrayList(myCartAdapter!!.ratings))
                            intent.putExtra("prodRatingCounts", ArrayList(myCartAdapter!!.ratingCounts))
                            Log.d("OverAllRealPrice", myCartAdapter!!.overAllRealPrice.toString())
                            Log.d("OverAllDiscountedPrice", myCartAdapter!!.overAllDiscountedPrice.toString())
                            Log.d("OverAllDiscount", myCartAdapter!!.overAllDiscount.toString())
                            intent.putExtra("overAllRealPrice", myCartAdapter!!.overAllRealPrice)
                            intent.putExtra("overAllDiscountedPrice", myCartAdapter!!.overAllDiscountedPrice)
                            intent.putExtra("overAllDiscount", myCartAdapter!!.overAllDiscount)
                            startActivity(intent)

                            myCartAdapter!!.clearCart()
                        }
                        else{
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Check Out")
                                .setMessage("You can only check out 1-10 products at a time!")
                                .setPositiveButton("Ok") { dialog, which ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                }
        }
    }
}