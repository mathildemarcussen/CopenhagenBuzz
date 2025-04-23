package dk.itu.moapd.copenhagenbuzz.msem.View

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.collection.emptyLongSet
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentMapsBinding
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.copenhagenbuzz.msem.Model.LocationService
import dk.itu.moapd.copenhagenbuzz.msem.Model.LocationService.SharedPreferenceUtil.toSimpleDateFormat
import dk.itu.moapd.copenhagenbuzz.msem.R
import java.util.Locale

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION, Location::class.java)
            else
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION)
            location?.let {

            }

        }
    }

    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }


    private val binding
        get() = requireNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    private var locationService: LocationService? = null

    private var locationServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationService = null
            locationServiceBound = false
        }
    }


    private val callback = OnMapReadyCallback { googleMap ->

        // Add a marker in IT University of Copenhagen and move the camera.
        val itu = LatLng(55.6596, 12.5910)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(itu))

        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        // Enable the location layer. Request the permission if it is not granted.
        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestUserPermissions()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        locationBroadcastReceiver = LocationBroadcastReceiver()


    }

    override fun onStart() {
        super.onStart()


        Intent(requireContext(), LocationService::class.java).let { serviceIntent ->
            requireActivity().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
            super.onResume()

            // Register the broadcast receiver.
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
                locationBroadcastReceiver,
                IntentFilter(LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
            )
        }


    override fun onPause() {
            // Unregister the broadcast receiver.
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
                locationBroadcastReceiver
            )
            super.onPause()
        }


    override fun onStop() {
            // Unbind from the service.
            if (locationServiceBound) {
                requireActivity().unbindService(serviceConnection)
                locationServiceBound = false
            }

            // Unregister the shared preference change listener.
            super.onStop()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }

}