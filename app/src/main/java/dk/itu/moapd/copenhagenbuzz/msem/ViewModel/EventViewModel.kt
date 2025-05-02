package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import kotlinx.coroutines.launch
import androidx.camera.core.CameraSelector
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dk.itu.moapd.copenhagenbuzz.msem.DATABASE_URL
import java.util.UUID

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>(emptyList())
    private var favoriteList = MutableLiveData<List<Event>>()
    var _favorites : LiveData<List<Event>> get() = favoriteList
    val events: LiveData<List<Event>> = _events
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance(DATABASE_URL).reference

    init {
        _favorites = _events
    }

    var _selector = MutableLiveData<CameraSelector>()

    val selector: LiveData<CameraSelector>
            get() = _selector


    fun onCameraSelectorChanged(selector: CameraSelector) {
            this._selector.value = selector
        }


    fun addEvent(event: Event, photoByteArray: ByteArray) {
        viewModelScope.launch {
            try {
                val key = UUID.randomUUID().toString()
                val uri = uploadImage(photoByteArray)
                event.photourl = uri.toString()
                database.child("CopenhagenBuzz").child("events").child(key).setValue(event)
            } catch (e: Exception) {

            }
        }
    }
    fun uploadImage(photoByteArray: ByteArray): String {

    }



}