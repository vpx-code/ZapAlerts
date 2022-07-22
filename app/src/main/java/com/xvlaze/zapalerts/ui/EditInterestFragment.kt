package com.xvlaze.zapalerts.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.xvlaze.zapalerts.databinding.EditDialogBinding
import com.xvlaze.zapalerts.util.Constants

class EditInterestFragment : DialogFragment() {

    private lateinit var binding: EditDialogBinding
    private lateinit var viewModel: InterestsViewModel
    private lateinit var interestName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[InterestsViewModel::class.java]
        val fragmentBinding = EditDialogBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels)
        val height = (resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val confirmButton = binding.btnSave
        confirmButton.setOnClickListener {
            Toast.makeText(context, "Confirmed!", Toast.LENGTH_SHORT).show()

            viewModel.overwriteInterest(
                interestName,
                when (binding.settings.getFrequency()) { // FIXME: THIS IS SHIT! No puedo estar repitiendo este código cada vez.
                    0 -> {
                        Constants.AlertType.DAILY
                    }
                    1 -> {
                        Constants.AlertType.WEEKLY
                    }
                    2 -> {
                        Constants.AlertType.REALTIME
                    }
                    else -> {
                        Constants.AlertType.DAILY
                    }
                },
                binding.settings.getLang(),
                binding.settings.getCountry(),
                when (binding.settings.getType()) {
                    0 -> Constants.InterestType.Website
                    1 -> Constants.InterestType.Image
                    2 -> Constants.InterestType.Video
                    3 -> Constants.InterestType.News
                    else -> {
                        Constants.InterestType.News
                    }
                }
            )

            viewModel.isInterestSaved.observe(requireActivity()) { isSaveSuccessful ->
                if (isSaveSuccessful) {
                    Snackbar.make(
                        binding.root,
                        "Successfully edited Interest.",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Something wrong happened.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                dismiss()
            }
        }

        val deleteButton = binding.btnDelete
        deleteButton.setOnClickListener {
            viewModel.deleteInterest(interestName) // FIXME: Need to include this via an Intent (?)
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getString("interestName")?.let {
            interestName = it
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(interestName: String) = EditInterestFragment().apply {
            arguments = Bundle().apply {
                putString("interestName", interestName)
            }
        }
    }
}