package com.zanzigu.notes.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.zanzigu.notes.data.Note
import com.zanzigu.notes.data.NoteViewModel
import com.zanzigu.notes.databinding.DialogTextboxBinding
import com.zanzigu.notes.databinding.ItemNoteBinding
import com.zanzigu.notes.databinding.BottomsheetEditnoteBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.zanzigu.notes.R

class RecyclerViewAdapter (
    private val categoryID: Int,
    private val myNoteViewModel: NoteViewModel
    ) : RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    inner class RecyclerViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    private var noteList= emptyList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.binding.note.text = noteList[position].title

        // bottom-sheet handling
        val context = holder.binding.root.context
        holder.binding.note.setOnClickListener {
            val editNoteDialog = BottomSheetDialog(context)
            val bottomSheetBinding = BottomsheetEditnoteBinding.inflate(LayoutInflater.from(context), holder.binding.root, false)
            val note = noteList[position]

            bottomSheetBinding.tvTitle.text = noteList[position].title

            // event listeners
            bottomSheetBinding.btnEdit.setOnClickListener {
                // Edit
                val dialogTextboxBinding = DialogTextboxBinding.inflate(LayoutInflater.from(context), holder.binding.root, false)
                dialogTextboxBinding.etDialog.setText(note.title)
                dialogTextboxBinding.etDialog.setSelection(dialogTextboxBinding.etDialog.text.length)

                AlertDialog.Builder(context).also { builder ->
                    builder.setTitle(R.string.edit_note)
                    builder.setView(dialogTextboxBinding.root)
                    builder.setPositiveButton(R.string.edit) { _, _ ->
                        // apply edit
                        val inputText = dialogTextboxBinding.etDialog.text.toString()
                        note.title = inputText

                        myNoteViewModel.updateNote(note)

                        editNoteDialog.dismiss()
                    }
                    builder.create().also { dialog ->
                        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

                        // perform positive action on enter key
                        dialogTextboxBinding.etDialog.setOnEditorActionListener { _, actionID, _ ->
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
            bottomSheetBinding.btnDelete.setOnClickListener {
                // Delete
                AlertDialog.Builder(context).also { builder ->
                    builder.setTitle("${context.resources.getString(R.string.confirm_deleting_note)}: ${note.title} ?")
                    builder.setPositiveButton(R.string.delete) { _, _ ->
                        // delete note
                        myNoteViewModel.deleteNote(note)
                        editNoteDialog.dismiss()
                    }
                    builder.setNegativeButton(R.string.cancel) { _, _ -> }
                    builder.create().also { dialog ->
                        dialog.show()
                    }
                }
            }
            bottomSheetBinding.btnCategory.setOnClickListener {
                // Edit category
                val categories = myNoteViewModel.getCategories.value
                var categoriesTitles = arrayOf<String>()
                var currentCategoryPos: Int = -1

                for (i in categories!!.indices) {
                    categoriesTitles += categories[i].name

                    if (categories[i].categoryID == note.categoryID)
                        currentCategoryPos = i
                }

                AlertDialog.Builder(context).also { builder ->
                    builder.setTitle(R.string.edit_category)
                    builder.setSingleChoiceItems(categoriesTitles, currentCategoryPos) { dialog, pos ->
                        note.categoryID = categories[pos].categoryID
                        myNoteViewModel.updateNote(note)

                        dialog.dismiss()
                        editNoteDialog.dismiss()
                    }
                    builder.create().also { dialog ->
                        dialog.show()
                    }
                }
            }

            editNoteDialog.setContentView(bottomSheetBinding.root)
            editNoteDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    //
    fun setData(newNoteList: List<Note>) {
        this.noteList = listOf()
        for (note in newNoteList) {
            if (note.categoryID == categoryID) {
                noteList = noteList + note
            }
        }

        notifyDataSetChanged()
    }

    fun getData(): List<Note> {
        return noteList
    }
}