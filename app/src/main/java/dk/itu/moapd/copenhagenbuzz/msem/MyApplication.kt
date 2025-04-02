package dk.itu.moapd.copenhagenbuzz.msem

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.cdimascio.dotenv.dotenv

    val DATABASE_URL: String = dotenv {
        directory = "/assets"
        filename = "secrets.env"
    }["DATABASE_URL"]

class MyApplication : Application() {
    

    
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
        Firebase.database(DATABASE_URL).reference.keepSynced(true)
    }
}