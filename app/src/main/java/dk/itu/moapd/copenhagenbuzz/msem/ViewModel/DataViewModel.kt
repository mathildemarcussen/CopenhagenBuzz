package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch

class DataViewModel : ViewModel() {
    lateinit var eventList : MutableLiveData<Event>
    val events : LiveData<Event> get() = eventList

    fun resetCont() {
        val EmptyEvent = Event("","","","","")
        eventList.value = (EmptyEvent)
    }

    suspend fun fetchOrInit() {
        viewModelScope.launch{
            if (eventList.isInitialized()) {
                events
            } else {
                resetCont()
            }
        }
    }




}