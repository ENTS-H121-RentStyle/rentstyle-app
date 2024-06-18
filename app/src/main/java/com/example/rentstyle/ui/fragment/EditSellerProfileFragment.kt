package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentEditSellerProfileBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.viewmodel.SellerViewModel
import com.example.rentstyle.viewmodel.SellerViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class EditSellerProfileFragment : Fragment() {
    private lateinit var _binding: FragmentEditSellerProfileBinding
    private val binding get() = _binding

    private lateinit var viewModel: SellerViewModel

    private lateinit var pref: LoginSession
    private lateinit var sellerId: String
    private var run = true

    private lateinit var inputName: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputDesc: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditSellerProfileBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        binding.mainToolbar.apply {
            tvToolbarTitle.text = getString(R.string.title_update_profile)
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        val factory = SellerViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SellerViewModel::class.java]

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        binding.apply {
            inputName = edAddShopName
            inputAddress = edShopAddress
            inputDesc = edAddShopDesc
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sellerId = pref.getSellerId().first()!!
        }

        getSellerData()
        updateListener()

        return binding.root
    }

    private fun updateListener() {
        binding.btnUpdate.setOnClickListener {
            binding.ivLoadingSpinner.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }

            val name = inputName.text.toString()
            val address = inputAddress.text.toString()
            val desc = inputDesc.text.toString()

            if (name.isNotEmpty() &&
                address.isNotEmpty() && desc.isNotEmpty()) {

                val bodyUserName = name.toRequestBody("text/plain".toMediaType())
                val bodyAddress = address.toRequestBody("text/plain".toMediaType())
                val bodyDesc = desc.toRequestBody("text/plain".toMediaType())

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updateSellerProfile(sellerId,
                        bodyUserName, bodyAddress, bodyDesc).observe(viewLifecycleOwner){ result ->
                        if (result != null) {
                            when (result) {
                                is StatusResult.Loading -> { }

                                is StatusResult.Success -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()

                                    if (run) {
                                        run = false
                                        findNavController().navigateUp()
                                    }
                                }

                                is StatusResult.Error -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

            } else {
                binding.ivLoadingSpinner.isVisible = false
                Toast.makeText(requireContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSellerData() {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val userId = pref.getUserId().first().toString()

            viewModel.getSellerData(userId).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> { }

                        is DataResult.Success -> {
                            val data = result.data

                            updateSellerData(data.sellerName!!, data.address!!, data.description!!, data.image!!)
                        }

                        is DataResult.Error -> {
                            binding.ivLoadingSpinner.isVisible = false
                            Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateSellerData(sellerName: String,
                                 address: String,
                                 desc: String,
                                 image: String) {
        Glide.with(requireContext())
            .load(image).into(binding.ivShopImage)

        inputName.setText(sellerName)
        inputAddress.setText(address)
        inputDesc.setText(desc)

        binding.ivLoadingSpinner.isVisible = false
    }
}