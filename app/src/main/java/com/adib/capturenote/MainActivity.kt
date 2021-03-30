package com.adib.capturenote

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.adib.capturenote.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var etNewFolderName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fabNote.setOnClickListener {
            binding.fabMenu.close(true)
            val openNote = Intent(this@MainActivity, NoteActivity::class.java)
            openNote.putExtra(NoteActivity.STATUS, "create")
            startActivity(openNote)
        }

        binding.fabFolder.setOnClickListener {
            binding.fabMenu.close(true)
            createNewFolder()
        }

        showNotes()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        setMode(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    private fun setMode(selectedMode: Int) {
        when (selectedMode) {
            R.id.show_note -> {
                binding.fabMenu.close(true)
                showNotes()
            }
            R.id.show_folder -> {
                binding.fabMenu.close(true)
                showFolders()
            }
        }
    }

    private fun showFolders() {
        val noFolder = "You have no folder"
        if (getFolderList().size > 0) {
            binding.rvFolders.visibility = View.VISIBLE
            binding.tvNoRecords.visibility = View.INVISIBLE
            binding.rvNotes.visibility = View.INVISIBLE

            binding.rvFolders.layoutManager = GridLayoutManager(this, 3)
            val folderAdapter = FolderAdapter(this, getFolderList())
            binding.rvFolders.adapter = folderAdapter

            folderAdapter.setOnItemClickCallback(object: FolderAdapter.OnItemClickCallback{
                override fun onItemClicked(data: Folder) {
                    openSelectedFolder(data)
                }
            })
        } else {
            binding.rvFolders.visibility = View.INVISIBLE
            binding.tvNoRecords.visibility = View.VISIBLE
            binding.tvNoRecords.text = noFolder
            binding.rvNotes.visibility = View.INVISIBLE
        }
    }

    private fun getFolderList() : ArrayList<Folder> {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.viewFolder()
    }

    private fun openSelectedFolder(folder: Folder) {
        Toast.makeText(this, "Opening " + folder.name, Toast.LENGTH_SHORT).show()
        val openFolder = Intent(this@MainActivity, FolderActivity::class.java)
        openFolder.putExtra(FolderActivity.FOLDER, folder)
        startActivity(openFolder)
    }

    private fun createNewFolder() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("New Folder")
        val dialogLayout = inflater.inflate(R.layout.new_folder, null)
        etNewFolderName = dialogLayout.findViewById(R.id.et_new_folder_name)
        builder.setView(dialogLayout)
        builder.setPositiveButton("OK") { _: DialogInterface, _: Int ->
            val name = etNewFolderName.text.toString()
            val databaseHandler = DatabaseHandler(this)
            if (name.isNotEmpty()) {
                val status = databaseHandler.addFolder(Folder(0, name))
                if (status > -1) {
                    Toast.makeText(this, "$name created", Toast.LENGTH_SHORT).show()
                    showFolders()
                } else Toast.makeText(this, "Failed to create $name", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Folder name can't be blank", Toast.LENGTH_SHORT).show()
                createNewFolder()
            }
        }
        builder.show()
    }

    private fun showNotes() {
        val noNote = "You have no note"
        if (getNoteList().size > 0) {
            binding.rvNotes.visibility = View.VISIBLE
            binding.tvNoRecords.visibility = View.INVISIBLE
            binding.rvFolders.visibility = View.INVISIBLE

            binding.rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            val noteAdapter = NoteAdapter(this, getNoteList())
            binding.rvNotes.adapter = noteAdapter

            noteAdapter.setOnItemClickCallback(object: NoteAdapter.OnItemClickCallback{
                override fun onItemClicked(data: Note) {
                    openSelectedNote(data)
                }
            })
        } else {
            binding.rvNotes.visibility = View.INVISIBLE
            binding.tvNoRecords.visibility = View.VISIBLE
            binding.tvNoRecords.text = noNote
            binding.rvFolders.visibility = View.INVISIBLE
        }
    }

    private fun getNoteList(): ArrayList<Note> {
        val databaseHandler = DatabaseHandler(this)
        return databaseHandler.viewNote(-1)
    }

    private fun openSelectedNote(note: Note) {
        val openNote = Intent(this@MainActivity, NoteActivity::class.java)
        openNote.putExtra(NoteActivity.STATUS, "edit")
        openNote.putExtra(NoteActivity.NOTE, note)
        startActivity(openNote)
    }

    override fun onResume() {
        showNotes()
        super.onResume()
    }
}