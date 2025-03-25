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
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ContentMainBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.copenhagenbuzz.msem.View.CalendarFragment
import dk.itu.moapd.copenhagenbuzz.msem.View.FavoritesFragment
import dk.itu.moapd.copenhagenbuzz.msem.View.MapsFragment
import dk.itu.moapd.copenhagenbuzz.msem.View.ModalBottomSheet
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.View.TimelineFragment
import dk.itu.moapd.copenhagenbuzz.msem.View.LoginActivity
import dk.itu.moapd.copenhagenbuzz.msem.View.UserInfoDialogFragment


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
         val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
         val menuButton: View = findViewById(R.id.login)
        userButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        replaceFragment(TimelineFragment())

        makeNavigationBar()

        if (isLoggedIn) {
            makeBottomSheet()
            val navigationView = findViewById<NavigationView>(R.id.navigation_view)
            val menu = navigationView.menu
            val user = FirebaseAuth.getInstance().currentUser
            var mail = user?.email
            Log.d(TAG, "users $mail")
            menu.findItem(R.id.accountname_item).title = user?.displayName ?: "Anonymous"
            menu.findItem(R.id.accountmail_item).title = user?.email ?: "anonymous@gmail.com"
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
        userButton.setImageResource(R.drawable.baseline_menu_24)
    }

    override fun onStart() {
        super.onStart()

        auth.currentUser ?: startLoginActivity()
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
        startLoginActivity()
    }

}
