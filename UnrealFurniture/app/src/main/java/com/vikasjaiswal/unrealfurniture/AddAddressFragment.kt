package com.vikasjaiswal.unrealfurniture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.regex.Pattern

class AddAddressFragment : Fragment() {

    lateinit var addaddresstitle : TextView

    private lateinit var addressCancel: Button
    private lateinit var addNewAddress: Button

    private lateinit var userName: EditText
    private lateinit var userPhone: EditText
    private lateinit var userHouse: EditText
    private lateinit var userLandmark: EditText
    private lateinit var userRegion: EditText

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var useLocation: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val REQUEST_ENABLE_LOCATION = 1002
    private val PERMISSION_CODE = 1001
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.addaddress_fragment, container, false)

        addaddresstitle = view.findViewById(R.id.addaddresstitle)
        addressCancel = view.findViewById(R.id.addressCancel)
        addNewAddress = view.findViewById(R.id.addNewAddress)
        userName = view.findViewById(R.id.nameedit)
        userPhone = view.findViewById(R.id.contactedit)
        userHouse = view.findViewById(R.id.houseedit)
        userLandmark = view.findViewById(R.id.landmarkedit)
        userRegion = view.findViewById(R.id.regionedit)
        useLocation = view.findViewById(R.id.useLocation)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        addressCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        useLocation.setOnClickListener {
            if (arePermissionsGranted()) {
                getLocation()
            } else {
                requestPermissions()
            }
        }

        addNewAddress.setOnClickListener {
            if (validateInput()) {
                saveAddress()
            }
        }

        var type = arguments?.getString("type")
        var docId = arguments?.getString("docId")
        var name = arguments?.getString("name")
        var phone = arguments?.getString("phone")
        var house = arguments?.getString("house")
        var landmark = arguments?.getString("landmark")
        var region = arguments?.getString("region")

        if (type == "edit") {
            addaddresstitle.text = "Edit Address"
            addNewAddress.text = "Update Address"
            userName.setText(name)
            userPhone.setText(phone)
            userHouse.setText(house)
            userLandmark.setText(landmark)
            userRegion.setText(region)
        }

        return view
    }

    private fun arePermissionsGranted(): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, PERMISSION_CODE)
    }

    private fun getLocation() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 30 * 1000
                fastestInterval = 5 * 1000
            }

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            builder.setAlwaysShow(true)

            val result = LocationServices.getSettingsClient(requireContext())
                .checkLocationSettings(builder.build())

            result.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    getDeviceLocation()
                } else {
                    handleLocationSettings(task)
                }
            }
        } else {
//            ask to open location settings and enable GPS
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, REQUEST_ENABLE_LOCATION)
        }
    }

    private fun getDeviceLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                updateRegionWithLocation(it)
            } ?: run {
                Toast.makeText(requireContext(), "Location Not Found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Log.d("TAG", "getLocation: $it")
        }
    }

    private fun handleLocationSettings(task: Task<LocationSettingsResponse>) {
        try {
            val resolvable = task.exception as ResolvableApiException
            resolvable.startResolutionForResult(requireActivity(), REQUEST_ENABLE_LOCATION)
        } catch (sendEx: IntentSender.SendIntentException) {
            Log.d("TAG", "Error starting resolution for location settings")
        }
    }

    private fun updateRegionWithLocation(location: Location) {
        val geocoder = android.location.Geocoder(requireContext())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val city = addresses?.get(0)?.locality
        val state = addresses?.get(0)?.adminArea
        val country = addresses?.get(0)?.countryName
        val pincode = addresses?.get(0)?.postalCode

        val myAddress = "$city, $pincode, $state, $country"

        Log.d("Addrress", myAddress)

        userRegion.setText(myAddress)
    }

    private fun validateInput(): Boolean {
        val userNameTxt = userName.text.toString()
        val userPhoneTxt = userPhone.text.toString()
        val userHouseTxt = userHouse.text.toString()
        val userLandmarkTxt = userLandmark.text.toString()
        val userRegionTxt = userRegion.text.toString()

        return when {
            userNameTxt.isBlank() || !Pattern.matches("^(?!\\s)[a-zA-Z\\s]{2,}$", userNameTxt) -> {
                showError(userName, "Please enter a valid name")
                false
            }
            userPhoneTxt.isBlank() || !Pattern.matches("^[6-9]\\d{9}$", userPhoneTxt) -> {
                showError(userPhone, "Invalid Phone Number")
                false
            }
            userHouseTxt.isBlank() || userHouseTxt.length<5 -> {
                showError(userHouse, "Please enter a valid house number or building name")
                false
            }
            userLandmarkTxt.isBlank() || userLandmarkTxt.length<5 -> {
                showError(userLandmark, "Please enter a valid landmark or area name")
                false
            }
            userRegionTxt.isBlank() || userRegionTxt.length<5 -> {
                showError(userRegion, "Please enter a valid region or city name")
                false
            }
            else -> true
        }
    }

    private fun showError(editText: EditText, errorMessage: String) {
        editText.error = errorMessage
        editText.requestFocus()
    }

    private fun saveAddress() {
        val userNameTxt = userName.text.toString()
        val userPhoneTxt = userPhone.text.toString()
        val userHouseTxt = userHouse.text.toString()
        val userLandmarkTxt = userLandmark.text.toString()
        val userRegionTxt = userRegion.text.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val address = hashMapOf(
                "name" to userNameTxt,
                "phone" to userPhoneTxt,
                "house" to userHouseTxt,
                "landmark" to userLandmarkTxt,
                "region" to userRegionTxt
            )

            val documentId = UUID.randomUUID().toString()

            if (arguments?.getString("type") == "edit") {
                db.collection("users").document(auth.currentUser!!.uid)
                    .collection("addresses")
                    .document(arguments?.getString("docId").toString())
                    .set(address)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Address Updated Successfully", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                return@launch
            }

            db.collection("users").document(auth.currentUser!!.uid)
                .collection("addresses")
                .document(documentId)
                .set(address)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Address Added Successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
                return@launch
        }
    }
}
