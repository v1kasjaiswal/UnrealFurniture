package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import com.faltenreich.skeletonlayout.Skeleton

class ProfileFragment : Fragment() {

    lateinit var skeleton: Skeleton

    lateinit var myOrdersCard : CardView
    lateinit var myProfileCard : CardView
    lateinit var myAddressCard : CardView
    lateinit var appInfoCard : CardView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        skeleton = view.findViewById(R.id.skeletonLayout)
        skeleton.showSkeleton()

        myOrdersCard = view.findViewById(R.id.myOrdersCard)
        myProfileCard = view.findViewById(R.id.myProfileCard)
        myAddressCard = view.findViewById(R.id.myAddressCard)
        appInfoCard = view.findViewById(R.id.appInfoCard)

        myOrdersCard.setOnClickListener {
            val intent = Intent(context, MyOrdersActivity::class.java)
            startActivity(intent)
        }

        myProfileCard.setOnClickListener {
            val intent = Intent(context, MyProfileActivity::class.java)
            startActivity(intent)
        }

        myAddressCard.setOnClickListener {
            val intent = Intent(context, MyAddressActivity::class.java)
            startActivity(intent)
        }

        appInfoCard.setOnClickListener {
            val intent = Intent(context, AppInfoActivity::class.java)
            startActivity(intent)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            skeleton.showOriginal()
        }, 1800)
          
        return  view
    }
}