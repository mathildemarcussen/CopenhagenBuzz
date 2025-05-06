package dk.itu.moapd.copenhagenbuzz.msem

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Firebase.database.setPersistenceEnabled(true)
        Firebase.database.reference.keepSynced(true)
    }
}