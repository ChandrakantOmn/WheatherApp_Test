package com.csk.wheatherapp.ui.place_picker.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.csk.wheatherapp.R
import com.csk.wheatherapp.ui.place_picker.models.AddressData
import com.csk.wheatherapp.ui.place_picker.utilities.CurrentPlaceSelectionBottomSheet
import com.csk.wheatherapp.utilities.Constants
import com.csk.wheatherapp.utilities.PermissionUtility.checkLocationFineAndCoarsePermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class PlacePickerActivity : AppCompatActivity(), OnMapReadyCallback {
    private var map: GoogleMap? = null
    private var markerImage: ImageView? = null
    private lateinit var mImgBack: ImageView
    private var markerShadowImage: ImageView? = null
    private lateinit var bottomSheet: CurrentPlaceSelectionBottomSheet
    private var fab: FloatingActionButton? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var showLatLong = true
    private var zoom = 12.0f
    private var addressRequired = true
    private var shortAddress: String? = ""
    private var fullAddress: String? = ""
    private var hideMarkerShadow = false
    private var markerDrawableRes = -1
    private var markerColorRes = -1
    private var fabColorRes = -1
    private var primaryTextColorRes = -1
    private var secondaryTextColorRes = -1
    private var addresses: List<Address>? = ArrayList()
    private lateinit var mBtnSearchLocation: ImageView
    private lateinit var mBtnSelectAddress: CardView
    private lateinit var mBtnSearchBar: CardView
    private var mCountryCode = ""
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var mBtnCurrentLocation: ImageView
    private var isSearchFromDropdown = false
    private var googleMapApiKey: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker)
        intentData
        initViews()
        updateCurrentLocation()
    }

    private fun updateCurrentLocation() {
        if (latitude == 0.0 && longitude == 0.0) {
            if (checkLocationFineAndCoarsePermission(this)) startLocationUpdates()
        }
    }

    private fun initViews() {
        initPlaces()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        bottomSheet = findViewById(R.id.bottom_sheet)
        bottomSheet.showCoordinatesTextView(showLatLong)
        val mTvTitle = findViewById<TextView>(R.id.tv_title)
        mTvTitle.setText(R.string.search_address)
        markerImage = findViewById(R.id.marker_image_view)
        mImgBack = findViewById(R.id.img_back)
        markerShadowImage = findViewById(R.id.marker_shadow_image_view)
        fab = findViewById(R.id.place_chosen_button)
        mBtnSelectAddress = findViewById(R.id.btnSelectAddress)
        mBtnSearchBar = findViewById(R.id.cvSearchAddress)
        mBtnSearchLocation = findViewById(R.id.img_search)
        mBtnCurrentLocation = findViewById(R.id.ivCurrentLocation)
        mBtnSelectAddress.setOnClickListener(View.OnClickListener {
            if (addresses != null) {
                val addressData = AddressData(latitude, longitude, shortAddress!!, addresses)
                val returnIntent = Intent()
                returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
                setResult(RESULT_OK, returnIntent)
                finish()
            } else {
                if (!addressRequired) {
                    val addressData = AddressData(latitude, longitude, shortAddress!!, null)
                    val returnIntent = Intent()
                    returnIntent.putExtra(Constants.ADDRESS_INTENT, addressData)
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.no_address),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        mImgBack.setOnClickListener(View.OnClickListener { finish() })
        mBtnSearchLocation.setOnClickListener(View.OnClickListener { openAutocompletePicker() })
        mBtnSearchBar.setOnClickListener(View.OnClickListener {
            openAutocompletePicker()
            //                openAutocompletePicker(new LatLng(latitude,longitude),50000);
        })

//        mBtnCurrentLocation.setVisibility(View.GONE);
        mBtnCurrentLocation.setOnClickListener(View.OnClickListener {
            if (checkLocationFineAndCoarsePermission(
                    this@PlacePickerActivity
                )
            ) startLocationUpdates()
        })
        setIntentCustomization()
    }

    private fun initPlaces() {
        try {
            // Initialize Places.
//            Places.initialize(this, getResources().getString(R.string.google_maps_api_key));
            Places.initialize(this, googleMapApiKey!!)
            // Create a new Places client instance.
            val placesClient = Places.createClient(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val intentData: Unit
        private get() {
            if (intent != null) {
                googleMapApiKey = this.intent.getStringExtra(Constants.GOOGLE_MAP_API_KEY)
                latitude = this.intent.getDoubleExtra(Constants.INITIAL_LATITUDE_INTENT, 0.0)
                longitude = this.intent.getDoubleExtra(Constants.INITIAL_LONGITUDE_INTENT, 0.0)
                showLatLong = this.intent.getBooleanExtra(Constants.SHOW_LAT_LONG_INTENT, false)
                addressRequired =
                    this.intent.getBooleanExtra(Constants.ADDRESS_REQUIRED_INTENT, true)
                hideMarkerShadow =
                    this.intent.getBooleanExtra(Constants.HIDE_MARKER_SHADOW_INTENT, false)
                zoom = this.intent.getFloatExtra(Constants.INITIAL_ZOOM_INTENT, 12.0f)
                markerDrawableRes =
                    this.intent.getIntExtra(Constants.MARKER_DRAWABLE_RES_INTENT, -1)
                markerColorRes = this.intent.getIntExtra(Constants.MARKER_COLOR_RES_INTENT, -1)
                fabColorRes = this.intent.getIntExtra(Constants.FAB_COLOR_RES_INTENT, -1)
                primaryTextColorRes =
                    this.intent.getIntExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, -1)
                secondaryTextColorRes =
                    this.intent.getIntExtra(Constants.SECONDARY_TEXT_COLOR_RES_INTENT, -1)
            }
        }

    private fun setIntentCustomization() {
        if (hideMarkerShadow) markerShadowImage!!.visibility =
            View.GONE else markerShadowImage!!.visibility = View.VISIBLE
        if (markerColorRes != -1) {
            markerImage!!.setColorFilter(ContextCompat.getColor(this, markerColorRes))
        }
        if (markerDrawableRes != -1) {
            markerImage!!.setImageDrawable(ContextCompat.getDrawable(this, markerDrawableRes))
        }
        if (fabColorRes != -1) {
            fab!!.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, fabColorRes))
        }
        if (primaryTextColorRes != -1) {
            bottomSheet!!.setPrimaryTextColor(ContextCompat.getColor(this, primaryTextColorRes))
        }
        if (secondaryTextColorRes != -1) {
            bottomSheet!!.setSecondaryTextColor(ContextCompat.getColor(this, secondaryTextColorRes))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        /*if (PermissionUtility.checkLocationFineAndCoarsePermission(this)) {
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }*/map!!.setOnCameraMoveStartedListener {
            if (markerImage!!.translationY == 0f) {
                markerImage!!.animate()
                    .translationY(-75f)
                    .setInterpolator(OvershootInterpolator())
                    .setDuration(250)
                    .start()
                if (bottomSheet!!.isShowing) {
                    bottomSheet!!.dismissPlaceDetails()
                }
            }
        }
        map!!.setOnCameraIdleListener {
            markerImage!!.animate()
                .translationY(0f)
                .setInterpolator(OvershootInterpolator())
                .setDuration(250)
                .start()
            if (isSearchFromDropdown) {
                if (bottomSheet != null) bottomSheet!!.setPlaceDetails(
                    latitude,
                    longitude,
                    shortAddress!!,
                    fullAddress
                )
                isSearchFromDropdown = false
            } else {
                bottomSheet!!.showLoadingBottomDetails()
                val latLng = map!!.cameraPosition.target
                latitude = latLng.latitude
                longitude = latLng.longitude
                AsyncTask.execute {
                    addressForLocation
                    runOnUiThread {
                        bottomSheet!!.setPlaceDetails(
                            latitude,
                            longitude,
                            shortAddress!!,
                            fullAddress
                        )
                    }
                }
            }
        }
        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
    }

    private val addressForLocation: Unit
        private get() {
            setAddress(latitude, longitude)
        }

    private fun setAddress(latitude: Double, longitude: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            this.addresses = addresses
            if (addresses != null && addresses.size != 0) {
                fullAddress = addresses[0].getAddressLine(0)
                shortAddress = generateFinalAddress(fullAddress).trim { it <= ' ' }
                mCountryCode = addresses[0].countryCode
            } else {
                shortAddress = ""
                fullAddress = ""
            }
        } catch (e: Exception) {
            shortAddress = ""
            fullAddress = ""
            addresses = null
            e.printStackTrace()
        }
    }

    private fun generateFinalAddress(address: String?): String {
        val strRtr: String
        val s = address!!.split(",".toRegex()).toTypedArray()
        strRtr = if (s.size >= 3) s[1] + "," + s[2] else if (s.size == 2) s[1] else s[0]
        return strRtr
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.AUTOCOMPLETE_PLACE_PICKER_REQUEST -> if (resultCode == RESULT_OK) {
                try {
                    if (data != null) {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        if (place.latLng != null) {
                            //   bottomSheet.showLoadingBottomDetails();
                            isSearchFromDropdown = true
                            latitude = place.latLng!!.latitude
                            longitude = place.latLng!!.longitude
                            shortAddress = place.name
                            fullAddress = place.address
                            val gcd = Geocoder(this, Locale.getDefault())
                            val addresses = gcd.getFromLocation(latitude, longitude, 1)
                            if (addresses != null && addresses.size != 0) {
                                fullAddress = addresses[0].getAddressLine(0)
                                shortAddress = getPlaceName(
                                    place.name,
                                    generateFinalAddress(fullAddress).trim { it <= ' ' })
                                //                                    shortAddress += " " +generateFinalAddress(fullAddress).trim();
                                mCountryCode = addresses[0].countryCode
                            }
                            if (map != null) map!!.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(latitude, longitude),
                                    zoom
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openAutocompletePicker() {
        try {
            // Set the fields to specify which types of place data to return.
            val fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG,
                Place.Field.RATING,
                Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI,
                Place.Field.VIEWPORT,
                Place.Field.PRICE_LEVEL,
                Place.Field.TYPES,
                Place.Field.OPENING_HOURS,
                Place.Field.PLUS_CODE,
                Place.Field.USER_RATINGS_TOTAL
            )
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .build(this)
            startActivityForResult(intent, Constants.AUTOCOMPLETE_PLACE_PICKER_REQUEST)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openAutocompletePicker(location: LatLng, mDistanceInMeters: Int) {
        val latRadian = Math.toRadians(location.latitude)
        val degLatKm = 110.574235
        val degLongKm = 110.572833 * Math.cos(latRadian)
        val deltaLat = mDistanceInMeters / 1000.0 / degLatKm
        val deltaLong = mDistanceInMeters / 1000.0 / degLongKm
        val minLat = location.latitude - deltaLat
        val minLong = location.longitude - deltaLong
        val maxLat = location.latitude + deltaLat
        val maxLong = location.longitude + deltaLong
        Log.d(
            "Min lat long",
            "Min: " + java.lang.Double.toString(minLat) + "," + java.lang.Double.toString(minLong)
        )
        Log.d(
            "Max lat long",
            "Max: " + java.lang.Double.toString(maxLat) + "," + java.lang.Double.toString(maxLong)
        )
        val bounds = RectangularBounds.newInstance(
            LatLng(minLat, minLong),
            LatLng(maxLat, maxLong)
        )

        // Set the fields to specify which types of place data to return.
        val fields = Arrays.asList(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.ADDRESS_COMPONENTS,
            Place.Field.LAT_LNG,
            Place.Field.RATING,
            Place.Field.PHOTO_METADATAS,
            Place.Field.WEBSITE_URI,
            Place.Field.VIEWPORT,
            Place.Field.PRICE_LEVEL,
            Place.Field.TYPES,
            Place.Field.OPENING_HOURS,
            Place.Field.PLUS_CODE,
            Place.Field.USER_RATINGS_TOTAL
        )
        // Start the autocomplete intent.
        val intent: Intent
        intent = if (TextUtils.isEmpty(mCountryCode)) {
            Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setLocationRestriction(bounds)
                .build(this)
        } else {
            Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields
            )
                .setCountry(mCountryCode)
                .setLocationRestriction(bounds)
                .build(this)
        }
        startActivityForResult(intent, Constants.AUTOCOMPLETE_PLACE_PICKER_REQUEST)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest, LocationCallback(),
            Looper.getMainLooper()
        )
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener(this) { location -> // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    if (map != null) map!!.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                latitude,
                                longitude
                            ), zoom
                        )
                    )
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.MY_PERMISSIONS_REQUEST_LOCATION -> if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startLocationUpdates()
            } else {
            }
        }
    }

    private fun getPlaceName(shortName: String?, fullName: String): String? {
        var returnName: String? = ""
        if (TextUtils.isEmpty(shortName)) {
            if (!TextUtils.isEmpty(fullName)) returnName = fullName
        } else {
            returnName = if (TextUtils.isEmpty(fullName)) shortName else "$shortName, $fullName"
        }
        return returnName
    }
}