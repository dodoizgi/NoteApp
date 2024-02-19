package com.task.noteapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.task.noteapp.data.viewmodel.HomeViewModel
import com.task.noteapp.databinding.FragmentHomeBinding
import com.task.noteapp.ui.adapter.NoteListAdapter
import com.task.noteapp.ui.dialog.AddNoteDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val addNoteDialog: AddNoteDialog by lazy {
        AddNoteDialog(requireContext(), viewLifecycleOwner,
            onSaveClicked = {
                viewModel.insertData(it)
                addNoteDialog.dismiss()
                adapter.notifyDataSetChanged()
            },
            onUpdateClicked = {
                viewModel.updateData(it)
                addNoteDialog.dismiss()
                adapter.notifyDataSetChanged()
            }
        )
    }

    private val adapter: NoteListAdapter by lazy {
        NoteListAdapter(
            onNoteClicked = { addNoteDialog.show(it, true) },
            onNoteDeleteClicked = {
                viewModel.deleteItem(it)
                adapter.notifyDataSetChanged()
            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel.getAllData.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data)
            binding.recyclerView.scheduleLayoutAnimation()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerview()

        initView()
    }

    private fun initView() = with(binding) {
        btnAdd.setOnClickListener {
            addNoteDialog.show(null, false)
        }
    }

    private fun setupRecyclerview() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}