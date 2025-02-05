package dk.itu.moapd.copenhagenbuzz.msem.Model

data class Event(
    var eventName: String,
    var eventLocation: String,
    var eventDate: String,
    var eventType: String,
    var eventDescription: String

) {


    override fun toString(): String {
        return "Event(eventName='$eventName ', eventLocation='$eventLocation ', eventDate='$eventDate', evenType='$eventType', eventDescription='$eventDescription')"
        }
}