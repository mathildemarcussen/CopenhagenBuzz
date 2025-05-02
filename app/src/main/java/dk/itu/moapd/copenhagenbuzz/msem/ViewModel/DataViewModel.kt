package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    private var eventList = MutableLiveData<List<Event>>()
    private var favoriteList = MutableLiveData<List<Event>>()
    val _favorites : LiveData<List<Event>> get() = favoriteList
    val _events: LiveData<List<Event>> get() = eventList


    fun resetCont() {
        eventList.value = emptyList()
        favoriteList.value = emptyList()

    }

    fun fetchOrInit() {
        viewModelScope.launch {
            if (eventList.isInitialized) {
                _events
            }
            if (favoriteList.isInitialized) {
                val favorites = generateRandomFavorites(eventList.value ?: emptyList())
                favoriteList.postValue(favorites)
                _favorites
            } else {
                resetCont()
            }
        }
    }
    private fun generateRandomFavorites(events: List <Event >): List <Event > {
        val shuffledIndices = (events.indices).shuffled().take(2).sorted()
        return shuffledIndices.mapNotNull { index -> events.getOrNull(index) }
    }

}