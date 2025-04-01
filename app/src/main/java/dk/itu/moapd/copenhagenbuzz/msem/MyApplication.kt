package dk.itu.moapd.copenhagenbuzz.msem

import android.app.Application
import com.google.android.material.color.DynamicColors
import io.github.cdimascio.dotenv.dotenv

class MyApplication : Application() {
    val DATABASE_URL: String = dotenv {
                directory = "/assets"
                filename = "secrets.env"
            }["DATABASE_URL"]
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}