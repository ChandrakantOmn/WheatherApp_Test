package com.csk.wheatherapp.ui.viewmodel

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.csk.wheatherapp.data.Resource
import com.csk.wheatherapp.data.local.entity.Location
import com.csk.wheatherapp.data.repo.WeatherRepository
import com.csk.wheatherapp.ui.place_picker.models.AddressData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    private var mFab: FloatingActionButton? = null
    var selectedLocation: Location? = null

    fun hideFab() {
        mFab?.isVisible = false
    }

    fun showFab() {
        mFab?.isVisible = true
    }

    fun setFab(fab: FloatingActionButton?) {
        mFab = fab
    }

    fun listenToFab(ocl: View.OnClickListener?) {
        if (ocl != null) mFab?.setOnClickListener(ocl)
    }

    fun getWeatherReport() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            if (selectedLocation != null) emit(
                Resource.success(
                    data = repository.getWeatherReport(
                        selectedLocation!!
                    )
                )
            )
            else emit(Resource.error(data = null, message = "Error Occurred!"))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun getUpComingWeatherReport() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            if (selectedLocation != null) emit(
                Resource.success(
                    data = repository.getUpComingWeatherReport(
                        selectedLocation!!
                    )
                )
            )
            else emit(Resource.error(data = null, message = "Error Occurred!"))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }

    fun saveAddress(addressData: AddressData) {
        viewModelScope.launch {
            selectedLocation = addressData.getLocation()
            selectedLocation?.let {
                repository.saveAddress(selectedLocation!!)
            }
        }
    }

    fun getLocations(): LiveData<List<Location>> {
        return repository.getLocations()
    }

    fun removeLocation(location: Location) {
        viewModelScope.launch {
            repository.removeLocation(location)
        }
    }

}

private fun AddressData.getLocation(): Location {
    val addressData = this
    return Location().apply {
        latitude = addressData.latitude
        longitude = addressData.longitude
        placeName = addressData.placeName
        cityName = addressData.addressList?.get(0)?.locality ?: addressData.placeName
    }
}
