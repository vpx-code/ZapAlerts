package com.xvlaze.zapalerts.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.xvlaze.zapalerts.databinding.EditDialogBinding

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

        viewModel.getInterestInfo(interestName)
        viewModel.interestToEdit.observe(this) { interest ->
            Log.d("ZAP_TAG", "About to edit interest...")
            binding.settings.setFreqSelection(interest.frequency.toInt())
            binding.settings.setLangSelection(interest.language.toInt())
            binding.settings.setCountrySelection(interest.country.toInt())

            val confirmButton = binding.btnSave
            confirmButton.setOnClickListener {
                interest.country = binding.settings.getCountry().toString()
                interest.language = binding.settings.getLang().toString()
                interest.frequency = binding.settings.getFrequency().toString()
                viewModel.editInterest(
                    interest
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
                viewModel.deleteInterest(interest)
                dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getString("interestName")?.let {
            interestName = it
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (activity as DialogInterface.OnDismissListener).onDismiss(dialog)
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