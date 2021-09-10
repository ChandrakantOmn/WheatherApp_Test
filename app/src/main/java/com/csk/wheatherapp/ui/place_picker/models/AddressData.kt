package com.csk.wheatherapp.ui.place_picker.models

import android.location.Address
import android.os.Parcel
import android.os.Parcelable
import java.util.*

class AddressData : Parcelable {
    var latitude: Double
    var longitude: Double
    var placeName: String = ""
    var addressList: List<Address>? = ArrayList()

    constructor(
        latitude: Double,
        longitude: Double,
        placeName: String,
        addressList: List<Address>?
    ) {
        this.latitude = latitude
        this.longitude = longitude
        this.addressList = addressList
        this.placeName = placeName
    }

    constructor(`in`: Parcel) {
        latitude = `in`.readDouble()
        longitude = `in`.readDouble()
        placeName = `in`.readString()!!
        addressList = `in`.createTypedArrayList(Address.CREATOR)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeDouble(latitude)
        dest.writeDouble(longitude)
        dest.writeString(placeName)
        dest.writeTypedList(addressList)
    }


    companion object CREATOR : Parcelable.Creator<AddressData> {
        override fun createFromParcel(parcel: Parcel): AddressData {
            return AddressData(parcel)
        }

        override fun newArray(size: Int): Array<AddressData?> {
            return arrayOfNulls(size)
        }
    }
}