package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>(emptyList())
    val events: LiveData<List<Event>> = _events

    init {
        mockEventsAsync()
    }

    private fun mockEventsAsync() {
        viewModelScope.launch {
            _events.postValue(listOf(
                Event(
                    eventName = "Copoaw",
                    eventLocation = "Copenhagen",
                    eventDate = "22 februar",
                    eventType = "party :))",
                    eventDescription = "aaaaaaaaaaaaaa"
                ),
                Event(
                    eventName = "Copenhagen Light Festival",
                    eventLocation = "Copenhagen",
                    eventDate = "1 marts",
                    eventType = "Festival",
                    eventDescription = "Beautiful light instalations all over Copenahgen"
                ),
                Event(
                    eventName = "Tate Mcrae",
                    eventLocation = "Royal Arena",
                    eventDate = "30 maj",
                    eventType = "concert",
                    eventDescription = "Pop Concert"
                )
            )
            )
        }
    }



}