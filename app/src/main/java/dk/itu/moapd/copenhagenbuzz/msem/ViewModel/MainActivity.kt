/*
* MIT License
*
* Copyright (c) [year] [fullname]
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

package dk.itu.moapd.copenhagenbuzz.msem.ViewModel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ActivityMainBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ContentMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dk.itu.moapd.copenhagenbuzz.msem.CalendarFragment
import dk.itu.moapd.copenhagenbuzz.msem.FavoritesFragment
import dk.itu.moapd.copenhagenbuzz.msem.MapsFragment
import dk.itu.moapd.copenhagenbuzz.msem.ModalBottomSheet
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.TimelineFragment
import dk.itu.moapd.copenhagenbuzz.msem.View.LoginActivity


/**
 * Activity class with methods that manage the  main activities of the CopenhagenBuzz app
 */
class MainActivity : AppCompatActivity() {

    /**
     * ViewBindings used to make the interaction between the code and our views easier.
     */
    private lateinit var gestureDetector: GestureDetector
    private lateinit var binding: ActivityMainBinding
    private lateinit var customBinding: ContentMainBinding

    /**
     * The companion object defines class level functions,
     * in this one we set TAG that is used when logging something in logcat.
     * The TAG shows in logcat wherefrom the log came. This one sets it as
     * coming from MainAcrivity.
     */
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * A set of private variables used in the class.
     */


    private lateinit var eventType: String

    var isLoggedIn: Boolean = false


    /**
     * Instantiation of an object of the `Event ` class.
     * which takes the input eventName, eventLocation, eventDate, eventType and eventDescription
     */

    /**
     * Called when activity is starting.Initializes UI elements and event listeners.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        // Initialize ViewBindings
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customBinding = ContentMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)


        // Find and sssigns a reference to the imagebutton
        val userButton = findViewById<ImageButton>(R.id.login)

        // Retrieves the boolean value from the LoginActivity wether it is true or false
        isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        // Updates the userbutton based on the isLoggedIn value
        updateUserIcon(userButton)

        /** Click lisnetner for the User button
         * Calls startActivity to launch LoginActivity
         * Calls finish to close MainActivity
         */
        userButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        replaceFragment(TimelineFragment())

        makeNavigationBar()

        if (isLoggedIn) {
            makeBottomSheet()
        }



    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container, fragment)
        fragmentTransaction.commit()

    }
    private fun makeNavigationBar() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    replaceFragment(TimelineFragment())
                    true
                }

                R.id.item_2 -> {
                    replaceFragment(FavoritesFragment())
                    Log.d(TAG, "Navigated to Favorites tab succesfully")
                    true
                }

                R.id.item_3 -> {
                    replaceFragment(MapsFragment())
                    true
                }

                R.id.item_4 -> {
                    replaceFragment(CalendarFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun makeBottomSheet() {
        makeGestureDetector()
        val swipeArea = findViewById<View>(R.id.swipeArea)
        swipeArea.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }

        val bottomSheet: View = findViewById(R.id.standard_bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Initially hide it
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> Log.d(TAG, "Bottom Sheet Expanded")
                    BottomSheetBehavior.STATE_COLLAPSED -> Log.d(TAG, "Bottom Sheet Collapsed")
                    BottomSheetBehavior.STATE_HIDDEN -> Log.d(TAG, "Bottom Sheet Hidden")
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Handle sliding effects if needed
            }
        })
    }

    private fun makeGestureDetector() {
        gestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (e1 != null && e2 != null) {
                        val deltaY = e2.y - e1.y
                        if (deltaY < -100) { // Swipe up detected
                            val myBottomSheet = ModalBottomSheet()
                            myBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
                            return true
                        }
                    }
                    return false
                }
            })
    }


    /**
     * This Method determines which pivture is shown for the icon wether it is a
     * logout icon our a guest symbol
     */
    private fun updateUserIcon(userButton: ImageButton) {
        if (isLoggedIn) {
            userButton.setImageResource(R.drawable.baseline_logout_24) // Logout icon
        } else {
            userButton.setImageResource(R.drawable.baseline_account_circle_24) // Login icon
        }
    }
}
