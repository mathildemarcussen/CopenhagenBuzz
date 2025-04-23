package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>(emptyList())
    private var favoriteList = MutableLiveData<List<Event>>()
    var _favorites : LiveData<List<Event>> get() = favoriteList
    val events: LiveData<List<Event>> = _events

    init {
        mockEventsAsync()
        _favorites = _events
    }

    private fun mockEventsAsync() {
        viewModelScope.launch {
            _events.postValue(listOf(
                Event(
                    eventName = "Copoaw",
                    eventLocation = "Copenhagen",
                    eventDate = "22 februar",
                    eventType = "party :))",
                    eventDescription = "aaaaaaaaaaaaaa",
                    userID = "SkALyXvex3ejLzSVYPMg3DAOBd33"
                ),
                Event(
                    eventName = "Copenhagen Light Festival",
                    eventLocation = "Copenhagen",
                    eventDate = "1 marts",
                    eventType = "Festival",
                    eventDescription = "Beautiful light instalations all over Copenahgen",
                    userID = "n4C45w7HF8S5mJcUw5DGCsu32kA3"
                ),
                Event(
                    eventName = "Tate Mcrae",
                    eventLocation = "Royal Arena",
                    eventDate = "30 maj",
                    eventType = "concert",
                    eventDescription = "Pop Concert",
                    userID = "vdwocALo8pPcybQtQn6wmXpHiQc2"
                )
            )
            )
        }
    }



}