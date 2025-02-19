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

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dk.itu.moapd.copenhagenbuzz.msem.R
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ActivityLoginBinding
import dk.itu.moapd.copenhagenbuzz.msem.databinding.ContentLoginBinding
import dk.itu.moapd.copenhagenbuzz.msem.ViewModel.MainActivity
import android.content.Intent

/**
 * Activity class with methods that manage the Login activities of the CopenhagenBuzz app
 */
class LoginActivity : AppCompatActivity() {

    /**
     * ViewBindings used to make the interaction between the code and our views easier.
     */
    private lateinit var customBinding: ContentLoginBinding
    private lateinit var binding: ActivityLoginBinding

    public var isLoggedIn: Boolean = false

    /**
     * Called when activity is starting.Initializes UI elements and event listeners.
     *
     * @param savedInstanceState The saved instance state if the activity is being re-initialized.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allows our UI to extand to the edges of the screen
        enableEdgeToEdge()

        // Loads the Ui from the activity login xml file
        setContentView(R.layout.activity_login)

        //Prevents UI overlap that enableEdgeToEdge() can cause
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewBindings
        customBinding = ContentLoginBinding.inflate(layoutInflater)
        setContentView(customBinding.root)

        /** Click lisnetner for the login button
         * When the login in button is clicked the boolean isLoggedIn is se to true
         * Calls startActivity to launch MainActivity
         * Calls finish to close LoginActivity
         */
        customBinding.login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply{
            putExtra("isLoggedIn", true)
            }
            startActivity(intent)
            finish()
        }

        /** Click lisnetner for the guest button
         * When the guest in button is clicked the boolean isLoggedIn is se to false
         * Calls startActivity to launch MainActivity
         * Calls finish to close LoginActivity
         */
        customBinding.guest.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java).apply{
            putExtra("isLoggedIn", false)
            }
            startActivity(intent)
            finish()
        }
    }



}