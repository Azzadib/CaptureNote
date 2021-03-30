package com.adib.capturenote

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.adib.capturenote.databinding.ActivityNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    companion object {
        const val STATUS = "STATUS"
        const val NOTE = "note"
        const val FOLDER_ID = "folderId"
    }

    private lateinit var binding: ActivityNoteBinding
    private var status: String? = null
    private lateinit var note: Note
    private var folderId :Int = -1

    private lateinit var calendar: Calendar
    private lateinit var dateSimpleDateFormat: SimpleDateFormat
    private lateinit var timeSimpleDateFormat: SimpleDateFormat
    private lateinit var date: String
    private lateinit var time: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        status = intent.getStringExtra(STATUS)
        folderId = intent.getIntExtra(FOLDER_ID, -1)

        if (status == "edit") {
            note = intent.getParcelableExtra(NOTE)

            val noteCreated = "Created: ${note.created} "
            val noteUpdated = "Updated: ${note.updated} "

            binding.tvNoteCreated.visibility = View.VISIBLE
            binding.tvNoteUpdated.visibility = View.VISIBLE

            binding.tvNoteCreated.text = noteCreated
            binding.tvNoteUpdated.text = noteUpdated
            binding.noteTitle.setText(note.title)
            binding.noteContent.setText(note.content)
            binding.noteContent.hint = note.title
        }

        binding.noteTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                binding.noteContent.hint = "Note about $s"
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_note, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
            R.id.save_note -> {
                createNote()
            }
            R.id.delete_note -> {
                if (status == "edit") deleteDialog()
                else Toast.makeText(this, "You can't delete unsaved note", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNote() {
        val noteTitle = binding.noteTitle.text.toString()
        val noteContent = binding.noteContent.text.toString()
        val databaseHandler = DatabaseHandler(this)

        calendar = Calendar.getInstance()
        dateSimpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ROOT)
        timeSimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        date = dateSimpleDateFormat.format(calendar.time)
        time = timeSimpleDateFormat.format(calendar.time)

        val timeStamp = "$date at $time"

        if (noteTitle.isNotEmpty()) binding.noteContent.hint = noteTitle

        if (status == "edit") {
            if (noteTitle.isNotEmpty() or noteContent.isNotEmpty()) {
                val status = databaseHandler.updateNote(Note(note.id, noteTitle, noteContent, note.created, timeStamp, 0, "", note.folderId))
                if (status > -1) {
                    Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show()
                    finish()
                } else Toast.makeText(this, "Failed to update note", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Title or content can't be empty", Toast.LENGTH_SHORT).show()
        }

        else {
            if (noteTitle.isNotEmpty() or noteContent.isNotEmpty()) {
                val status = databaseHandler.addNote(Note(0, noteTitle, noteContent, timeStamp, timeStamp, 0, "", folderId))
                if (status > -1) {
                    Toast.makeText(this, "Note created", Toast.LENGTH_SHORT).show()
                    finish()
                } else Toast.makeText(this, "Failed to create note", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Title or content can't be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete note")
        builder.setMessage("Are you sure want to delete this note?")
        builder.setPositiveButton("Delete") { _: DialogInterface, _: Int -> deleteNote()}
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun deleteNote() {
        val databaseHandler = DatabaseHandler(this)
        val status = databaseHandler.deleteNote(Note(note.id))
        if (status > -1) {
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
            finish()
        } else Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Unsaved changes")
        builder.setMessage("Do you want to save this note?")
        builder.setPositiveButton("Save") { _: DialogInterface, _: Int -> createNote()}
        builder.setNegativeButton("Discard") { _: DialogInterface, _: Int -> super.onBackPressed()}
        builder.setNeutralButton("Cancel", null)
        builder.show()
    }
}