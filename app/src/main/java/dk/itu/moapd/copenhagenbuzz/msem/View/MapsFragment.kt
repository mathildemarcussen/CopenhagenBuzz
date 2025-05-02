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
import android.app.PendingIntent
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
import android.util.Log
import android.widget.TextView
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.msem.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.Model.LocationService
import dk.itu.moapd.copenhagenbuzz.msem.R
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.location.Geofence
import dk.itu.moapd.copenhagenbuzz.msem.Model.GeofenceBroadcastReceiver


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

        val database = Firebase.database(DATABASE_URL).reference
        val eventsRef = database
            .child("CopenhagenBuzz")
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
                        val event = markerEventMap[marker]
                        marker.showInfoWindow()
                        true
                    }

                    googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
                        override fun getInfoContents(marker: com.google.android.gms.maps.model.Marker): View? {
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

                        override fun getInfoWindow(marker: com.google.android.gms.maps.model.Marker): View? =
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
    private val geofenceIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


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