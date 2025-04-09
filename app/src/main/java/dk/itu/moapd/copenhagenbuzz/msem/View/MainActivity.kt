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

package dk.itu.moapd.copenhagenbuzz.msem.View

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.R


/**
 * Activity class with methods that manage the  main activities of the CopenhagenBuzz app
 */
class MainActivity : AppCompatActivity() {

    /**
     * ViewBindings used to make the interaction between the code and our views easier.
     */
    private lateinit var gestureDetector: GestureDetector
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

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

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController



        // Find and sssigns a reference to the imagebutton
        val userButton = binding.root.findViewById<ImageButton>(R.id.login)

        // Retrieves the boolean value from the LoginActivity wether it is true or false
        isLoggedIn = intent.getBooleanExtra("isLoggedIn", false)

        // Updates the userbutton based on the isLoggedIn value
        updateUserIcon(userButton)

        /** Click lisnetner for the User button
         * Calls startActivity to launch LoginActivity
         * Calls finish to close MainActivity
         */
         val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        userButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            makeBottomSheet()
            val navigationView = findViewById<NavigationView>(R.id.navigation_view)
            val menu = navigationView.menu
            menu.findItem(R.id.accountname_item).title = user?.displayName ?: "Anonymous"
            menu.findItem(R.id.accountmail_item).title = user?.email ?: "anonymous@gmail.com"
            menu.findItem(R.id.signin_signout_item).title = "Sign Out"

        }
        auth = FirebaseAuth.getInstance()

        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.signin_signout_item -> {
                    Log.d(TAG, "Sign in/out clicked from drawer")
                    signinSignout()
                    true
                }
                // Her kan du også håndtere andre navigation items, hvis du vil
                else -> false
            }
        }

        // Sets up the bottom navigation view with the navigation controller
        binding.bottomNavigation.setupWithNavController(navController)


    }

    /**
     * This method sets up the bottom sheet behavior and swipe gesture
     * detection for the bottom sheet.
     * It initializes the gesture detector, sets up a touch listener on the swipe area
     * that handle swipe gestures,
     * configures the bottom sheet behavior, initially hides the bottom sheet,
     * and adds a callback to log bottom sheet state changes.
     */
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

        // Add a callback to log bottom sheet state changes
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

    /**
     * This method initializes a GestureDetector to detect swipe-up gestures.
     * When a swipe-up gesture is detected, it displays the modalBottomSheet.
     */
    private fun makeGestureDetector() {
        gestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                // Function that is called when a swipe-up gesture is detected
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (e1 != null && e2 != null) {
                        val deltaY = e2.y - e1.y
                        if (deltaY < -100) { // Swipe up detected
                            val myBottomSheet = ModalBottomSheet() // Create an instance of the modal bottom sheet
                            myBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG) // Show the bottom sheet
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
        userButton.setImageResource(R.drawable.baseline_menu_24)
    }

    override fun onStart() {
        super.onStart()
    }

    private fun startLoginActivity() {
        Intent(this, LoginActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }.let(::startActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.signin_signout_item -> {
                Log.d(TAG, "Item Selected")
                signinSignout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun signinSignout() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startLoginActivity()
        } else {
            FirebaseAuth.getInstance().signOut()
            val user2 = FirebaseAuth.getInstance().currentUser
            val navigationView = findViewById<NavigationView>(R.id.navigation_view)
            val menu = navigationView.menu
            menu.findItem(R.id.accountname_item).title = user2?.displayName ?: "Anonymous"
            menu.findItem(R.id.accountmail_item).title = user2?.email ?: "anonymous@gmail.com"
            menu.findItem(R.id.signin_signout_item).title = "Sign In"
        }
    }

}
