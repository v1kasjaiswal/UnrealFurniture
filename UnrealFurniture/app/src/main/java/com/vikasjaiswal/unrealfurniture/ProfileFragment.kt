package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.faltenreich.skeletonlayout.Skeleton

class ProfileFragment : Fragment() {

    lateinit var skeleton: Skeleton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        skeleton = view.findViewById(R.id.skeletonLayout)
        skeleton.showSkeleton()

        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, 1800)
          
        return  view
    }
}