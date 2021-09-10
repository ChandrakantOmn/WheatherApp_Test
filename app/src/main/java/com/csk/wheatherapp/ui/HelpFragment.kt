package com.csk.wheatherapp.ui

import android.os.Bundle
import android.util.Xml
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.csk.wheatherapp.databinding.FragmentHelpBinding
import com.csk.wheatherapp.ui.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HelpFragment : Fragment() {
    private val viewModel by sharedViewModel<WeatherViewModel>()
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val text="<html>About app and steps to use the Weather App</html>"
        binding.webView.loadData(text, "text/html", Xml.Encoding.UTF_8.toString());

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.hideFab()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }
}