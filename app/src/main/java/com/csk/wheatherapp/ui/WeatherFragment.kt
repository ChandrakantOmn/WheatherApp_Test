package com.csk.wheatherapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.csk.wheatherapp.R
import com.csk.wheatherapp.data.Status
import com.csk.wheatherapp.databinding.FragmentWeatherBinding
import com.csk.wheatherapp.ui.adapter.UpcomingAdapter
import com.csk.wheatherapp.ui.place_picker.models.AddressData
import com.csk.wheatherapp.utilities.Constants
import com.csk.wheatherapp.ui.place_picker.utilities.PlacePicker
import com.csk.wheatherapp.ui.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class WeatherFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private val viewModel by sharedViewModel<WeatherViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayReport()
        binding.acCity.setOnClickListener {
            requestPlace()
        }
        binding.refresh.setOnRefreshListener {
            displayReport()
        }

    }

    private fun displayReport() {
        displayWeatherReport()
        displayUpcomingWeatherReport()
        viewModel.listenToFab(this)
        binding.acCity.setText(viewModel.selectedLocation?.cityName)
    }

    @SuppressLint("SetTextI18n")
    private fun displayWeatherReport() {
        viewModel.getWeatherReport(
        ).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val data = resource.data
                        binding.loading.visibility = View.INVISIBLE
                        binding.tvHumidity.text = "${data?.main?.humidity}%"
                        binding.tvPressure.text = "${data?.main?.pressure} hPa"
                        binding.tvTemp.text = "${data?.main?.temp?.toInt()}\u2103"
                        val image =
                            "http://openweathermap.org/img/wn/${data?.weather?.get(0)?.icon}@2x.png"
                        Glide.with(this)
                            .load(image)
                            .into(binding.imgCloud)

                        binding.tvDescriptionWeather.text = "${data?.weather?.get(0)?.description}"
                        binding.refresh.isRefreshing = false
                        binding.loading.visibility = View.INVISIBLE

                    }
                    Status.ERROR -> {
                        binding.loading.visibility = View.INVISIBLE
                        binding.refresh.isRefreshing = false
                    }
                    Status.LOADING -> {
                        binding.loading.visibility = View.VISIBLE

                    }
                }
            }
        })


    }

    @SuppressLint("SetTextI18n")
    private fun displayUpcomingWeatherReport() {
        viewModel.getUpComingWeatherReport(
        ).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val data = resource.data
                        binding.refresh.isRefreshing = false
                        binding.rvUpComingDays.apply {
                            layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            if (data?.list?.isNotEmpty() == true) {
                                val list =
                                    if (data.list?.size!! > 10) data.list?.take(10) else data.list
                                adapter = UpcomingAdapter(list)
                            }

                        }
                    }
                    Status.ERROR -> {
                        binding.loading.visibility = View.INVISIBLE
                        binding.refresh.isRefreshing = false
                    }
                    Status.LOADING -> binding.loading.visibility = View.VISIBLE
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(p0: View?) {
        findNavController().navigate(R.id.action_WeatherFragment_to_LocationsFragment)
    }

    private fun requestPlace() {
        val intent = PlacePicker.IntentBuilder()
            .setGoogleMapApiKey(getString(R.string.GOOGLE_MAPS_API_KEY))
            .setLatLong(17.4749, 78.3174)
            .setMapZoom(12.0f)
            .setAddressRequired(true)
            .setPrimaryTextColor(R.color.black)
            .build(requireActivity())
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                Constants.PLACE_PICKER_REQUEST -> {
                    val addressData: AddressData =
                        data.getParcelableExtra(Constants.ADDRESS_INTENT)!!
                    Log.d("addressData", addressData.toString())
                    viewModel.saveAddress(addressData)
                    displayReport()
                }
            }
        }
    }

}