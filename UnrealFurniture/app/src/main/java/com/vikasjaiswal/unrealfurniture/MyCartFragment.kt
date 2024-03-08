package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyCartFragment : Fragment() {

    private lateinit var myCartLayoutManager: GridLayoutManager

    private var myCartAdapter: MyCartRecAdapter? = null

    private lateinit var myCartRecyclerView: RecyclerView

    private lateinit var myCartAnimation : LottieAnimationView
    private lateinit var emptyCartTextView: TextView

    private lateinit var myCartRealPrice : TextView
    private lateinit var myCartDiscountedPrice : TextView
    private lateinit var myCartDiscount : TextView

    private lateinit var skeleton : SkeletonLayout

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

            myCartRealPrice.text = myCartAdapter!!.overAllRealPrice.toString()
            myCartDiscountedPrice.text = myCartAdapter!!.overAllDiscountedPrice.toString()
            myCartDiscount.text = myCartAdapter!!.overAllDiscount.toString()+"% off"
        }
    }

}