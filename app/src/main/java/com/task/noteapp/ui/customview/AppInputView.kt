package com.task.noteapp.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.widget.doAfterTextChanged
import androidx.customview.view.AbsSavedState
import com.task.noteapp.R
import com.task.noteapp.databinding.LayoutAppInputViewBinding
import com.task.noteapp.utils.ext.orZero

open class AppInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    val focusListenerList = arrayListOf<OnFocusChangeListener>()

    var binding: LayoutAppInputViewBinding =
        LayoutAppInputViewBinding.inflate(LayoutInflater.from(context), rootView as ViewGroup)
    private var validation: (() -> Boolean)? = null
    private var oldEndIcon: Int? = null
    var isAutoChangeActive = true

    init {
        setAttrs(attrs)
        binding.editText.setOnFocusChangeListener { _, hasFocus ->
            autoClearError()
            focusListenerList.forEach {
                it.onFocusChange(this, hasFocus)
            }
        }
    }

    fun setSelection(index: Int) {
        binding.editText.setSelection(index)
    }

    inline fun doAfterTextChanged(crossinline action: (text: Editable?) -> Unit) {
        binding.editText.doAfterTextChanged {
            action(it)
        }
    }

    private fun setBgViaState() {
        setTextBg(R.drawable.bg_edittext_state_normal)
    }

    fun setTextBg(@DrawableRes id: Int) {
        binding.textAreaLayout.setBackgroundResource(id)
    }

    private fun setErrorEndIcon() {
        binding.imageEndIcon.setImageResource(R.drawable.ic_input_error)
        binding.imageEndIcon.visibility = VISIBLE
    }

    private fun setAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.AppInputView
            )
            typedArray.run {
                binding.editText.hint =
                    typedArray.getString(R.styleable.AppInputView_android_hint)
                        ?: ""

                binding.textViewTitle.apply {
                    text = typedArray.getString(R.styleable.AppInputView_title)
                        ?: ""
                    visibility = if (text.toString().isNotEmpty()) View.VISIBLE else View.GONE
                }

                binding.textViewInfo.apply {
                    text = typedArray.getString(R.styleable.AppInputView_infoText)
                        ?: ""
                    visibility = if (text.toString().isNotEmpty()) View.VISIBLE else View.GONE
                }

                (typedArray.getString(R.styleable.AppInputView_prefix))?.let {
                    setPrefix(it)
                }

                typedArray.getString(R.styleable.AppInputView_android_text)?.let {
                    setText(it)
                }

                typedArray.getString(R.styleable.AppInputView_android_digits)?.let {
                    binding.editText.keyListener = DigitsKeyListener.getInstance(it)
                }

                setPrefixIcon(getResourceId(R.styleable.AppInputView_prefixIcon, 0))

                setEndIcon(getResourceId(R.styleable.AppInputView_endIcon, 0))

                typedArray.getInt(R.styleable.AppInputView_android_inputType, 0).let {
                    if (it != 0) {
                        binding.editText.apply {
                            val originalTypeFace = typeface
                            inputType = it
                            typeface = originalTypeFace
                        }
                    }
                }
                val filters = arrayListOf<InputFilter>()
                typedArray.getInt(R.styleable.AppInputView_android_maxLength, 0).let {
                    if (it != 0) {
                        filters.add(LengthFilter(it))
                    }
                }
                binding.editText.filters = filters.toTypedArray()
                typedArray.getInt(R.styleable.AppInputView_android_imeOptions, 0).let {
                    if (it != 0) {
                        binding.editText.imeOptions = it
                    }
                }
                typedArray.getDimension(R.styleable.AppInputView_android_textSize, 0f).let {
                    if (it != 0f) {
                        binding.textViewPrefix.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
                        binding.editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
                    }
                }
                isEnabled = typedArray.getBoolean(R.styleable.AppInputView_android_enabled, true)
            }
            typedArray.recycle()
        }
    }

    fun setText(text: String?) {
        binding.editText.setText(text)
        validation?.let {
            validate(validation = it)
        } ?: kotlin.run {
            clearError()
        }
    }

    fun setText(@IdRes resourceId: Int?) {
        resourceId?.let {
            binding.editText.setText(context.getString(it))
        }
        validation?.let {
            validate(validation = it)
        } ?: kotlin.run {
            clearError()
        }
    }

    fun setHint(text: String) {
        binding.editText.hint = text
    }

    fun setHint(id: Int) {
        binding.editText.hint = resources.getString(id)
    }

    fun setTitle(text: String) {
        binding.textViewTitle.text = text
    }

    fun setTitle(id: Int) {
        binding.textViewTitle.text = resources.getString(id)
    }

    fun getText(): String {
        return StringBuilder()
            .append(binding.textViewPrefix.text.toString())
            .append(binding.editText.text.toString()).toString()
    }

    fun setPrefix(text: String) {
        binding.textViewPrefix.text = text
        binding.textViewPrefix.visibility = VISIBLE
    }

    private fun setPrefixIcon(resourceId: Int) {
        if (resourceId > 0) {
            binding.imageStartIcon.setImageResource(resourceId)
            binding.imageStartIcon.visibility = VISIBLE
        }
    }

    fun setEndIcon(resourceId: Int) {
        if (resourceId > 0) {
            oldEndIcon = resourceId
            binding.imageEndIcon.setImageResource(resourceId)
            binding.imageEndIcon.tag = resourceId
            binding.imageEndIcon.visibility = VISIBLE
        }
    }

    fun setPrefixIconDrawable(drawable: Drawable) {
        binding.imageStartIcon.setImageDrawable(drawable)
        binding.imageStartIcon.visibility = VISIBLE
    }

    fun setPrefixIconClickListener(clickListener: OnClickListener) {
        binding.imageStartIcon.setOnClickListener(clickListener)
        binding.textViewPrefix.setOnClickListener(clickListener)
        binding.imageClickablePrefix.setOnClickListener(clickListener)
        binding.imageClickablePrefix.visibility = VISIBLE
    }

    fun setEndIconClickListener(clickListener: OnClickListener) {
        binding.imageEndIcon.setOnClickListener(clickListener)
    }

    fun setError(textResourceId: Int) {
        binding.textViewError.text = resources.getString(textResourceId)
        binding.textViewError.visibility = VISIBLE
        setErrorEndIcon()
        setTextBg(R.drawable.bg_edittext_state_error)
    }

    fun setError(textResource: String) {
        binding.textViewError.text = textResource
        binding.textViewError.visibility = VISIBLE
        setErrorEndIcon()
        setTextBg(R.drawable.bg_edittext_state_error)
    }

    fun clearError() {
        binding.textViewError.text = ""
        binding.textViewError.visibility = GONE
        clearErrorEndIcon()
        setBgViaState()
    }

    fun autoClearError() {
        if (isAutoChangeActive) {
            binding.textViewError.text = ""
            binding.textViewError.visibility = GONE
            clearErrorEndIcon()
            setBgViaState()
        }
    }

    private fun clearErrorEndIcon() {
        if (oldEndIcon.orZero() >= 1) {
            oldEndIcon?.let {
                binding.imageEndIcon.setImageResource(it)
                binding.imageEndIcon.visibility = VISIBLE
            }
        } else {
            binding.imageEndIcon.visibility = GONE
        }
    }

    fun validate(
        errorMessage: Int = R.string.default_edit_text_error,
        validateInBackground: Boolean = false,
        validation: (() -> Boolean),
    ): Boolean {
        this.validation = validation
        if (validateInBackground) {
            return validation()
        }
        return if (validation()) {
            clearError()
            true
        } else {
            setError(errorMessage)
            false
        }
    }

    fun onEditorAction(actionCode: Int) {
        binding.editText.onEditorAction(actionCode)
    }

    fun setOnEditorActionListener(l: OnEditorActionListener) {
        binding.editText.setOnEditorActionListener(l)
    }

    fun closeKeyboard() {
        val inputManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            binding.editText.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    fun disableChildViews(vg: ViewGroup) {
        for (i in 0 until vg.childCount) {
            val child = vg.getChildAt(i)
            child.isClickable = false
            child.isFocusable = false
            child.setOnTouchListener { _, event -> onTouchEvent(event) }
            if (child is ViewGroup) {
                disableChildViews(child)
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        when (enabled) {
            true -> {
                binding.editText.isFocusableInTouchMode = true
                binding.textAreaLayout.setBackgroundResource(R.drawable.bg_edittext)
            }

            false -> {
                binding.editText.isFocusable = false
                binding.textAreaLayout.setBackgroundResource(R.drawable.bg_edittext_disabled)
            }
        }
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        binding.editText.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        superState?.let {
            val state = SavedState(superState)
            state.value = binding.editText.text.toString()
            return state
        } ?: run {
            return superState
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                super.onRestoreInstanceState(state.superState)
                binding.editText.setText(state.value)
                // Restore view's state here
            }

            else -> {
                super.onRestoreInstanceState(state)
            }
        }
    }

    fun setInputFilter(filter: InputFilter) {
        binding.editText.filters = arrayOf(filter)
    }

    fun setInputType(type: Int) {
        binding.editText.inputType = type
    }

    class SavedState : AbsSavedState {
        var value: String? = null

        constructor(superState: Parcelable) : super(superState)

        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
            value = source.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(value)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.ClassLoaderCreator<SavedState> =
                object : Parcelable.ClassLoaderCreator<SavedState> {
                    override fun createFromParcel(source: Parcel, loader: ClassLoader): SavedState {
                        return SavedState(source, loader)
                    }

                    override fun createFromParcel(source: Parcel): SavedState {
                        return SavedState(source, null)
                    }

                    override fun newArray(size: Int): Array<SavedState> {
                        return newArray(size)
                    }
                }
        }
    }
}