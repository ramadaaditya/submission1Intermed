package com.dicoding.storyapp.ui.maps

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.ResultState
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.ui.ViewModelFactory
import com.dicoding.storyapp.utils.vectorToBitmap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeStoryLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val indonesia = LatLng(0.143136, 118.7371783)
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        setMapStyle()
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 5f))
    }

    private fun observeStoryLocation() {
        mapsViewModel.storyLocation.observe(this) { story ->
            when (story) {
                is ResultState.Loading -> {
                    Log.i("MapsActivity", "onMapReady: Loading")
                }

                is ResultState.Success -> {
                    handleSuccess(story.data)
                }

                is ResultState.Error -> {
                    toast(story.error)
                }
            }
        }
    }

    private fun handleSuccess(data: List<ListStoryItem>) {
        if (data.isEmpty()) {
            toast("No locations available")
            return
        }

        data.forEach { item ->
            val latLng = LatLng(item.lat, item.lon)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(item.name)
                    .snippet(item.description)
                    .icon(vectorToBitmap(R.drawable.baseline_smartphone_24, this@MapsActivity))
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(
            bounds,
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels,
            300
        )
        mMap.animateCamera(cameraUpdate)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "setMapStyle: Style Parsing Failed")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "setMapStyle: Can't find style. Error :", exception)
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "MapsActivity"
    }
}
