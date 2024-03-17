package com.vikasjaiswal.unrealfurniture

import android.animation.ObjectAnimator
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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.faltenreich.skeletonlayout.Skeleton
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private lateinit var openInfo: ImageView
    private lateinit var openSupport: ImageView

    private lateinit var skeleton: Skeleton

    private val slidingDelay = 1600L
    private val smoothScrollDelay = 16L
    private val skeletonDisplayDelay = 1500L

    private var slidingRunnable: Runnable? = null

    private lateinit var bannerImage: ImageView
    private var currentBannerIndex = 1
    private val totalBannerCount = 5

    private var storageReference = FirebaseStorage.getInstance().reference

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

        bannerImage = view.findViewById(R.id.bannerImage)

        // Start loading featured products with a slight delay
        handler.postDelayed({
            featularProdRecyclerView.adapter = featProdAdapter
        }, 100)

        // Show skeleton view for a short duration before loading popular products
        skeleton.showSkeleton()
        handler.postDelayed({
            popProdRecyclerView.adapter = popProdAdapter
            skeleton.showOriginal()
        }, 1000)

        startSliding()

        // Handle touch events on the horizontal scroll view
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

        // Open Info Activity on click
        openInfo.setOnClickListener {
            startActivity(Intent(context, InfoActivity::class.java))
        }

        // Open Support Activity on click
        openSupport.setOnClickListener {
            startActivity(Intent(context, SupportActivity::class.java))
        }

        return view
    }

    override fun onStart() {
        super.onStart()
//        loadBannerImagesLoop()
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

    private fun loadBannerImagesLoop() {
        coroutineScope.launch {
            while (true) {
                loadBannerImage()
                delay(3000)
            }
        }
    }

    private fun loadBannerImage() {
        try{
        if (isAdded() && activity != null) {
            val bannerRef = storageReference.child("BannerImages/$currentBannerIndex.jpg")
            bannerRef.downloadUrl.addOnSuccessListener { imageUrl ->
                lifecycleScope.launchWhenCreated {
                    Glide.with(this@HomeFragment)
                        .load(imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(bannerImage)

                }
                currentBannerIndex = (currentBannerIndex % totalBannerCount) + 1
            }
        }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}
