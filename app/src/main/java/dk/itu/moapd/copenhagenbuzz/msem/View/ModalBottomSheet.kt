package dk.itu.moapd.copenhagenbuzz.msem.View

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.location.Geocoder
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.Model.Event
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.BottomSheetContentBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.copenhagenbuzz.msem.DATABASE_URL
import dk.itu.moapd.copenhagenbuzz.msem.Model.EventLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.IOException
import java.util.Locale
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.ImageCaptureException
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.EventViewModel
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.tasks.await
import java.util.Date


class ModalBottomSheet : BottomSheetDialogFragment() {
    val REQUEST_IMAGE_CAPTURE = 42
    private lateinit var bottomBinding: BottomSheetContentBinding
    private val event: Event = Event("", EventLocation(), "", "", "", "", "")
    private lateinit var eventType: String
    private lateinit var dateRangeField: TextInputEditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val viewModel: EventViewModel by activityViewModels()
    private var imageCapture: ImageCapture? = null
    private lateinit var photoURI: String
    lateinit var bitmap: Bitmap
    private var photoByteArray: ByteArray = byteArrayOf(0)


    companion object {
        val TAG = "ModalBottomSheet"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        inflater.inflate(R.layout.bottom_sheet_content, container, false)
        bottomBinding = BottomSheetContentBinding.inflate(inflater)
        return bottomBinding.root

    }


    @OptIn(ExperimentalComposeUiApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setText()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        (dialog as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        val eventTypeDropdown = bottomBinding.eventTypeMenu // Use ViewBinding
        val eventTypes = resources.getStringArray(R.array.event_types)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, eventTypes)

        eventTypeDropdown.setAdapter(adapter)

        createTypePicker()

        /*bottomBinding.camera.buttonImageViewer.visibility = View.INVISIBLE
        bottomBinding.camera.buttonCameraSwitch.visibility = View.INVISIBLE
        bottomBinding.camera.buttonImageCapture.visibility = View.INVISIBLE
        launchCamera()*/

        // Getting the reference to the date picker UI element
        dateRangeField = bottomBinding.editTextEventDate

        // Sets up the DatePicker
        DateRangePicker()

        val bottomSheetDialog = dialog as? BottomSheetDialog
        val bottomSheet =
            bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        if (bottomSheet != null) {
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        setupClickBehavior()

        /*
        // Set up the listener for take photo button.
        bottomBinding.camera.buttonImageCapture.setOnClickListener {
            takePhoto()
        }*/

    }

    private fun setupClickBehavior() {
        with(bottomBinding) {
            addPictures.setOnClickListener {
                startTakePictureIntent()
            }
            bottomBinding.fabAddEvent.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    onAddEvent()
                }
            }
        }
    }

    private suspend fun onAddEvent() {
        val event = createEvent()

        if (!event.eventName.isNullOrEmpty() && photoByteArray.isNotEmpty()) {
            viewModel.addEvent(event, photoByteArray)

            showSnackbar("Successfully added event ${event.eventName}")
            Log.d(TAG, "Successfully added event ${event.eventName}")
            clearTextFields()
        } else {
            showSnackbar("Error: did you forget event name or photo?")
        }
    }

    private fun startTakePictureIntent() {
        if (checkPermissionsCamera()) {
            if (isCameraPermissionEnabled()) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                } catch (e: IOException) {
                    Log.e(TAG, "Error coould not get image", e)
                }
            }
        } else {
            requestCameraPermission()
        }
    }

    private fun isCameraPermissionEnabled(): Boolean {
        val permission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(requireContext(), permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE
        )
    }

    private fun checkPermissionsCamera(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

            photoByteArray = baos.toByteArray()
        }
    }


    private fun launchCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        bottomBinding.addPictures.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
                return@setOnClickListener
            }

            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(bottomBinding.camera.viewFinder.surfaceProvider)
                    }
                    val imageCapture = ImageCapture.Builder().build()
                    this.imageCapture = imageCapture
                    bottomBinding.camera.buttonImageViewer.visibility = View.VISIBLE
                    bottomBinding.camera.buttonCameraSwitch.visibility = View.VISIBLE
                    bottomBinding.camera.buttonImageCapture.visibility = View.VISIBLE

                    updateCameraSwitchButton(cameraProvider)

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            this,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to bind camera", e)
                    }
                }, ContextCompat.getMainExecutor(requireContext())
            )
        }
        bottomBinding.camera.buttonCameraSwitch.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                CameraSelector.DEFAULT_BACK_CAMERA
            else
                CameraSelector.DEFAULT_FRONT_CAMERA
            viewModel._selector.value = cameraSelector
            val cameraFacing =
                if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) "Back" else "Front"
            Log.d(TAG, "Switching Cameras $cameraFacing")
            launchCamera()
        }
    }

    private fun takePhoto() {
        val imgCapture = imageCapture ?: return
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "IMG_${timestamp}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }

        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

        imgCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    saveImage(output, filename)
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("TAG", "Photo capture failed: ${exception.message}")
                }
            }
        )
    }

    fun saveImage(output: ImageCapture.OutputFileResults, filename: String) {
        val savedUri = output.savedUri
        Log.d("TAG", "Photo saved at ${savedUri}")
        val file = File(
            requireContext().cacheDir,
            filename
        ) // Using cacheDir for a temporary file path
        bitmap = BitmapFactory.decodeStream(
            requireActivity().contentResolver.openInputStream(savedUri!!)
        )
    }

    fun saveImageToFirebaseStorage(bitmap: Bitmap) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "IMG_${timestamp}.jpg"

        val folder = bottomBinding.editTextEventName.text.toString()

        val storageReference = FirebaseStorage.getInstance().reference
            .child("${folder}/${filename}")
        Log.d(TAG, "${folder} her")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        storageReference.putBytes(data)
            .addOnSuccessListener {
                // Photo uploaded successfully
                Log.d("TAG", "Photo uploaded successfully to Firebase Storage")
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    photoURI = uri.toString()
                    Log.d("TAG", "Photo URI: $photoURI")
                    // Use this URL to reference the image in your database or elsewhere
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TAG", "Error uploading photo: ${exception.message}")
            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    override fun onStart() {
        super.onStart()

        val dialog = dialog as? BottomSheetDialog
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        dialog?.let {
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.halfExpandedRatio = 0.5f

                // Start i half-expanded
                // NB: STATE_HALF_EXPANDED kræver typisk, at du sætter `setHalfExpandedRatio()`
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // Gør den "draggable" (brugeren kan swipe op).
                behavior.isDraggable = true

                // Gør, at brugeren kan komme fra kollapset til fuld expanded
                behavior.skipCollapsed = false


                // For debugging kan du evt. sætte en callback på
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_COLLAPSED ->
                                Log.d("BS", "BottomSheet er kollapset")

                            BottomSheetBehavior.STATE_EXPANDED ->
                                Log.d("BS", "BottomSheet er fuldt udvidet")

                            BottomSheetBehavior.STATE_HALF_EXPANDED ->
                                Log.d("BS", "BottomSheet er i halv tilstand")

                            else -> Unit
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        // Kan bruges til animationer / lyt til offset
                    }
                })
            }
        }
    }

    private suspend fun createEvent(): Event {
        val name = bottomBinding.editTextEventName.text.toString()
        val date = bottomBinding.editTextEventDate.text.toString()
        val type = bottomBinding.eventTypeMenu.text.toString()
        val description = bottomBinding.editTextEventDiscription.text.toString()
        val location = getEventLocation()

        return Event(name, location, date, type, description)
    }

    private suspend fun getEventLocation(): EventLocation {
        val address = bottomBinding.editTextEventLocation.text.toString()
        val geo = Geocoder(requireContext(), Locale.getDefault())

        if (address.isNotBlank()) {
            geo.getFromLocationName(address, 1)
                ?.firstOrNull()
                ?.let { placemark ->
                    return EventLocation(
                        placemark.latitude,
                        placemark.longitude,
                        address
                    )
                }
        }
        val location = fusedLocationClient.lastLocation.await()
        if (location != null) {
            val line = geo
                .getFromLocation(location.latitude, location.longitude, 1)
                ?.firstOrNull()
                ?.getAddressLine(0).toString()
                .takeUnless(String::isNullOrBlank)
                ?: "Unknown location"

            return EventLocation(location.latitude, location.longitude, line)
        } else {
            return EventLocation(55.40, 72.9097, "default")
        }
    }

    private fun clearTextFields() {
        bottomBinding.editTextEventName.text?.clear()
        bottomBinding.editTextEventLocation.text?.clear()
        bottomBinding.editTextEventDate.text?.clear()
        bottomBinding.eventTypeMenu.text?.clear()
        bottomBinding.editTextEventDiscription.text?.clear()
    }

    fun addEventToDatabase(view: View) {
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database(DATABASE_URL).reference

        val eventName = bottomBinding.editTextEventName.text.toString()
        Log.d(TAG, "Event name: ${bottomBinding.editTextEventName.text.toString()}")
        val eventDate = bottomBinding.editTextEventDate.text.toString()
        val eventType = bottomBinding.eventTypeMenu.text.toString()
        val eventDescription = bottomBinding.editTextEventDiscription.text.toString()
        val userID = auth.currentUser?.uid.toString() // we know it is bad code okay
        var eventPhotourl = ""
        Log.d(TAG, "Event photo: $photoURI")
        if (photoURI.isNotEmpty()) {
            eventPhotourl = photoURI
        }


        if (eventName.isNotEmpty() && eventPhotourl.isNotEmpty()) {
            // Update the object attributes.
            event.eventName = eventName
            event.eventDate = eventDate
            event.eventType = eventType
            event.eventDescription = eventDescription
            event.userID = userID
            event.photourl = eventPhotourl
            // Calls the Snackbar so it gets shown when the button is clicked
            Snackbar(view, eventName, eventPhotourl)
            //Log the created event
            Log.d(TAG, "Event created ${event}")
            saveImageToFirebaseStorage(bitmap)

            auth.currentUser?.let { user ->
                val eventRef = database
                    .child("CopenhagenBuzz")
                    .child("events")
                    .push()

                eventRef.setValue(event)
            }
            bottomBinding.editTextEventName.setText("")
            bottomBinding.editTextEventLocation.setText("")
            bottomBinding.editTextEventDate.setText("")
            bottomBinding.eventTypeMenu.setText("")
            bottomBinding.editTextEventDiscription.setText("")
            photoURI = ""

        } else {
            Snackbar(view, eventName, eventPhotourl)
            Log.d(TAG, "${eventName} and  ${eventPhotourl}")
        }
    }

    /**
     * function takes a view and creates a snackbar with a message for when events are created.
     *
     * @parem view the current view
     */
    @Composable
    private fun showSnackbar(message: String) {
        parentFragment?.view?.let { view ->
            Snackbar.make(
                view,
                message,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    fun setText() {
        val sharedPreferences =
            requireContext().getSharedPreferences("EventPreferences", Context.MODE_PRIVATE)
        bottomBinding.editTextEventName.setText(sharedPreferences.getString("eventName", ""))
        Log.d(TAG, "Event name: ${bottomBinding.editTextEventName.text}")
        bottomBinding.editTextEventLocation.setText(
            sharedPreferences.getString(
                "eventLocation",
                ""
            )
        )
        bottomBinding.editTextEventDate.setText(sharedPreferences.getString("eventDate", ""))
        bottomBinding.editTextEventDiscription.setText(
            sharedPreferences.getString(
                "eventDescription",
                ""
            )
        )
        bottomBinding.eventTypeMenu.setText(sharedPreferences.getString("eventType", ""))
    }

    /**
     * Configures the dropdown menu for selecting an event type.
     */
    private fun createTypePicker() {
        val eventTypeMenu = bottomBinding.eventTypeMenu
        val eventTypes = resources.getStringArray(R.array.event_types)

        // Set up the dropdown adapter
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, eventTypes)
        eventTypeMenu.setAdapter(adapter)

        // Handle item selection
        eventTypeMenu.setOnItemClickListener { adapterView, _, position, _ ->
            eventType = adapterView.getItemAtPosition(position) as String
        }
    }


    /**
     * function creates a pop-up window with a calendar when the choose date field is clicked
     * this date range picker is taken from material components
     * "https://github.com/material-components/material-components-android/blob/master/docs/components/DatePicker.md"
     * When choosing a range of dates, the method will return this ranges in the event date field
     */
    fun DateRangePicker() {
        //Checks todays date to make the calendar starts at today. And to constrain the calendar from beginning to end of the year.
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calender = Calendar.getInstance(TimeZone.getFrozenTimeZone("UTC"))

        calender.timeInMillis = today
        calender[Calendar.MONTH] = Calendar.JANUARY
        val janThisYear = calender.timeInMillis

        calender.timeInMillis = today
        calender[Calendar.MONTH] = Calendar.DECEMBER
        val decThisYear = calender.timeInMillis

        //The constraintbuilder sets the point we start at and that we can only choose dates later than today
        val constraintsBuilder = CalendarConstraints.Builder()
            .setStart(janThisYear)
            .setEnd(decThisYear)
            .setValidator(DateValidatorPointForward.now())

        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getText(R.string.event_date))
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        // Sets up a click listener so the calendar prompt appears when accessing the field.
        dateRangeField.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, "date_range_picker")
        }

        /** Sets up a click listener that arranges the dates in the correct order
         * and saves the values to the event date field
         */
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            // The value returned when the user have chosen the dates and clicked save
            val (startDate, endDate) = selection

            // Formatting the date
            val format = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            val startString = format.format(startDate)
            val endString = format.format(endDate)
            val string: String = getString(R.string.date_range, startString, endString)


            // setting the text field  with a start date and an end date
            dateRangeField.setText(string)

        }
    }

    suspend fun geocodeAddress(
        context: Context,
        addressString: String
    ): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        try {
            withTimeout(10000) {
                val geo = Geocoder(context, Locale.getDefault())
                // Maks 1 resultat tilbage
                val results = geo.getFromLocationName(addressString, 1)
                if (!results.isNullOrEmpty()) {
                    val addr = results[0]
                    addr.latitude to addr.longitude
                } else {
                    null  // Intet resultat
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null  // Netværksfejl ell. mangel på Geocoder‐service
        }
    }

    private fun updateCameraSwitchButton(provider: ProcessCameraProvider) {
        bottomBinding.camera.buttonCameraSwitch.isEnabled = try {
            hasBackCamera(provider) && hasFrontCamera(provider)
        } catch (exception: CameraInfoUnavailableException) {
            false
        }
    }

    private fun hasFrontCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)

    private fun hasBackCamera(provider: ProcessCameraProvider) =
        provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

    override fun onStop() {
        super.onStop()
        val sharedPreferences =
            requireContext().getSharedPreferences("EventPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("eventName", bottomBinding.editTextEventName.text?.toString())
        editor.putString("eventLocation", bottomBinding.editTextEventLocation.text?.toString())
        editor.putString("eventDate", bottomBinding.editTextEventDate.text?.toString())
        editor.putString("eventType", bottomBinding.eventTypeMenu.text?.toString())
        editor.putString(
            "eventDescription",
            bottomBinding.editTextEventDiscription.text?.toString()
        )
        editor.apply()
    }


}

