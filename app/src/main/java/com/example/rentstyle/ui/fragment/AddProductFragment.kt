package com.example.rentstyle.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentAddProductBinding

class AddProductFragment : Fragment() {
    private lateinit var _binding: FragmentAddProductBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        binding.mainToolbar.apply {
            tvToolbarTitle.text = "Add product"
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Choose", "Adat", "Formal", "Cosplay", "Pesta")
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerProductCategory.adapter = adapter

        binding.ivProductImage.setOnClickListener {
            showDialogBox()
        }

        return binding.root
    }

    private fun showDialogBox() {
        val dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.add_product_dialog_box)
        dialogView.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_custom))
        dialogView.setCanceledOnTouchOutside(true)

        dialogView.show()
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onPause() {
        super.onPause()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
}