package com.zanzigu.notes.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zanzigu.notes.data.NotesCategory
import com.zanzigu.notes.databinding.ItemNoteBinding

class CategoriesRecyclerViewAdapter (
) : RecyclerView.Adapter<CategoriesRecyclerViewAdapter.RecyclerViewHolder>() {

    inner class RecyclerViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    private var categoryList = emptyList<NotesCategory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.binding.note.text = categoryList[position].name
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


    //
    fun setData(newCategories: List<NotesCategory>) {
        this.categoryList = newCategories
        notifyDataSetChanged()
    }
    fun getData(): List<NotesCategory> {
        return categoryList
    }
    fun getCategoryByPos(pos: Int): NotesCategory {
        return categoryList[pos]
    }
}
