package au.edu.swin.sdmd.emojinary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import au.edu.swin.sdmd.emojinary.models.Movie
import kotlinx.android.synthetic.main.item_trivia.view.*

// create adapter that takes two parameters: the context and list of trivia
// inherit it from the base adapter of RecyclerView.
class TriviaAdapter (val context: Context, val trivia: List<Movie>,
                     val listener: (Movie) -> Unit) :
    RecyclerView.Adapter<TriviaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_trivia, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = trivia.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(trivia[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(trivia: Movie) {
            itemView.setOnClickListener { listener(trivia) } // show the toast

            itemView.tvUsername.text = trivia.username
            // show the first answer on the card
            itemView.tvAnswer.text = trivia.answers[0]
            itemView.tvDifficulty.text = trivia.difficulty.toString()
            itemView.tvEmoji.text = trivia.emoji
        }
    }


}