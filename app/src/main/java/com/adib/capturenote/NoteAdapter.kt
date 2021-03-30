package com.adib.capturenote

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val context: Context, private val items: ArrayList<Note>):
    RecyclerView.Adapter<NoteAdapter.NoteHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    class NoteHolder(view: View): RecyclerView.ViewHolder(view) {
        val cvCardNote: CardView = view.findViewById(R.id.cv_card_note)
        val tvNoteTitle: TextView = view.findViewById(R.id.tv_note_title)
        val tvNoteContent: TextView = view.findViewById(R.id.tv_note_content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder(
            LayoutInflater.from(context).inflate(R.layout.card_note, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val item = items[position]
        when {
            position % 3 == 0 -> holder.cvCardNote.setCardBackgroundColor(Color.parseColor("#FFF600"))
            position %2 == 0 -> holder.cvCardNote.setCardBackgroundColor(Color.parseColor("#00FF0C"))
            else -> holder.cvCardNote.setCardBackgroundColor(Color.parseColor("#FF007C"))
        }
        holder.tvNoteTitle.text = item.title
        holder.tvNoteContent.text = item.content

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(items[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = items.size

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Note)
    }
}