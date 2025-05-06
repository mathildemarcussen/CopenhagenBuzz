package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.DB_EVENTS
import dk.itu.moapd.copenhagenbuzz.msem.Repository.EventRepository
import dk.itu.moapd.copenhagenbuzz.msem.Repository.ImageRepository
import dk.itu.moapd.copenhagenbuzz.msem.database
import java.util.UUID

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>(emptyList())
    private lateinit var eventRepository: EventRepository
    private lateinit var imageRepository: ImageRepository
    private var favoriteList = MutableLiveData<List<Event>>()
    var _favorites : LiveData<List<Event>> get() = favoriteList
    init {
        createRepos()
        getEvents()
        _favorites = _events
    }
    fun getEvents() {
        viewModelScope.launch {
            database.child(DB_EVENTS).get().addOnSuccessListener { snapshot ->
                Log.d("EventViewModel", "${database.child(DB_EVENTS)}")
                val eventList = snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                _events.postValue(eventList)
            }
        }
    }


    fun addEvent(event: Event, photoByteArray: ByteArray) {
        viewModelScope.launch {
            try {
                val key = UUID.randomUUID().toString()
                val uri = imageRepository.upload(key, photoByteArray)
                event.photourl = uri.toString()
                eventRepository.upload(key, event)
            } catch (e: Exception) {

            }
        }
    }
    
    fun editEvent(event: Event, photoByteArray: ByteArray, eventID: String) {
        viewModelScope.launch {
            try {
                val key = eventID
                val uri = imageRepository.upload(key, photoByteArray)
                event.photourl = uri.toString()
                eventRepository.upload(key, event)
            } catch (e: Exception) {
                
            }
        }
    }
    
    private fun createRepos() {
        eventRepository = EventRepository()
        imageRepository = ImageRepository()
    }


}