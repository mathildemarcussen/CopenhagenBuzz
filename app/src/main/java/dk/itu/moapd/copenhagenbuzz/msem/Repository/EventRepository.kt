package dk.itu.moapd.copenhagenbuzz.msem.Repository

import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.auth
import dk.itu.moapd.copenhagenbuzz.msem.database

class EventRepository : IRepository<Event> {
    override suspend fun upload(key: String, event: Event): String {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            event.userID = userId
            database
                .child("events")
                .child(key)
                .setValue(event)
        }
        return key
    }

    override fun get(key: String): Event {
        TODO("Not yet implemented")
    }

    override fun update(key: String, newItem: Event): Event {
        TODO("Not yet implemented")
    }

    override fun delete(key: String): Event {
        TODO("Not yet implemented")
    }
}