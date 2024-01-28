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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val cardCount = 5
    private val cardWidth = 330

    private lateinit var popProdLayoutManager: GridLayoutManager
    private lateinit var decorProdLayoutManager: GridLayoutManager

    private var popProdAdapter: PopProductsRecAdapter? = null
    private var decorProdAdapter: DecorProdRecAdapter? = null

    private lateinit var popularProdRecyclerView: RecyclerView
    private lateinit var decorProdRecyclerView: RecyclerView

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
        decorProdRecyclerView = view.findViewById(R.id.decorprodrecycler)

        popProdLayoutManager = GridLayoutManager(context, 2)
        decorProdLayoutManager = GridLayoutManager(context, 1)

        popularProdRecyclerView.layoutManager = popProdLayoutManager
        decorProdRecyclerView.layoutManager = decorProdLayoutManager

        popProdAdapter = PopProductsRecAdapter()
        decorProdAdapter = DecorProdRecAdapter()

        popularProdRecyclerView.adapter = popProdAdapter
        decorProdRecyclerView.adapter = decorProdAdapter

        horizontalScrollView = view.findViewById(R.id.horizontalScrollView)
        skeleton = view.findViewById(R.id.skeletonLayout)

        skeleton.showSkeleton()

        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, skeletonShowDelay)

        startSliding()

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

        // Add scroll listener for lazy loading
        popularProdRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = popProdLayoutManager.itemCount
                val lastVisibleItemPosition =
                    popProdLayoutManager.findLastVisibleItemPosition()

                if (!popProdAdapter!!.isLoading && totalItemCount - 1 <= lastVisibleItemPosition) {
                    popProdAdapter?.loadMoreItems()
                }
            }
        })

        //lazy loading for decor products
        decorProdRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = decorProdLayoutManager.itemCount
                val lastVisibleItemPosition =
                    decorProdLayoutManager.findLastVisibleItemPosition()

                if (!decorProdAdapter!!.isLoading && totalItemCount - 1 <= lastVisibleItemPosition) {
                    decorProdAdapter?.loadMoreItems()
                }
            }
        })

        return view
    }

    private fun startSliding() {
        handler.postDelayed({
            currentIndex = (currentIndex + 1) % cardCount
            val scrollX = currentIndex * cardWidth
            horizontalScrollView.smoothScrollTo(scrollX, 0)
            startSliding()
        }, slidingDelay)
    }

    private fun stopSliding() {
        handler.removeCallbacksAndMessages(null)
    }
}
