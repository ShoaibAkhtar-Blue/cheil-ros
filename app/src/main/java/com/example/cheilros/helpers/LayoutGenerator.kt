package com.example.cheilros.helpers

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout

class LayoutGenerator {
    var context: Context? = null
    lateinit var objectsArray: Array<String>

    fun LayoutGenerator(context: Context?) {
        this.context = context
    }

    fun addEditText(hint: String?, id: Int, qid: Int): EditText? {
        val et = EditText(context)
        et.hint = hint
        et.id = id
        et.width = LinearLayout.LayoutParams.MATCH_PARENT
        et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //qa.saveAnswers(qid, et.text.toString())
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        return et
    }
}