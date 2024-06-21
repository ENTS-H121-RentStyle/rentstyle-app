package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentEditUserProfileBinding
import com.example.rentstyle.helpers.CacheImageManager
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.ImageFileHelper.reduceFileImage
import com.example.rentstyle.helpers.ImageFileHelper.uriToFile
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.helpers.UserHelpers.getUserGender
import com.example.rentstyle.helpers.UserHelpers.getUserGenderValue
import com.example.rentstyle.viewmodel.UserViewModel
import com.example.rentstyle.viewmodel.UserViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar

class EditUserProfileFragment : Fragment() {
    private lateinit var _binding: FragmentEditUserProfileBinding
    private val binding get() = _binding

    private val viewModel: UserViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private lateinit var currentImageUri: Uri

    private lateinit var userName: EditText
    private lateinit var userDatePicker: AppCompatButton
    private lateinit var userAddress: EditText
    private lateinit var userPhone: EditText
    private lateinit var userGenderSpinner: Spinner

    private var userGender = ""
    private var userBirthDate = ""

    private var run = true

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val inputUri = result.data?.data as Uri

            val outputUri = CacheImageManager.saveTempImage(requireContext())

            val listOfUri = listOf(inputUri, outputUri)
            cropImage.launch(listOfUri)
        }
    }

    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            val uCropTool = UCrop.of(inputUri, outputUri)
                .withAspectRatio(1f,1f)
                .withMaxResultSize(1080, 1080)

            return uCropTool.getIntent(context)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return if (resultCode == AppCompatActivity.RESULT_OK && intent != null) {
                UCrop.getOutput(intent)!!
            } else {
                Uri.EMPTY
            }
        }
    }

    private val cropImage = registerForActivityResult(uCropContract){uri ->
        if (uri != Uri.EMPTY){
            currentImageUri = uri
            setProductImage(currentImageUri)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditUserProfileBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        currentImageUri = Uri.EMPTY

        binding.mainToolbar.apply {
            tvToolbarTitle.text = getString(R.string.title_update_profile)
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.apply {
            userName = edAddUserName
            userDatePicker = btnDate
            userAddress = edUserAddress
            userPhone = edAddUserPhone
            userGenderSpinner = spinnerUserGender
        }

        setUpGenderSpinner()
        getUserData()
        changeImageListener()
        birthDateListener()
        updateListener()

        return binding.root
    }

    private fun updateListener() {
        binding.btnUpdate.setOnClickListener {
            binding.ivLoadingSpinner.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }
            try {
                val name = userName.text.toString()
                val address = userAddress.text.toString()

                val phone = if (userPhone.text.toString().isNotEmpty()) {
                                "0${userPhone.text}"
                            } else {
                                "0"
                }

                if (currentImageUri != Uri.EMPTY && name.isNotEmpty()
                    && address.isNotEmpty() && phone != "0" && userBirthDate.isNotEmpty()
                    && (userGender != "" && userGender != getUserGender(requireContext())[0])) {

                    val bodyUserName = name.toRequestBody("text/plain".toMediaType())
                    val bodyUserBirthDate = userBirthDate.toRequestBody("text/plain".toMediaType())
                    val bodyUserAddress = address.toRequestBody("text/plain".toMediaType())
                    val bodyUserPhone = phone.toRequestBody("text/plain".toMediaType())
                    val bodyUserGender = getUserGenderValue(userGender).toRequestBody("text/plain".toMediaType())

                    val imageFile = uriToFile(currentImageUri, requireContext()).reduceFileImage()
                    val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "image",
                        imageFile.name,
                        requestImageFile
                    )

                    updateUserProfile(bodyUserName, bodyUserBirthDate, bodyUserAddress, bodyUserPhone, bodyUserGender, multipartBody, currentImageUri.toString(), name)

                } else if (currentImageUri == Uri.EMPTY) {
                    binding.ivLoadingSpinner.isVisible = false
                    Toast.makeText(requireContext(), getString(R.string.txt_change_new_image), Toast.LENGTH_SHORT).show()
                } else {
                    binding.ivLoadingSpinner.isVisible = false
                    Toast.makeText(requireContext(), getString(R.string.empty_field), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.ivLoadingSpinner.isVisible = false
                Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfile (userName: RequestBody,
                                   birthDate: RequestBody,
                                   address: RequestBody,
                                   phone: RequestBody,
                                   gender: RequestBody,
                                   file: MultipartBody.Part, image: String, newUserName: String
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateUserProfile(userName, birthDate, address,
                phone, gender, file).observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is StatusResult.Loading -> {}

                        is StatusResult.Success -> {
                            binding.ivLoadingSpinner.isVisible = false
                            Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()

                            if (run) {
                                viewModel.userData.value = arrayListOf(newUserName, image)
                                Glide.with(requireContext())
                                    .clear(binding.ivUserImage)
                                findNavController().navigateUp()
                            }
                        }

                        is StatusResult.Error -> {
                            binding.ivLoadingSpinner.isVisible = false
                            Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    binding.ivLoadingSpinner.isVisible = false
                    Toast.makeText(requireContext(), getString(R.string.txt_error_update_user), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun birthDateListener() {
        binding.btnDate.setOnClickListener {
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedMonth = String.format("%02d", selectedMonth + 1)
                    val formattedDay = String.format("%02d", selectedDay)

                    userBirthDate = "$selectedYear-$formattedMonth-$formattedDay"
                    binding.tvUserBirthDate.text = "$selectedYear-$formattedMonth-$formattedDay"
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun changeImageListener() {
        binding.ivUserImage.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
            launcherIntentGallery.launch(chooser)
        }
    }

    private fun setUpGenderSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getUserGender(requireContext())
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userGenderSpinner.adapter = adapter

        userGenderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                userGender = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), getString(R.string.txt_select_gender), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserData() {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    when (result) {
                        is DataResult.Loading -> { }

                        is DataResult.Success -> {
                            val data = result.data

                            updateUserData(data.image, data.name, data.birthDate, data.address, data.phone, data.gender)
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

    private fun updateUserData (image: String?,
                                name: String?,
                                birthDate: String?,
                                address: String?,
                                phone: String?,
                                gender: String?) {

        if (image != null) {
            setProductImage(image.toUri())
        }

        if (name != null) {
            userName.setText(name)
        }

        if (birthDate != null) {
            userBirthDate = birthDate
            binding.tvUserBirthDate.text = birthDate
        }

        if (address != null) {
            userAddress.setText(address)
        }

        if (phone != null) {
            if (phone.isNotEmpty()) {
                userPhone.setText(phone.removePrefix("0"))
            } else {
                userPhone.setText(phone)
            }
        }

        when (gender) {
            getUserGenderValue(getUserGender(requireContext())[1]) -> {
                userGender = getUserGender(requireContext())[1]
                userGenderSpinner.setSelection(1)
            }
            getUserGenderValue(getUserGender(requireContext())[2]) -> {
                userGender = getUserGender(requireContext())[2]
                userGenderSpinner.setSelection(2)
            }
            else -> {
                userGenderSpinner.setSelection(0)
            }
        }

        binding.ivLoadingSpinner.isVisible = false
    }

    private fun setProductImage(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_placeholder)
            )
            .into(binding.ivUserImage)
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