package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class HomeFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val cardCount = 5 // Replace with the actual number of cards
    private val cardWidth = 330 // Replace with the actual width of your cards


    private lateinit var popProdLayoutManager: GridLayoutManager
//    private lateinit var decorProdLayoutManager: GridLayoutManager

    private var popProdAdapter: PopProductsRecAdapter? = null
//    private var decorProdAdapter: DecorProdRecAdapter? = null

    private lateinit var popularProdRecyclerView: RecyclerView
//    private lateinit var decorProdRecyclerView: RecyclerView

    private lateinit var horizontalScrollView: HorizontalScrollView
    private lateinit var skeleton: Skeleton

    private val skeletonShowDelay = 1800L
    private val slidingDelay = 2200L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        popularProdRecyclerView = view.findViewById(R.id.popularprodrecycler)
//        decorProdRecyclerView = view.findViewById(R.id.decorprodrecycler)

        popProdLayoutManager = GridLayoutManager(context, 2)
//        decorProdLayoutManager = GridLayoutManager(context, 1)

        popularProdRecyclerView.layoutManager = popProdLayoutManager
//        decorProdRecyclerView.layoutManager = decorProdLayoutManager

        popProdAdapter = PopProductsRecAdapter() // Replace with your actual PopProductsRecAdapter
//        decorProdAdapter = DecorProdRecAdapter() // Replace with your actual DecorProdRecAdapter

        popularProdRecyclerView.adapter = popProdAdapter
//        decorProdRecyclerView.adapter = decorProdAdapter

        horizontalScrollView = view.findViewById(R.id.horizontalScrollView)
        skeleton = view.findViewById(R.id.skeletonLayout)

        skeleton.showSkeleton()

        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, skeletonShowDelay)

        startSliding()

        // Stop sliding when the user interacts with the HorizontalScrollView
        horizontalScrollView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> stopSliding()
                MotionEvent.ACTION_UP -> {
                    currentIndex = 0
                    startSliding()
                }
            }
            false
        }

        return view
    }

    private fun startSliding() {
        handler.postDelayed({
            currentIndex = (currentIndex + 1) % cardCount
            val scrollX = currentIndex * cardWidth
            horizontalScrollView.smoothScrollTo(scrollX, 0)
            startSliding() // Continue the sliding loop
        }, slidingDelay)
    }

    private fun stopSliding() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun onDataLoaded() {
        skeleton.showOriginal()

        // Notify the adapters that data has been loaded
        popProdAdapter?.notifyDataSetChanged()
//        decorProdAdapter?.notifyDataSetChanged()
    }
}
