package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.faltenreich.skeletonlayout.Skeleton
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val cardCount = 5
    private val cardWidth = 330

    private lateinit var featProdLayoutManager: GridLayoutManager
    private lateinit var popProdLayoutManager: GridLayoutManager

    private var featProdAdapter: FeatProductsRecAdapter? = null
    private var popProdAdapter: PopProductsRecAdapter? = null

    private lateinit var featularProdRecyclerView: RecyclerView
    private lateinit var popProdRecyclerView: RecyclerView

    private lateinit var horizontalScrollView: HorizontalScrollView

    lateinit var openInfo: ImageView
    lateinit var openSupport: ImageView

    lateinit var skeleton: Skeleton

    private val slidingDelay = 1600L
    private val smoothScrollDelay = 16L  // Adjust as needed
    private val skeletonDisplayDelay = 1500L // 0.8 seconds

    private var slidingRunnable: Runnable? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        featularProdRecyclerView = view.findViewById(R.id.featprodrecycler)
        popProdRecyclerView = view.findViewById(R.id.popprodrecycler)

        featProdLayoutManager = GridLayoutManager(context, 2)
        popProdLayoutManager = GridLayoutManager(context, 1)

        featularProdRecyclerView.layoutManager = featProdLayoutManager
        popProdRecyclerView.layoutManager = popProdLayoutManager

        featProdAdapter = FeatProductsRecAdapter()
        popProdAdapter = PopProductsRecAdapter()

        openInfo = view.findViewById(R.id.openInfo)
        openSupport = view.findViewById(R.id.openSupport)

        horizontalScrollView = view.findViewById(R.id.horizontalScrollView)

        skeleton = view.findViewById(R.id.skeletonLayout)


        Handler(Looper.getMainLooper()).postDelayed({
            featularProdRecyclerView.adapter = featProdAdapter
        }, 100)

        skeleton.showSkeleton()
        Handler(Looper.getMainLooper()).postDelayed({
            popProdRecyclerView.adapter = popProdAdapter
            skeleton.showOriginal()
        }, 1000)

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

        openInfo.setOnClickListener {
            val intent = Intent(context, InfoActivity::class.java)
            startActivity(intent)
        }

        openSupport.setOnClickListener {
            val intent = Intent(context, SupportActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun startSliding() {
        slidingRunnable = object : Runnable {
            override fun run() {
                currentIndex = (currentIndex + 1) % cardCount
                val scrollX = currentIndex * cardWidth
                horizontalScrollView.smoothScrollTo(scrollX, 0)
                handler.postDelayed(this, slidingDelay)
            }
        }
        handler.postDelayed(slidingRunnable!!, slidingDelay)
    }

    private fun stopSliding() {
        slidingRunnable?.let {
            handler.removeCallbacks(it)
            slidingRunnable = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
