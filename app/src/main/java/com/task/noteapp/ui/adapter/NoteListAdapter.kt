package com.task.noteapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.task.noteapp.databinding.ItemNoteBinding
import com.task.noteapp.domain.model.Note

class NoteListAdapter(
    private val onNoteClicked: ((Note) -> Unit)? = null,
    private val onNoteDeleteClicked: ((Note) -> Unit)? = null

) : ListAdapter<Note, NoteListAdapter.NoteListViewHolder>(NoteListAdapterItemsDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteListViewHolder(
        ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class NoteListViewHolder(
        private val binding: ItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) = with(binding) {
            binding.note = note
            binding.ivDelete.setOnClickListener {
                onNoteDeleteClicked?.invoke(note)

            }
            root.setOnClickListener {
                onNoteClicked?.invoke(note)
            }
            binding.executePendingBindings()
        }
    }

    object NoteListAdapterItemsDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.title == newItem.title
        }
    }
}