package au.edu.swin.sdmd.emojinary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

private const val TAG = "LoginActivity"
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                // Show toast if email or password is empty
                Toast.makeText(this, "Email/password cannot be empty", Toast.LENGTH_SHORT).show()
                btnLogin.isEnabled = true
                return@setOnClickListener
            }
            // Firebase authentication check
            val auth = FirebaseAuth.getInstance()
            // This is an async operation, so we need an addOnCompleteListener
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                btnLogin.isEnabled = true
                if (task.isSuccessful) {
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    goTriviaActivity()
                } else {
                    // if login fails, log and toast that it failed with the exception
                    Log.e(TAG, "signInWithEmail failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun goTriviaActivity() {
        Log.i(TAG, "goTriviaActivity")
        /*
        val intent = Intent(this, TriviaActivity::class.java)
        startActivity(intent)
        finish()
         */
    }
}