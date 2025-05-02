package dk.itu.moapd.copenhagenbuzz.msem

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import io.github.cdimascio.dotenv.dotenv

private val DATABASE_URL: String = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]
private val DATABASE_NAME: String = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_NAME"]

private val BUCKET_URL: String = dotenv {
    directory = "/assets"
    filename = "env"
}["BUCKET_URL"]

val GEOCODING_API_KEY: String = dotenv {
    directory = "/assets"
    filename = "env"
}["GEOCODING_API_KEY"]

const val DB_EVENTS = "events"

const val DB_FAVORITES = "favorites"

val auth by lazy {
    FirebaseAuth.getInstance()
}


val database by lazy {
    Firebase.database(DATABASE_URL).reference.child(DATABASE_NAME)
}


val storage by lazy {
    Firebase.storage(BUCKET_URL).reference.child(DB_EVENTS)
}

fun setupDatabase() {
    Firebase.database(DATABASE_URL).setPersistenceEnabled(true)
    database.keepSynced(true)
}
