package com.csk.wheatherapp.ui

import android.os.Bundle
import android.view.Menu
import androidx.preference.PreferenceFragmentCompat
import com.csk.wheatherapp.R
import com.csk.wheatherapp.ui.viewmodel.WeatherViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SettingsFragment : PreferenceFragmentCompat() {
    private val viewModel by sharedViewModel<WeatherViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
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