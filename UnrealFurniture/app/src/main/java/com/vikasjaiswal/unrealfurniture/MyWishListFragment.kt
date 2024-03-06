package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.DeleteGesture
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.faltenreich.skeletonlayout.SkeletonLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyWishListFragment : Fragment() {

    private lateinit var myWishListLayoutManager: GridLayoutManager

    private var myWishListAdapter: MyWishListRecAdapter? = null

    private lateinit var myWishListRecyclerView: RecyclerView

    private lateinit var myWishListAnimation : LottieAnimationView
    private lateinit var emptyWishTextView: TextView

    private lateinit var skeletonLayout: SkeletonLayout

    private lateinit var deleteWishList : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.mywishlist_fragment, container, false)

        myWishListRecyclerView = view.findViewById(R.id.myWishListRecyclerView)

        skeletonLayout = view.findViewById(R.id.skeletonLayout)

        deleteWishList = view.findViewById(R.id.deleteWishList)

        myWishListLayoutManager = GridLayoutManager(context, 1)

        myWishListRecyclerView.layoutManager = myWishListLayoutManager

        myWishListAdapter = MyWishListRecAdapter {
            updateEmptyViewVisibility()
        }

        deleteWishList.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete WishList")
                .setMessage("Are you sure you want to delete all items from your wishlist?")
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Yes") { dialog, which ->
                    myWishListAdapter!!.emptyWishList()
                }
                .show()
        }

        skeletonLayout.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            skeletonLayout.showOriginal()
        }, 1000)

        myWishListRecyclerView.adapter = myWishListAdapter

        myWishListAnimation = view.findViewById(R.id.myWishListAnimation)
        emptyWishTextView = view.findViewById(R.id.emptyWishTextView)

        updateEmptyViewVisibility()

        return view
    }

    private fun updateEmptyViewVisibility() {
        if (myWishListAdapter!!.itemCount == 0) {
            myWishListAnimation.visibility = View.VISIBLE
            emptyWishTextView.visibility = View.VISIBLE
        } else {
            myWishListAnimation.visibility = View.GONE
            emptyWishTextView.visibility = View.GONE
        }
    }

}