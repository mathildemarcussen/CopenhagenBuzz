package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    lateinit var eventList : MutableLiveData<Event>
    lateinit var favoriteList : MutableLiveData<Event>
    val events : LiveData<Event> get() = eventList
    val favorites : LiveData<Event> get() = favoriteList

    fun resetCont() {
        val EmptyEvent = Event("","","","","")
        eventList.value = (EmptyEvent)
        favoriteList.value = (EmptyEvent)

    }

    suspend fun fetchOrInit() {
        viewModelScope.launch {
            if (eventList.isInitialized()) {
                events
            } else {
                resetCont()
            }
            if (favoriteList.isInitialized()) {
                favorites

            } else {
                resetCont()
            }
        }


    }

    private fun generateRandomFavorites(events: List <Event >): List <Event > {
        val shuffledIndices = (events.indices).shuffled().take(1).sorted()
        return shuffledIndices.mapNotNull { index -> events.getOrNull(index) }
        }

}