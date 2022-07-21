package com.xvlaze.zapalerts.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.xvlaze.zapalerts.R

class EditInterestFragment: DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner);
        return inflater.inflate(R.layout.edit_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels)
        val height = (resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val confirmButton = dialog!!.findViewById<Button>(R.id.btn_save)
        confirmButton.setOnClickListener {
            Toast.makeText(context, "Confirmed!", Toast.LENGTH_SHORT).show()
        }

        val deleteButton = dialog!!.findViewById<Button>(R.id.btn_delete)
        deleteButton.setOnClickListener {
            Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
        }
    }

}