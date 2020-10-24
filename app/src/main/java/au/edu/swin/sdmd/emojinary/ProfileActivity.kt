package au.edu.swin.sdmd.emojinary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import au.edu.swin.sdmd.emojinary.models.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_trivia.*

private const val TAG = "ProfileActivity"
private const val EXTRA_USERNAME = "EXTRA_USERNAME"

class ProfileActivity : AppCompatActivity() {

    // this is a lateinit var because it is initalised in onCreate,
    // and once it is initialised it should never be null.
    private lateinit var firestoreDb: FirebaseFirestore
    // create mutable list of movie trivia objects
    // needs to mutable so firebase can update it
    private lateinit var trivia: MutableList<Movie>
    private lateinit var adapter: TriviaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Create the layout file for each trivia item - Finished!
        // Create data source
        // during onCreate, initially set trivia as empty list
        trivia = mutableListOf()

        // Create the adapter
        adapter = TriviaAdapter(this, trivia)

        // Bind the adapter and layout manager to the Recycler View
        rvTrivia.adapter = adapter
        rvTrivia.layoutManager = LinearLayoutManager(this)

        // Query Firestore to retrieve data

        // get the username from the intent extras
        val username = intent.getStringExtra(EXTRA_USERNAME)
        //val username = "Jonathan"
        firestoreDb = FirebaseFirestore.getInstance() // points to the root of the db
        var moviesReference = firestoreDb
            .collection("movies") // go to movies collection
            .whereEqualTo("username", username) // only include the trivia where the username field matches the current user


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
            // update the adapter
            adapter.notifyDataSetChanged()
            for (movie in movieList) {
                Log.i(TAG, "Movie ${movie}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_logout) {
            Log.i(TAG, "User is logging out.")
            FirebaseAuth.getInstance().signOut()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}