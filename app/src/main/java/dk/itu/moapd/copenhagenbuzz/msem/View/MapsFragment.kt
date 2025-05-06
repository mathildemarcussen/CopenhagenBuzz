package dk.itu.moapd.copenhagenbuzz.msem.View

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import dk.itu.moapd.copenhagenbuzz.msem.databinding.FragmentMapsBinding
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.Model.LocationService
import dk.itu.moapd.copenhagenbuzz.msem.R
import com.google.android.gms.maps.model.Marker
import dk.itu.moapd.copenhagenbuzz.msem.database


class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markerEventMap = mutableMapOf<Marker, Event>()
    lateinit var geofencingClient: GeofencingClient


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


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(listener: OnTokenCanceledListener) = this
                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener { location ->
            if (location != null) {
                val latLong = LatLng(location.latitude, location.longitude)
                Log.d("Camera Movement", "Moved camera to point {$latLong}")
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLong))
            }
        }


        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        // Enable the location layer. Request the permission if it is not granted.
        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestUserPermissions()
        }

        val eventsRef = database
            .child("events")


        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    val locationSnapshot = eventSnapshot.child("eventLocation")
                    val lat = locationSnapshot.child("latitude").getValue(Double::class.java)
                    val lng = locationSnapshot.child("longitude").getValue(Double::class.java)

                    val name = eventSnapshot.child("eventName").getValue(String::class.java)

                    if (lat != null && lng != null) {
                        val position = LatLng(lat, lng)
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(position)
                                .title(name)
                        )
                        if(marker != null && event !=null) {
                            markerEventMap[marker] = event
                        }

                    }
                    googleMap.setOnMarkerClickListener { marker ->
                        marker.showInfoWindow()
                        true
                    }

                    googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(marker: Marker): View? {
                            val infoView = layoutInflater.inflate(R.layout.custom_info_window, null)

                            val titleView = infoView.findViewById<TextView>(R.id.info_title)
                            val descView = infoView.findViewById<TextView>(R.id.info_description)
                            val dateView = infoView.findViewById<TextView>(R.id.info_date)
                            val typeView = infoView.findViewById<TextView>(R.id.info_type)
                            val addView = infoView.findViewById<TextView>(R.id.info_address)

                            titleView.text = marker.title

                            val event1 = markerEventMap[marker]
                            dateView.text = event1?.eventDate ?: "Unknown date"
                            typeView.text = event1?.eventType ?: "Unknown type"
                            descView.text = event1?.eventDescription ?: "Unknown description"
                            addView.text = event1?.eventLocation?.address ?: "Unknown Locatoin"

                            return infoView
                        }

                        override fun getInfoWindow(marker: Marker): View? =
                            null
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to read event locations: ${error.message}")
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        locationBroadcastReceiver = LocationBroadcastReceiver()

        binding.btnAddGeofence.setOnClickListener {
            showGeofenceDialog()
        }
    }

    override fun onStart() {
        super.onStart()

        val serviceIntent = Intent(requireContext(), LocationService::class.java)
        requireContext().startService(serviceIntent)

        requireActivity().bindService(
            serviceIntent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

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

    private fun checkPermission(): Boolean {
        val fineLocation = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Background permission not needed before Android Q
        }

        return fineLocation && backgroundLocation
    }

    private fun requestUserPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions.toTypedArray(),
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun showGeofenceDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_geofence, null)
        val seekBar = dialogView.findViewById<SeekBar>(R.id.radius_seekbar)
        val radiusLabel = dialogView.findViewById<TextView>(R.id.radius_label)

        var radius = 100
        radiusLabel.text = "Radius: ${radius}m"

        seekBar.progress = radius
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                radius = if (progress < 50) 50 else progress // Min radius = 50m
                radiusLabel.text = "Radius: ${radius}m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        AlertDialog.Builder(requireContext())
            .setTitle("Add Geofence")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                addGeofenceAtCurrentLocation(radius)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofenceAtCurrentLocation(radius: Int) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Create a geofence centered at the current location
                val geofenceCenter = LatLng(location.latitude, location.longitude)

                val eventsRef = database.child("events")

                eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val eventsInsideGeofence = mutableListOf<String>()

                        for (eventSnapshot in snapshot.children) {
                            val lat = eventSnapshot.child("eventLocation").child("latitude").getValue(Double::class.java)
                            val lng = eventSnapshot.child("eventLocation").child("longitude").getValue(Double::class.java)

                            if (lat != null && lng != null) {
                                val eventLocation = LatLng(lat, lng)
                                val distance = calculateDistance(geofenceCenter, eventLocation)

                                if (distance <= radius) {
                                    val eventName = eventSnapshot.child("eventName").getValue(String::class.java)
                                    eventsInsideGeofence.add(eventName ?: "Unnamed event")
                                }
                            }
                        }

                        // Show dialog with events inside geofence
                        showEventsInsideGeofenceDialog(eventsInsideGeofence)

                        drawGeofenceCircle(geofenceCenter, radius)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FirebaseError", "Failed to read event locations: ${error.message}")
                    }
                })
            }
        }
    }

    private fun calculateDistance(latLng1: LatLng, latLng2: LatLng): Float {
        val location1 = Location("Point 1")
        location1.latitude = latLng1.latitude
        location1.longitude = latLng1.longitude

        val location2 = Location("Point 2")
        location2.latitude = latLng2.latitude
        location2.longitude = latLng2.longitude

        return location1.distanceTo(location2) // Returns distance in meters
    }

    private fun showEventsInsideGeofenceDialog(events: List<String>) {
        val eventsList = events.joinToString("\n")

        AlertDialog.Builder(requireContext())
            .setTitle("Events Inside Geofence")
            .setMessage(eventsList.ifEmpty { "No events inside the geofence" })
            .setPositiveButton("OK", null)
            .show()
    }

    private fun drawGeofenceCircle(center: LatLng, radius: Int) {
        val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync { map ->
            map.addCircle(
                CircleOptions()
                    .center(center)
                    .radius(radius.toDouble())
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF)
                    .strokeWidth(2f)
            )
        }
    }



}