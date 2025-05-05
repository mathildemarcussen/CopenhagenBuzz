package dk.itu.moapd.copenhagenbuzz.msem.Model

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getString
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import dk.itu.moapd.copenhagenbuzz.msem.R

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
        if (geofencingEvent!!.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent?.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                context!!,
                geofenceTransition,
                triggeringGeofences
            )

            // Send notification and log the transition details.
            sendNotification(context, geofenceTransitionDetails)
            Log.i(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.d(TAG, "error")
        }
    }
    private fun getGeofenceTransitionDetails(
        context: Context,
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>?
    ): String {
        val transitionString = when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entering"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exiting"
            else -> "Unknown transition"
        }

        val ids = triggeringGeofences?.joinToString { it.requestId } ?: "No Geofence IDs"
        return "$transitionString geofence(s): $ids"
    }

    private fun sendNotification(context: Context, message: String) {
        // You could use NotificationManager here to send a proper notification.
        Log.d(TAG, "Notification: $message")
        // Or actually build and show a real notification (code omitted here for brevity).
    }

}