package com.vikasjaiswal.unrealfurniture

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import com.faltenreich.skeletonlayout.Skeleton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class HomeFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private var currentIndex = 0
    private val cardCount = 5 // Replace with the actual number of cards
    private val cardWidth = 330 // Replace with the actual width of your cards

    lateinit var strikeText : MaterialTextView
    lateinit var strikeText2 : MaterialTextView
    lateinit var strikeText3 : MaterialTextView
    lateinit var strikeText4 : MaterialTextView
    lateinit var strikeText5 : MaterialTextView
    lateinit var strikeText6 : MaterialTextView
    lateinit var strikeText7 : MaterialTextView
    lateinit var strikeText8 : MaterialTextView
    lateinit var strikeText9 : MaterialTextView
    lateinit var strikeText10 : MaterialTextView

    lateinit var horizontalScrollView: HorizontalScrollView

    private lateinit var skeleton: Skeleton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)

        horizontalScrollView = view.findViewById(R.id.horizontalScrollView)

        skeleton = view.findViewById(R.id.skeletonLayout)

        strikeText = view.findViewById(R.id.strikeText)
        strikeText2 = view.findViewById(R.id.strikeText2)
        strikeText3 = view.findViewById(R.id.strikeText3)
        strikeText4 = view.findViewById(R.id.strikeText4)
        strikeText5 = view.findViewById(R.id.strikeText5)
        strikeText6 = view.findViewById(R.id.strikeText6)
        strikeText7 = view.findViewById(R.id.strikeText7)
        strikeText8 = view.findViewById(R.id.strikeText8)
        strikeText9 = view.findViewById(R.id.strikeText9)
        strikeText10 = view.findViewById(R.id.strikeText10)

        strikeText.paint.isStrikeThruText = true
        strikeText2.paint.isStrikeThruText = true
        strikeText3.paint.isStrikeThruText = true
        strikeText4.paint.isStrikeThruText = true
        strikeText5.paint.isStrikeThruText = true
        strikeText6.paint.isStrikeThruText = true
        strikeText7.paint.isStrikeThruText = true
        strikeText8.paint.isStrikeThruText = true
        strikeText9.paint.isStrikeThruText = true
        strikeText10.paint.isStrikeThruText = true

        skeleton.showSkeleton()

        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, 1800)

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
        }, 2200)
    }

    private fun stopSliding() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun onDataLoaded() {
        skeleton.showOriginal()
    }
}
