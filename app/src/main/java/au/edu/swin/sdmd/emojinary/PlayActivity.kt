package au.edu.swin.sdmd.emojinary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import au.edu.swin.sdmd.emojinary.models.Movie
import au.edu.swin.sdmd.emojinary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.item_trivia.*
import kotlinx.android.synthetic.main.item_trivia.tvEmoji

private const val TAG = "PlayActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"
class PlayActivity : AppCompatActivity() {

    // get currently signed in user from Firebase
    private var signedInUser: User? = null
    // this is a lateinit var because it is initalised in onCreate,
    // and once it is initialised it should never be null.
    private lateinit var firestoreDb: FirebaseFirestore
    // create mutable list of movie trivia objects
    // needs to mutable so firebase can update it
    private lateinit var trivia: MutableList<Movie>
    private var currentMovie: Movie? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        // during onCreate, initially set trivia as empty list
        trivia = mutableListOf()

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

        // get the movies
        var moviesReference = firestoreDb
            .collection("movies") // go to movies collection
                // snapshot listener asks Firebase to inform you of any changes to the collection,
        // it has 2 parameters for the async callback, snapshot and exception
        moviesReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "An exception occured when querying movies", exception)
                // return early when something goes wrong
                return@addSnapshotListener
            }
            // map the the list of movies and translate it to a list of Movie class objects.
            val movieList = snapshot.toObjects(Movie::class.java)
            // clear old data
            trivia.clear()
            // add data from Firebase to trivia list
            trivia.addAll(movieList)
            Log.i(TAG, trivia.toString())
            currentMovie = trivia.random() // randomise the movie
            tvEmoji.text = currentMovie!!.emoji // update the ui with the current emoji
        }

        btnCheckAnswer.setOnClickListener {
            var correct = false
            if (currentMovie != null) {
                currentMovie!!.answers.forEach {// loop through the possible answers
                    Log.i(TAG, it)

                    if (it.equals(etAnswer.text.toString(), ignoreCase = true)) {
                        correct = true
                        trivia.remove(currentMovie!!)
                        currentMovie = trivia.random()
                        tvEmoji.text = currentMovie!!.emoji // update the ui with the current emoji
                        etAnswer.setText("")
                    }

                    if (correct) {
                        Toast.makeText(applicationContext, "Correct!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Incorrect :/", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    fun nextMovie() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // call menuInflater and inflate the menu resource file
        menuInflater.inflate(R.menu.menu_trivia, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // if the user taps on the profile icon
        if (item.itemId == R.id.menu_profle) {
            // navigate to the ProfileActivity using an intent
            val intent = Intent(this, ProfileActivity::class.java)
            // pass in an extra into the intent, which is the username
            // so that the user only sees trivia that they created
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}