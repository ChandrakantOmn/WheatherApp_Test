package com.csk.wheatherapp.ui.place_picker.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.csk.wheatherapp.R
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CurrentPlaceSelectionBottomSheet : CoordinatorLayout {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var rootView: CoordinatorLayout? = null
    private var placeNameTextView: TextView? = null
    private var placeAddressTextView: TextView? = null
    private var placeCoordinatesTextView: TextView? = null
    private var placeProgressBar: ProgressBar? = null
    private var mBottomContainer: LinearLayout? = null

    constructor(context: Context) : super(context) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initialize(context)
    }

    val isShowing: Boolean
        get() = bottomSheetBehavior!!.state != BottomSheetBehavior.STATE_HIDDEN

    private fun initialize(context: Context) {
        rootView = inflate(context, R.layout.bottom_sheet_view, this) as CoordinatorLayout
        bottomSheetBehavior =
            BottomSheetBehavior.from(rootView!!.findViewById(R.id.root_bottom_sheet))
        (bottomSheetBehavior as BottomSheetBehavior<*>).isHideable = true
        (bottomSheetBehavior as BottomSheetBehavior<*>).state = BottomSheetBehavior.STATE_HIDDEN
        bindViews()
    }

    private fun bindViews() {
        placeNameTextView = rootView!!.findViewById(R.id.text_view_place_name)
        placeAddressTextView = rootView!!.findViewById(R.id.text_view_place_address)
        placeCoordinatesTextView = rootView!!.findViewById(R.id.text_view_place_coordinates)
        placeProgressBar = rootView!!.findViewById(R.id.progress_bar_place)
        mBottomContainer = findViewById(R.id.ll_bottom_container)
    }

    fun showCoordinatesTextView(show: Boolean) {
        if (show) placeCoordinatesTextView!!.visibility =
            VISIBLE else placeCoordinatesTextView!!.visibility = GONE
    }

    fun setPrimaryTextColor(color: Int) {
        placeNameTextView!!.setTextColor(color)
    }

    fun setSecondaryTextColor(color: Int) {
        placeAddressTextView!!.setTextColor(color)
    }

    @SuppressLint("SetTextI18n")
    fun setPlaceDetails(
        latitude: Double,
        longitude: Double,
        shortAddress: String,
        fullAddress: String?
    ) {
        if (latitude == -1.0 || longitude == -1.0) {
            placeNameTextView!!.text = ""
            placeAddressTextView!!.text = ""
            mBottomContainer!!.visibility = INVISIBLE
            placeProgressBar!!.visibility = VISIBLE
            return
        }
        placeProgressBar!!.visibility = INVISIBLE
        mBottomContainer!!.visibility = VISIBLE
        if (shortAddress.isEmpty()) placeNameTextView!!.text =
            "Dropped Pin" else placeNameTextView!!.text = shortAddress
        placeAddressTextView!!.text = fullAddress
        placeCoordinatesTextView!!.text =
            Location.convert(latitude, Location.FORMAT_DEGREES) + ", " + Location.convert(
                longitude,
                Location.FORMAT_DEGREES
            )
        showBottomSheetView()
    }

    fun showLoadingBottomDetails() {
        if (!isShowing) {
            toggleBottomSheet()
        }
        placeNameTextView!!.text = ""
        placeAddressTextView!!.text = ""
        placeCoordinatesTextView!!.text = ""
        placeProgressBar!!.visibility = VISIBLE
        mBottomContainer!!.visibility = INVISIBLE
    }

    fun dismissPlaceDetails() {
        toggleBottomSheet()
    }

    private fun showBottomSheetView() {
        val relativeLayout = rootView!!.findViewById<RelativeLayout>(R.id.bottom_sheet_header)
        val vto = relativeLayout.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relativeLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height = relativeLayout.measuredHeight
                bottomSheetBehavior!!.peekHeight = height
                bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        })
        bottomSheetBehavior!!.peekHeight =
            rootView!!.findViewById<View>(R.id.bottom_sheet_header).height
        bottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun toggleBottomSheet() {
        bottomSheetBehavior!!.peekHeight =
            rootView!!.findViewById<View>(R.id.bottom_sheet_header)?.height!!
        if (isShowing) bottomSheetBehavior!!.setState(BottomSheetBehavior.STATE_HIDDEN) else bottomSheetBehavior!!.setState(
            BottomSheetBehavior.STATE_COLLAPSED
        )
    }
}