package com.zanzigu.notes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.zanzigu.notes.databinding.ActivityMainBinding
import com.zanzigu.notes.databinding.DialogTextboxBinding
import com.zanzigu.notes.adapters.ViewPagerAdapter
import com.zanzigu.notes.data.Note
import com.zanzigu.notes.data.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myNoteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // viewPager
        myNoteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val adapter = ViewPagerAdapter(this@MainActivity, myNoteViewModel)
        binding.viewPager.adapter = adapter

        // setup viewPager's view-model
        myNoteViewModel.getCategories.observe(this) { categories ->
            adapter.setData(categories)

            // notes
            myNoteViewModel.getNotes.observe(this) { notes ->
                adapter.setNotes(notes)
            }
        }

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            tab.text = adapter.getCategoryByPos(pos)?.name
        }.attach()



        // floating action button ( + )
        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener {
            val dialogBinding: DialogTextboxBinding = DialogTextboxBinding.inflate(LayoutInflater.from(this))


            AlertDialog.Builder(this@MainActivity).also { builder ->
                builder.setView(dialogBinding.root)
                builder.setTitle(R.string.create_note)
                builder.setPositiveButton(R.string.add) { _, _ ->
                    val currentCategory = adapter.getCategoryByPos(binding.viewPager.currentItem)
                    if (currentCategory != null) {
                        // add new note
                        val inputText = dialogBinding.etDialog.text.toString()
                        myNoteViewModel.addNote(Note(0, inputText, adapter.itemCount, currentCategory.categoryID))
                    }
                    else {
                        // snack-bar
                        Snackbar.make(binding.root, R.string.no_categories_error, Snackbar.LENGTH_SHORT).show()
                    }

                }
                builder.setNegativeButton(R.string.cancel) { _, _ -> }
                builder.create().also { dialog ->
                    dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

                    // perform positive action on enter key
                    dialogBinding.etDialog.setOnEditorActionListener { _, actionID, _ ->
                        if (actionID == EditorInfo.IME_ACTION_DONE) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
                            return@setOnEditorActionListener true
                        }
                        return@setOnEditorActionListener true
                    }

                    dialog.show()
                }
            }
        }

        // top app bar
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.categories -> {
                    Intent(this, CategoriesActivity::class.java).also {
                        startActivity(it)
                    }
                    true
                }
                else -> false
            }
        }
    }
}