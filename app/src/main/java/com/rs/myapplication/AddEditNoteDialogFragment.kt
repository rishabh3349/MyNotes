package com.rs.myapplication

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import android.view.WindowManager

class AddEditNoteDialogFragment(
    private val note: Note?,
    private val onSave: (String, String) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_edit_note, null)
        dialog.setContentView(view)

        val titleEditText = view.findViewById<EditText>(R.id.note_title)
        val contentEditText = view.findViewById<EditText>(R.id.note_content)
        val saveButton = view.findViewById<Button>(R.id.save_button)

        note?.let {
            titleEditText.setText(it.title)
            contentEditText.setText(it.content)
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val content = contentEditText.text.toString()
            onSave(title, content)
            dismiss()
        }

        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}
