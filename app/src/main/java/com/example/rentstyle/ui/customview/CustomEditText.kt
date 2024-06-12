package com.example.rentstyle.ui.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.example.rentstyle.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs){
    init {

        setOnFocusChangeListener { v, hasFocus ->
            val editText = v as EditText
            val parentLayout = editText.parent.parent as? TextInputLayout
            parentLayout?.errorIconDrawable = null

            if (!hasFocus) {
                if (editText.text.isEmpty()) {
                    parentLayout?.error = context.resources.getString(R.string.empty_field)
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ){
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (editText.text.isEmpty()) {
                        parentLayout?.error = context.resources.getString(R.string.empty_field)
                    } else if (inputType == 33 &&
                        !editText.text.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"))){
                        parentLayout?.error = context.resources.getString(R.string.invalid_email)
                    } else if (inputType == 129 &&
                        editText.text.count() in 1..7){
                        parentLayout?.error = context.resources.getString(R.string.password_length)
                    } else {
                        parentLayout?.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })

        }

        setOnEditorActionListener { v, actionId, _ ->
            val parentLayout = v.parent as? TextInputLayout
            parentLayout?.errorIconDrawable = null

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (v.text.isEmpty()) {
                    parentLayout?.error = context.resources.getString(R.string.empty_field)
                } else if (inputType == 33 &&
                    !v.text.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\$"))){
                    parentLayout?.error = context.resources.getString(R.string.invalid_email)
                } else if (inputType == 129 &&
                   v.text.count() in 1..7){
                    parentLayout?.error = context.resources.getString(R.string.password_length)
                } else {
                    parentLayout?.error = null
                }

                return@setOnEditorActionListener false
            }

            false
        }
    }

    override fun setError(error: CharSequence?, icon: Drawable?) {
        super.setError(error, null)
    }
}