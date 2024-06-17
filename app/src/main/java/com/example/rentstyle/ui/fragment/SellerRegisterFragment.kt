package com.example.rentstyle.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentSellerRegisterBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.ImageFileHelper.reduceFileImage
import com.example.rentstyle.helpers.ImageFileHelper.uriToFile
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.viewmodel.SellerRegisterViewModel
import com.example.rentstyle.viewmodel.SellerViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class SellerRegisterFragment : Fragment() {
    private lateinit var _binding: FragmentSellerRegisterBinding
    private val binding get() = _binding

    private lateinit var viewModel: SellerRegisterViewModel

    private lateinit var inputName: EditText
    private lateinit var inputAddress: EditText
    private lateinit var inputDesc: EditText
    private lateinit var inputCity: EditText

    private lateinit var pref: LoginSession
    private lateinit var userId: String
    private var run = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerRegisterBinding.inflate(inflater, container, false)

        pref = LoginSession.getInstance(requireActivity().application.dataStore)
        lifecycleScope.launch {
            userId = pref.getUserId().first()!!
        }

        binding.apply {
            inputName = edAddShopName
            inputAddress = edShopAddress
            inputDesc = edAddShopDesc
            inputCity = edAddShopCity
        }

        val factory = SellerViewModelFactory.getInstance(this.requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[SellerRegisterViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            val name = inputName.text.toString()
            val address = inputAddress.text.toString()
            val desc = inputDesc.text.toString()
            val city = inputCity.text.toString()

            if (name.isNotEmpty() &&
                address.isNotEmpty() &&
                desc.isNotEmpty() &&
                city.isNotEmpty()) {

                val imageUri = getDrawableUri(requireContext(), R.drawable.img_placeholder)
                val imageFile = uriToFile(imageUri, requireContext()).reduceFileImage()
                val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestImageFile
                )

                val bodyUserName = name.toRequestBody("text/plain".toMediaType())
                val bodyUserId = userId.toRequestBody("text/plain".toMediaType())
                val bodyAddress = address.toRequestBody("text/plain".toMediaType())
                val bodyDesc = desc.toRequestBody("text/plain".toMediaType())
                val bodyCity = city.toRequestBody("text/plain".toMediaType())

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.registerSellerAccount(multipartBody,
                        bodyUserName, bodyUserId,
                        bodyAddress, bodyDesc, bodyCity).observe(viewLifecycleOwner){ result ->
                        if (result != null) {
                            when (result) {
                                is DataResult.Loading -> { }

                                is DataResult.Success -> {
                                    Toast.makeText(requireContext(), "Registering account is success", Toast.LENGTH_SHORT).show()

                                    lifecycleScope.launch {
                                        if (result.data.id?.isNotEmpty() == true) pref.setSellerId(result.data.id)
                                    }

                                    if (run && result.data.id?.isNotEmpty() == true) {
                                        run = false
                                        findNavController().navigate(OnBoardingSellerFragmentDirections.actionNavigationOnboardingSellerToNavigationSellerDashboard())
                                    }
                                }

                                is DataResult.Error -> {
                                    Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }

            } else {
                Toast.makeText(requireContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun getDrawableUri(context: Context, drawableId: Int): Uri {
        return Uri.parse("android.resource://${context.packageName}/$drawableId")
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