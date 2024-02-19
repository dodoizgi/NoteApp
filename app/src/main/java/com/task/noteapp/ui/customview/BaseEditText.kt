package com.task.noteapp.ui.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class BaseEditText : AppCompatEditText {
    var cursorAlwaysEnd = false

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    )

    init {
        includeFontPadding = false
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        if (cursorAlwaysEnd) {
            this.setSelection(this.text?.length ?: 0)
        } else {
            super.onSelectionChanged(selStart, selEnd)
        }
    }
}