package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch
import androidx.camera.core.CameraSelector

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>(emptyList())
    private var favoriteList = MutableLiveData<List<Event>>()
    var _favorites : LiveData<List<Event>> get() = favoriteList
    val events: LiveData<List<Event>> = _events

    init {
        _favorites = _events
    }

    var _selector = MutableLiveData<CameraSelector>()

    val selector: LiveData<CameraSelector>
            get() = _selector


    fun onCameraSelectorChanged(selector: CameraSelector) {
            this._selector.value = selector
        }




}