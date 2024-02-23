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
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    lateinit var myOrdersCard : CardView
    lateinit var myProfileCard : CardView
    lateinit var myAddressCard : CardView
    lateinit var appInfoCard : CardView

    lateinit var signOut : Button

    var auth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        myOrdersCard = view.findViewById(R.id.myOrdersCard)
        myProfileCard = view.findViewById(R.id.myProfileCard)
        myAddressCard = view.findViewById(R.id.myAddressCard)
        appInfoCard = view.findViewById(R.id.appInfoCard)

        signOut = view.findViewById(R.id.signOut)

        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gsio)

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

        signOut.setOnClickListener {
            try {
                auth.signOut()
                googleSignInClient.signOut()

                val intent = Intent(context, SignInActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Error: " + e.message)
            }
        }

        return  view
    }
}