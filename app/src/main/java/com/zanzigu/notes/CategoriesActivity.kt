package com.zanzigu.notes

import android.app.AlertDialog
import android.graphics.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zanzigu.notes.adapters.CategoriesRecyclerViewAdapter
import com.zanzigu.notes.data.NoteViewModel
import com.zanzigu.notes.data.NotesCategory
import com.zanzigu.notes.databinding.ActivityCategoriesBinding
import com.zanzigu.notes.databinding.DialogTextboxBinding
import java.util.*


class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var myNoteViewModel: NoteViewModel
    private lateinit var adapter: CategoriesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // top app bar
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }

        // categories recyclerView
        adapter = CategoriesRecyclerViewAdapter()
        binding.rvCategories.adapter = adapter
        binding.rvCategories.layoutManager = LinearLayoutManager(this@CategoriesActivity)

        // setup categories view-model
        myNoteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        myNoteViewModel.getCategories.observe(this) { categories ->
            adapter.setData(categories)
            myITH.attachToRecyclerView(binding.rvCategories)
        }

        // floating action button ( + )
        binding.fabAdd.setOnClickListener {
            val dialogBinding: DialogTextboxBinding = DialogTextboxBinding.inflate(LayoutInflater.from(this))
            AlertDialog.Builder(this).also { builder ->
                builder.setView(dialogBinding.root)
                builder.setTitle(R.string.create_category)
                builder.setPositiveButton(R.string.add) { _, _ ->
                    // add category
                    val inputText = dialogBinding.etDialog.text
                    myNoteViewModel.addCategory(
                        NotesCategory(0, inputText.toString(), adapter.itemCount)
                    )
                }
                builder.setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
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
    }

    // Drag event handler for recyclerview
    private val myITH = ItemTouchHelper(
        object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            private lateinit var categories: List<NotesCategory>

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                // swap positions
                if (! this::categories.isInitialized || categories.isEmpty())
                    categories = adapter.getData()

                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition

                Collections.swap(categories, fromPos, toPos)
                for (i in categories.indices) {
                    categories[i].categoryPos = i
                }

                adapter.notifyItemMoved(fromPos, toPos)
                return false // true if moved, false otherwise
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // update DB only if there has been changes
                if (this::categories.isInitialized)
                    myNoteViewModel.updateCategories(categories)
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val category = adapter.getCategoryByPos(viewHolder.adapterPosition)

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        // delete category
                        AlertDialog.Builder(this@CategoriesActivity).also { builder ->
                            builder.setTitle("${getString(R.string.confirm_deleting_category)}: ${category.name} ?")
                            builder.setPositiveButton(R.string.delete) { _, _ ->
                                // delete note
                                myNoteViewModel.deleteCategory(category)
                                categories = emptyList()
                            }
                            builder.setNegativeButton(R.string.cancel) {_, _ ->
                            }
                            builder.setOnDismissListener {
                                adapter.notifyItemChanged(viewHolder.adapterPosition)
                            }
                            builder.create().also { dialog ->
                                dialog.show()
                            }
                        }
                    }
                    ItemTouchHelper.RIGHT -> {
                        // edit category
                        val dialogBinding: DialogTextboxBinding = DialogTextboxBinding.inflate(LayoutInflater.from(this@CategoriesActivity))
                        dialogBinding.etDialog.setText(category.name)
                        dialogBinding.etDialog.setSelection(dialogBinding.etDialog.text.length)

                        AlertDialog.Builder(this@CategoriesActivity).also { builder ->
                            builder.setView(dialogBinding.root)
                            builder.setTitle(R.string.edit_category)
                            builder.setPositiveButton(R.string.edit) { _, _ ->
                                category.name = dialogBinding.etDialog.text.toString()
                                myNoteViewModel.updateCategory(category)
                            }
                            builder.setNegativeButton(R.string.cancel) { _, _ -> }
                            builder.setOnDismissListener {
                                adapter.notifyItemChanged(viewHolder.adapterPosition)
                            }
                            builder.create().also { dialog ->
                                dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                                dialog.show()
                            }
                        }
                    }
                }
            }

            // draw icons on horizontal swipe
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // icons management

                if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }

                val swipingLeft: Boolean = (dX <= 0)
                val iv = viewHolder.itemView

                if (swipingLeft) {
                    // delete icon
                    val icon = AppCompatResources.getDrawable(this@CategoriesActivity,
                        R.drawable.ic_round_delete_outline_24
                    )
                    if (icon == null) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        return
                    }

                    val icMargin = (iv.height - icon.intrinsicHeight) / 2
                    icon.setBounds(
                        iv.right - icMargin - icon.intrinsicWidth,
                        iv.top + icMargin,
                        iv.right - icMargin,
                        iv.top + icMargin + icon.intrinsicHeight
                    )

                    icon.draw(c)
                }
                else {
                    // edit icon
                    val icon = AppCompatResources.getDrawable(this@CategoriesActivity,
                        R.drawable.ic_round_edit_24
                    )
                    if (icon == null) {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        return
                    }

                    val icMargin = (iv.height - icon.intrinsicHeight) / 2
                    icon.setBounds(
                        iv.left + icMargin,
                        iv.top + icMargin,
                        iv.left + icMargin + icon.intrinsicWidth,
                        iv.top + icMargin + icon.intrinsicHeight
                    )

                    icon.draw(c)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    )
}