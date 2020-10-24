package au.edu.swin.sdmd.emojinary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import au.edu.swin.sdmd.emojinary.models.Movie
import au.edu.swin.sdmd.emojinary.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "TriviaActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"
class TriviaActivity : AppCompatActivity() {

    // get currently signed in user from Firebase
    private var signedInUser: User? = null
    // this is a lateinit var because it is initalised in onCreate,
    // and once it is initialised it should never be null.
    private lateinit var firestoreDb: FirebaseFirestore
    // create mutable list of movie trivia objects
    // needs to mutable so firebase can update it
    private lateinit var trivia: MutableList<Movie>
    private lateinit var adapter: TriviaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia)

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