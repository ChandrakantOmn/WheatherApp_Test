package com.csk.wheatherapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.csk.wheatherapp.utilities.MarginItemDecorationGrid
import com.csk.wheatherapp.R
import com.csk.wheatherapp.data.local.entity.Location
import com.csk.wheatherapp.databinding.FragmentLocationsBinding
import com.csk.wheatherapp.ui.adapter.LocationAdapter
import com.csk.wheatherapp.ui.adapter.OnItemSelectListener
import com.csk.wheatherapp.ui.place_picker.models.AddressData
import com.csk.wheatherapp.utilities.Constants
import com.csk.wheatherapp.utilities.Constants.PLACE_PICKER_REQUEST
import com.csk.wheatherapp.ui.place_picker.utilities.PlacePicker
import com.csk.wheatherapp.ui.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class LocationsFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLocationsBinding? = null
    private val viewModel by sharedViewModel<WeatherViewModel>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLocations().observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                displayLocation(it.reversed())
            }
        })
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView.addItemDecoration(
            MarginItemDecorationGrid(
                resources.getDimensionPixelSize(R.dimen.margin),
            )
        )
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.listenToFab(this)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestPlace() {
        val intent = PlacePicker.IntentBuilder()
            .setGoogleMapApiKey(getString(R.string.GOOGLE_MAPS_API_KEY))
            .setLatLong(17.4749, 78.3174)
            .setMapZoom(12.0f)
            .setAddressRequired(true)
            .setPrimaryTextColor(R.color.black)
            .build(requireActivity())
        startActivityForResult(intent, PLACE_PICKER_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PLACE_PICKER_REQUEST -> {
                    val addressData: AddressData =
                        data.getParcelableExtra(Constants.ADDRESS_INTENT)!!
                    Log.d("addressData", addressData.toString())
                    viewModel.saveAddress(addressData)
                }
            }
        }
    }

    private fun displayLocation(exams: List<Location>?) {
        binding.recyclerView.adapter = exams?.let {
            LocationAdapter(it as ArrayList<Location>, object : OnItemSelectListener {
                override fun onItemSelect(location: Location) {
                    viewModel.selectedLocation = location
                    findNavController().navigate(R.id.action_LocationFragment_to_CityFragment)
                }

                override fun onDelete(location: Location) {
                    viewModel.removeLocation(location)
                }
            })
        }
    }

    override fun onClick(p0: View?) {
        requestPlace()
    }

}