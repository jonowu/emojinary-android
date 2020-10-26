package au.edu.swin.sdmd.emojinary

import android.content.Intent
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import au.edu.swin.sdmd.emojinary.models.Movie
import au.edu.swin.sdmd.emojinary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_create.*

private var signedInUser: User? = null
private const val TAG = "CreateActivity"
// these are character types that emoji unicodes can be made out of
private val VALID_CHAR_TYPES = listOf(
    Character.SURROGATE,
    Character.NON_SPACING_MARK,
    Character.OTHER_SYMBOL
).map { it.toInt() }.toSet()
class CreateActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // restrict input length to 100 chars and only to emojis
        // each emoji is a unicode, which consists of multiple characters, up to 4
        val emojiFilter = EmojiFilter()
        val lengthFilter = InputFilter.LengthFilter(100)
        etEmoji.filters = arrayOf(lengthFilter, emojiFilter)

        // Get the extras (if there are any)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey(EXTRA_TRIVIA)) {
                val movie = intent.getParcelableExtra<Movie>(EXTRA_TRIVIA)!!
                etEmoji.setText(movie.emoji)
                rbDifficulty.rating = movie.difficulty.toFloat()
                etAnswer.setText(movie.answers[0])
                btnSubmit.text = "Update"
                btnDelete.visibility = View.VISIBLE // make the delete button visible
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
        btnDelete.setOnClickListener() {
            handleDeleteButtonClick()
        }


    }


    inner class EmojiFilter : InputFilter {
        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
            if (source == null || source.isBlank()) {
                return ""
            }
            Log.i(TAG, "Text added $source, it has a length of ${source.length} characters")
            val validCharTypes = listOf(Character.SURROGATE, Character.NON_SPACING_MARK, Character.OTHER_SYMBOL).map { it.toInt() }
            // check that every character input matches the valid char types
            for (inputChar in source) {
                val type = Character.getType(inputChar)
                Log.i(TAG, "Character type $type")
                if (!VALID_CHAR_TYPES.contains(type)) {
                    Toast.makeText(this@CreateActivity, "Only emojis are allowed", Toast.LENGTH_SHORT).show()
                    return ""
                }
            }
            // The CharSequence being added is a valid emoji, so allow it to be added
            return source
        }
    }

    private fun handleDeleteButtonClick() {
        Toast.makeText(this, "Delete  selected", Toast.LENGTH_SHORT).show()
        return
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
        val newTrivia =  Movie(
            "",
            etEmoji.text.toString(),
            rbDifficulty.rating.toInt(),
            answers,
            signedInUser!!.username
        )

        val extras = intent.extras
        if (extras!!.containsKey(EXTRA_TRIVIA)) { // If trivia is being updated
            val movie = intent.getParcelableExtra<Movie>(EXTRA_TRIVIA)!!
            Toast.makeText(this, "Updating...", Toast.LENGTH_SHORT).show()
            firestoreDb.collection("movies").document(movie.documentId)
                .set(newTrivia, SetOptions.merge())
                .addOnSuccessListener { val profileIntent = Intent(this, ProfileActivity::class.java)
                    profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                    startActivity(profileIntent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        } else { // add the newTrivia to Firebase
            firestoreDb.collection("movies")
                .add(newTrivia)
                .addOnSuccessListener {
                    // navigate back to profile screen
                    val profileIntent = Intent(this, ProfileActivity::class.java)
                    profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                    startActivity(profileIntent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }

    }
}