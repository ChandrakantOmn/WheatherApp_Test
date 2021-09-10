package com.csk.wheatherapp.ui.place_picker.utilities

import androidx.annotation.DrawableRes
import androidx.annotation.ColorRes
import android.app.Activity
import android.content.Intent
import com.csk.wheatherapp.ui.place_picker.activities.PlacePickerActivity
import com.csk.wheatherapp.utilities.Constants

class PlacePicker {
    class IntentBuilder {
        private var showLatLong = false
        private var latitude = 0.0
        private var longitude = 0.0
        private var zoom = 14.0f
        private var hideMarkerShadow = false
        private var markerDrawableRes = -1
        private var markerImageColorRes = -1
        private var fabBackgroundColorRes = -1
        private var primaryTextColorRes = -1
        private var secondaryTextColorRes = -1
        private var googleMapApiKey: String? = null
        fun setGoogleMapApiKey(googleMapApiKey: String?): IntentBuilder {
            this.googleMapApiKey = googleMapApiKey
            return this
        }

        fun showLatLong(showLatLong: Boolean): IntentBuilder {
            this.showLatLong = showLatLong
            return this
        }

        fun setLatLong(latitude: Double, longitude: Double): IntentBuilder {
            this.latitude = latitude
            this.longitude = longitude
            return this
        }

        fun setMapZoom(zoom: Float): IntentBuilder {
            this.zoom = zoom
            return this
        }

        fun setAddressRequired(addressRequired: Boolean): IntentBuilder {
            return this
        }

        fun hideMarkerShadow(hideMarkerShadow: Boolean): IntentBuilder {
            this.hideMarkerShadow = hideMarkerShadow
            return this
        }

        fun setMarkerDrawable(@DrawableRes markerDrawableRes: Int): IntentBuilder {
            this.markerDrawableRes = markerDrawableRes
            return this
        }

        fun setMarkerImageImageColor(@ColorRes markerImageColorRes: Int): IntentBuilder {
            this.markerImageColorRes = markerImageColorRes
            return this
        }

        fun setFabColor(@ColorRes fabBackgroundColor: Int): IntentBuilder {
            fabBackgroundColorRes = fabBackgroundColor
            return this
        }

        fun setPrimaryTextColor(@ColorRes primaryTextColor: Int): IntentBuilder {
            primaryTextColorRes = primaryTextColor
            return this
        }

        fun setSecondaryTextColor(@ColorRes secondaryTextColorRes: Int): IntentBuilder {
            this.secondaryTextColorRes = secondaryTextColorRes
            return this
        }

        fun build(activity: Activity?): Intent {
            val intent = Intent(activity, PlacePickerActivity::class.java)
            intent.putExtra(Constants.GOOGLE_MAP_API_KEY, googleMapApiKey)
            intent.putExtra(Constants.SHOW_LAT_LONG_INTENT, showLatLong)
            intent.putExtra(Constants.INITIAL_LATITUDE_INTENT, latitude)
            intent.putExtra(Constants.INITIAL_LONGITUDE_INTENT, longitude)
            intent.putExtra(Constants.INITIAL_ZOOM_INTENT, zoom)
            intent.putExtra(Constants.HIDE_MARKER_SHADOW_INTENT, hideMarkerShadow)
            intent.putExtra(Constants.MARKER_DRAWABLE_RES_INTENT, markerDrawableRes)
            intent.putExtra(Constants.MARKER_COLOR_RES_INTENT, markerImageColorRes)
            intent.putExtra(Constants.FAB_COLOR_RES_INTENT, fabBackgroundColorRes)
            intent.putExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, primaryTextColorRes)
            intent.putExtra(Constants.PRIMARY_TEXT_COLOR_RES_INTENT, secondaryTextColorRes)
            return intent
        }
    }
}