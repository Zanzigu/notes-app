package com.zanzigu.notes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zanzigu.notes.data.Note
import com.zanzigu.notes.data.NoteViewModel
import com.zanzigu.notes.data.NotesCategory
import com.zanzigu.notes.databinding.FragmentRecyclerviewnotesBinding
import java.util.*

class ViewPagerAdapter(
    private val context: Context,
    private val myNoteViewModel: NoteViewModel
    ) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    private var categoryList = emptyList<NotesCategory>()
    private var noteAdapters = listOf<RecyclerViewAdapter>()
    private var allNotes = listOf<Note>()

    inner class ViewPagerViewHolder(val binding: FragmentRecyclerviewnotesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = FragmentRecyclerviewnotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val adapter = RecyclerViewAdapter(categoryList[position].categoryID, myNoteViewModel)
        noteAdapters += adapter

        val rvNotes = holder.binding.rvNotes
        rvNotes.adapter = noteAdapters.last()
        rvNotes.layoutManager = LinearLayoutManager(context)
        noteAdapters.last().setData(allNotes)

        getITH(adapter).attachToRecyclerView(rvNotes)

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


    //
    fun setData(newCategories: List<NotesCategory>) {
        this.categoryList = newCategories
        notifyDataSetChanged()
    }
    fun getCategoryByPos(pos: Int): NotesCategory? {
        if (categoryList.size > pos)
            return categoryList[pos]
        return null
    }
    fun setNotes(newNotesList: List<Note>) {
        this.allNotes = newNotesList
        for (noteAdapter in noteAdapters) {
            noteAdapter.setData(newNotesList)
        }
    }

    // Drag event handler for recyclerview
    private fun getITH(adapter: RecyclerViewAdapter): ItemTouchHelper {
        return ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                0
            ) {
                private lateinit var notes: List<Note>

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
                ): Boolean {
                    if (!this::notes.isInitialized)
                        notes = adapter.getData()

                    // swap notes pos
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition

                    Collections.swap(notes, fromPos, toPos)

                    for (i in notes.indices) {
                        notes[i].pos = i
                    }

                    adapter.notifyItemMoved(fromPos, toPos)
                    return false
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    // update DB only if there has been changes
                    if (this::notes.isInitialized)
                        myNoteViewModel.updateNotes(notes)
                }


                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // remove from adapter
                }
            }
        )
    }
}