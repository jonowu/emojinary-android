package au.edu.swin.sdmd.emojinary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.swin.sdmd.emojinary.models.Movie
import au.edu.swin.sdmd.emojinary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create.*

private var signedInUser: User? = null
private const val TAG = "CreateActivity"

class CreateActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Get the extras (if there are any)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(EXTRA_TRIVIA)) {
                val movie = intent.getParcelableExtra<Movie>(EXTRA_TRIVIA)!!
                etEmoji.setText(movie.emoji)
                rbDifficulty.rating = movie.difficulty.toFloat()
                etAnswer.setText(movie.answers[0])
                btnSubmit.text = "Update"
            }
        }

        firestoreDb = FirebaseFirestore.getInstance() // points to the root of the db
        // set signedInUser to the currently signed in user
        firestoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get the currently logged in user", exception)
            }

        btnSubmit.setOnClickListener {
            handleSubmitButtonClick()
        }

    }

    private fun handleSubmitButtonClick() {
        // Data validation
        if (etEmoji.text.isBlank()) {
            Toast.makeText(this, "Emoji cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (rbDifficulty.rating < 1) {
            Toast.makeText(this, "Difficulty must be selected", Toast.LENGTH_SHORT).show()
            return
        }
        if (etAnswer.text.isBlank()) {
            Toast.makeText(this, "Answer cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser == null) {
            Toast.makeText(this, "User is not signed in, please wait", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false

        val answers = mutableListOf<String>()
        answers.add(etAnswer.text.toString())
        // Create movie object with the provided details
        val trivia =  Movie(
            "",
            etEmoji.text.toString(),
            rbDifficulty.rating.toInt(),
            answers,
            signedInUser!!.username
        )
        if (btnSubmit.text == "Update") {
            // update the entry
            Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show()

        } else {
            // add it to Firebase
            firestoreDb.collection("movies")
                .add(trivia)
                .addOnSuccessListener {
                    // navigate back to profile screen
                    val profileIntent = Intent(this, ProfileActivity::class.java)
                    profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                    startActivity(profileIntent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error adding trivia", exception)
                }
        }

    }
}