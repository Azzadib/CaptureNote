package com.adib.capturenote

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adib.capturenote.databinding.ActivityFolderBinding

class FolderActivity : AppCompatActivity() {
    companion object {
        const val FOLDER = "folder"
    }

    private lateinit var binding: ActivityFolderBinding
    private lateinit var folder: Folder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFolderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        folder = intent.getParcelableExtra(FOLDER)
        showNotes()

        binding.fabAddNoteInFolder.setOnClickListener {
            val openNote = Intent(this@FolderActivity, NoteActivity::class.java)
            openNote.putExtra(NoteActivity.STATUS, "create")
            openNote.putExtra(NoteActivity.FOLDER_ID, folder.id)
            startActivity(openNote)
        }
    }

    private fun getNoteList(): ArrayList<Note> {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.viewNote(folder.id)
    }

    private fun showNotes() {
        if (getNoteList().size > 0) {
            binding.rvNotesInFolder.visibility = View.VISIBLE
            binding.tvNoNoteInFolder.visibility = View.INVISIBLE

            binding.rvNotesInFolder.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            val noteAdapter = NoteAdapter(this, getNoteList())
            binding.rvNotesInFolder.adapter = noteAdapter

            noteAdapter.setOnItemClickCallback(object: NoteAdapter.OnItemClickCallback{
                override fun onItemClicked(data: Note) {
                    openSelectedNote(data)
                }
            })
        } else {
            binding.rvNotesInFolder.visibility = View.INVISIBLE
            binding.tvNoNoteInFolder.visibility = View.VISIBLE
        }
    }

    private fun openSelectedNote(note: Note) {
        val openNote = Intent(this@FolderActivity, NoteActivity::class.java)
        openNote.putExtra(NoteActivity.STATUS, "edit")
        openNote.putExtra(NoteActivity.NOTE, note)
        startActivity(openNote)
    }

    override fun onResume() {
        showNotes()
        super.onResume()
    }
}