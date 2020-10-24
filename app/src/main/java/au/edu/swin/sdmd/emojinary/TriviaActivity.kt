package au.edu.swin.sdmd.emojinary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class TriviaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trivia)
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
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}