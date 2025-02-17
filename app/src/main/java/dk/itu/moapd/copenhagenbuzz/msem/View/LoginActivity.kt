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


class LoginActivity : AppCompatActivity() {
    private lateinit var customBinding: ContentLoginBinding


    private lateinit var binding: ActivityLoginBinding
    public var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        customBinding = ContentLoginBinding.inflate(layoutInflater)
        setContentView(customBinding.root)

        customBinding.login.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply{
            putExtra("isLoggedIn", true)
            }
            startActivity(intent)
        }
        customBinding.guest.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java).apply{
            putExtra("isLoggedIn", false)
            }
            startActivity(intent)
        }
    }



}