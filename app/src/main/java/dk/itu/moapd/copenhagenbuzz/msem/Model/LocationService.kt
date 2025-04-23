package dk.itu.moapd.copenhagenbuzz.msem.Model

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dk.itu.moapd.copenhagenbuzz.msem.R

class LocationService : Service() {

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    private val localBinder = LocalBinder()

    companion object {
        private const val PACKAGE_NAME = "dk.itu.moapd.copenhagenbuzz.msem"
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                val currentLocation = p0.lastLocation
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }


    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    fun subscribeToLocationUpdates() {
        SharedPreferenceUtil.saveLocationTrackingPref(this, true)

        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 60)
            .setMinUpdateIntervalMillis(30)
            .setMaxUpdateDelayMillis(2)
            .build()

        try {
            fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        }
    }

    fun unsubscribeToLocationUpdates() {
        try {
            fusedLocationProviderClient
                .removeLocationUpdates(locationCallback)
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
        }
    }

    internal object SharedPreferenceUtil {

        const val KEY_FOREGROUND_ENABLED = "tracking_foreground_location"
        fun saveLocationTrackingPref(context: Context, requestingLocationUpdates: Boolean) =
            context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE
            ).edit {
                putBoolean(KEY_FOREGROUND_ENABLED, requestingLocationUpdates)
            }

        fun Long.toSimpleDateFormat(): String {
            val dateFormat = SimpleDateFormat("E, MMM dd yyyy hh:mm:ss a", Locale.US)
            return dateFormat.format(this)
        }

    }
}