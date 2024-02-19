package com.task.noteapp.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.bottomsheets.setPeekHeight
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.afollestad.materialdialogs.utils.MDUtil.getWidthAndHeight
import com.task.noteapp.R.dimen.radius_24dp
import com.task.noteapp.databinding.DialogAddNoteBinding
import com.task.noteapp.domain.model.Note
import com.task.noteapp.utils.DateUtils
import java.time.LocalDate

class AddNoteDialog(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onSaveClicked: ((Note) -> Unit)? = null,
    private val onUpdateClicked: ((Note) -> Unit)? = null
) {

    private var isUpdate: Boolean = false
    private var note: Note? = null
    private val addNoteDialogBinding by lazy {
        DialogAddNoteBinding.inflate(LayoutInflater.from(context))
    }

    private val addNoteDialog by lazy {
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).apply {
            initView()
            window?.windowManager?.getWidthAndHeight()?.let { (_, height) ->
                setPeekHeight(height)
            }
            cornerRadius(
                (radius_24dp).toFloat(),
                radius_24dp
            )
            customView(view = addNoteDialogBinding.root, noVerticalPadding = true)
            lifecycleOwner(lifecycleOwner)
        }
    }

    private fun initView() = with(addNoteDialogBinding) {
        buttonApply.setOnClickListener {
            if (validateUIData()) {
                if (isUpdate) {
                    note?.title = etNoteTitle.getText()
                    note?.description = etDescription.getText()
                    note?.url = etImageUrl.getText()
                    note?.isChanged = true
                    note?.let { onUpdateClicked?.invoke(it) }
                } else
                    onSaveClicked?.invoke(
                        Note(
                            0,
                            etNoteTitle.getText(),
                            etDescription.getText(),
                            etImageUrl.getText(),
                            DateUtils.format(
                                DateUtils.parse(
                                    LocalDate.now().toString(),
                                    DateUtils.P3
                                ), DateUtils.P2
                            ).orEmpty(),
                            false
                        )
                    )
            }
        }
    }


    fun validateUIData(): Boolean = with(addNoteDialogBinding) {
        val isTitleOk = etNoteTitle.validate { etNoteTitle.getText().isNotBlank() }
        val isDescriptionOk = etDescription.validate { etDescription.getText().isNotBlank() }

        addNoteDialog.window?.windowManager?.getWidthAndHeight()?.let { (_, height) ->
            addNoteDialog.setPeekHeight(height)
        }

        return isTitleOk && isDescriptionOk
    }

    fun show(note: Note? = null, isUpdate: Boolean) = with(addNoteDialogBinding) {
        note?.let { this@AddNoteDialog.note = it }
        this@AddNoteDialog.isUpdate = isUpdate
        etNoteTitle.setText(note?.title ?: "")
        etDescription.setText(note?.description ?: "")
        etImageUrl.setText(note?.url ?: "")
        addNoteDialog.show()
    }

    fun dismiss() {
        addNoteDialog.dismiss()
    }
}
