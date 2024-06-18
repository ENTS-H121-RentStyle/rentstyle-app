package com.example.rentstyle.ui.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentAddProductBinding
import com.example.rentstyle.helpers.CacheImageManager
import com.example.rentstyle.helpers.CacheImageManager.clearTempImages
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.ImageFileHelper.reduceFileImage
import com.example.rentstyle.helpers.ImageFileHelper.uriToFile
import com.example.rentstyle.helpers.ProductHelpers.getCategoryValue
import com.example.rentstyle.helpers.ProductHelpers.getProductCategory
import com.example.rentstyle.helpers.ProductHelpers.getProductSize
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.viewmodel.AddProductViewModel
import com.example.rentstyle.viewmodel.ProductViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddProductFragment : Fragment() {
    private lateinit var _binding: FragmentAddProductBinding
    private val binding get() = _binding
    private lateinit var dialogView: Dialog

    private val viewModel: AddProductViewModel by activityViewModels {
        ProductViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val args: AddProductFragmentArgs by navArgs()

    private lateinit var currentImageUri: Uri
    private var isImageFromGallery = false

    private lateinit var inputProductsName: EditText
    private lateinit var inputProductsCategory: Spinner
    private lateinit var inputProductsSize: Spinner
    private lateinit var inputProductsColor: EditText
    private lateinit var inputProductsDesc: EditText
    private lateinit var inputProductsRentPrice: EditText
    private lateinit var inputProductsPrice: EditText
    private lateinit var uploadButton: AppCompatButton

    private var productCategory = ""
    private var productSize = ""

    private var run = true

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val inputUri = result.data?.data as Uri

            val outputUri = CacheImageManager.saveTempImage(requireContext())

            val listOfUri = listOf(inputUri, outputUri)
            cropImage.launch(listOfUri)
            dialogView.hide()
        }
    }

    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            dialogView.hide()
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
            isImageFromGallery = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        currentImageUri = Uri.EMPTY

        binding.mainToolbar.apply {
            tvToolbarTitle.text = getString(R.string.title_add_product)
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.apply {
            inputProductsName = edAddProductName
            inputProductsCategory = spinnerProductCategory
            inputProductsSize = spinnerProductSize
            inputProductsColor = edAddProductColor
            inputProductsDesc = edAddDescription
            inputProductsRentPrice = edAddProductRentPrice
            inputProductsPrice = edAddProductPrice
            uploadButton = btnAdd
        }

        checkLatestProductData()
        setUpProductImage()
        setUpProductCategory()
        setUpProductSize()
        prepareUploadBody()

        return binding.root
    }

    private fun checkLatestProductData() {
        if (args.id != null) {
            viewModel.sellerIdLiveData.value = args.id
        }

        viewModel.productNameLiveData.observe(requireActivity()) {
            inputProductsName.setText(it)
        }

        viewModel.categoryLiveData.observe(requireActivity()) {
            when (it) {
                getProductCategory(requireContext())[0] -> inputProductsCategory.setSelection(0)
                getProductCategory(requireContext())[1] -> inputProductsCategory.setSelection(1)
                getProductCategory(requireContext())[2] -> inputProductsCategory.setSelection(2)
                getProductCategory(requireContext())[3] -> inputProductsCategory.setSelection(3)
                getProductCategory(requireContext())[4] -> inputProductsCategory.setSelection(4)
            }
        }

        viewModel.sizeLiveData.observe(requireActivity()) {
            when (it) {
                getProductSize()[0] -> inputProductsSize.setSelection(0)
                getProductSize()[1] -> inputProductsSize.setSelection(1)
                getProductSize()[2] -> inputProductsSize.setSelection(2)
                getProductSize()[3] -> inputProductsSize.setSelection(3)
                getProductSize()[4] -> inputProductsSize.setSelection(4)
            }
        }

        viewModel.colorLiveData.observe(requireActivity()) {
            inputProductsColor.setText(it)
        }

        viewModel.descLiveData.observe(requireActivity()) {
            inputProductsDesc.setText(it)
        }

        viewModel.rentPriceLiveData.observe(requireActivity()) {
            inputProductsRentPrice.setText(it.toString())
        }

        viewModel.productPriceLiveData.observe(requireActivity()) {
            inputProductsPrice.setText(it.toString())
        }
    }

    private fun setUpProductSize() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getProductSize()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputProductsSize.adapter = adapter
    }

    private fun prepareUploadBody() {
        inputProductsCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()

                productCategory = getCategoryValue(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Please select product category", Toast.LENGTH_SHORT).show()
            }

        }

        inputProductsSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                productSize = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Please select product category", Toast.LENGTH_SHORT).show()
            }

        }

        uploadButton.setOnClickListener {
            binding.ivLoadingSpinner.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }

            val productName = inputProductsName.text.toString()
            val productColor = inputProductsColor.text.toString()
            val productDesc = inputProductsDesc.text.toString()
            val productRentPrice = inputProductsRentPrice.text.toString()
            val productPrice = inputProductsPrice.text.toString()

            if (productName.isNotEmpty() && productColor.isNotEmpty() && currentImageUri != Uri.EMPTY
                && productDesc.isNotEmpty() && productRentPrice.isNotEmpty()
                && productPrice.isNotEmpty() && productCategory != "" && productCategory != getProductCategory(requireContext())[0]
                && productSize != "" && productSize != getProductSize()[0]
                && viewModel.sellerIdLiveData.value?.isNotEmpty() == true ) {

                try {
                    val bodyProductName = productName.toRequestBody("text/plain".toMediaType())
                    val bodySellerId = viewModel.sellerIdLiveData.value!!.toRequestBody("text/plain".toMediaType())
                    val bodyProductCategory = productCategory.toRequestBody("text/plain".toMediaType())
                    val bodyProductSize = productSize.toRequestBody("text/plain".toMediaType())

                    val imageFile = uriToFile(currentImageUri, requireContext()).reduceFileImage()
                    val requestImageFile = imageFile.asRequestBody("image/jpg".toMediaType())
                    val multipartBody = MultipartBody.Part.createFormData(
                        "image",
                        imageFile.name,
                        requestImageFile
                    )

                    val bodyProductColor = productColor.toRequestBody("text/plain".toMediaType())
                    val bodyProductDesc = productDesc.toRequestBody("text/plain".toMediaType())
                    val bodyProductRentPrice = productRentPrice.toRequestBody("text/plain".toMediaType())
                    val bodyProductPrice = productPrice.toRequestBody("text/plain".toMediaType())

                    uploadNewProduct(bodyProductName, bodySellerId, bodyProductCategory,
                        bodyProductSize, multipartBody, bodyProductColor, bodyProductDesc,
                        bodyProductRentPrice, bodyProductPrice)

                } catch (e: Exception) {
                    binding.ivLoadingSpinner.isVisible = false
                    Toast.makeText(requireContext(), "Invalid product size / category", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.ivLoadingSpinner.isVisible = false
                Toast.makeText(requireContext(), "All fields must be filled!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadNewProduct (productName: RequestBody,
                                  sellerId: RequestBody,
                                  category: RequestBody,
                                  size: RequestBody,
                                  image: MultipartBody.Part,
                                  color: RequestBody,
                                  desc: RequestBody,
                                  rentPrice: RequestBody,
                                  productPrice: RequestBody) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addNewProduct(productName, sellerId, category,
                size, image, color, desc, rentPrice, productPrice).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is StatusResult.Loading -> {}

                            is StatusResult.Success -> {
                                binding.ivLoadingSpinner.isVisible = false
                                Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()

                                if (run) {
                                    clearTempImages(requireContext())
                                    Glide.with(requireContext())
                                        .clear(binding.ivProductImage)
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
                        Toast.makeText(requireContext(), "Error upload new product", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun setUpProductImage() {
        binding.ivProductImage.setOnClickListener {
            showDialogBox()
        }
    }

    private fun setUpProductCategory() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            getProductCategory(requireContext())
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputProductsCategory.adapter = adapter
    }

    private fun showDialogBox() {
        dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.add_product_dialog_box)
        dialogView.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_custom))
        dialogView.setCanceledOnTouchOutside(true)

        dialogView.findViewById<AppCompatButton>(R.id.btn_camera).setOnClickListener {
            saveProductData()
            dialogView.hide()
            findNavController().navigate(AddProductFragmentDirections.actionNavigationAddProductToNavigationCamera())
        }

        dialogView.findViewById<AppCompatButton>(R.id.btn_gallery).setOnClickListener {
            dialogView.hide()
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
            launcherIntentGallery.launch(chooser)
        }

        dialogView.show()
    }

    private fun saveProductData () {
        viewModel.apply {
            productNameLiveData.value = inputProductsName.text.toString()

            categoryLiveData.value = productCategory

            sizeLiveData.value = productSize
            colorLiveData.value = inputProductsColor.text.toString()
            descLiveData.value = inputProductsDesc.text.toString()
            rentPriceLiveData.value = inputProductsRentPrice.text.toString()
            productPriceLiveData.value = inputProductsPrice.text.toString()
        }
    }

    private fun setProductImage(uri: Uri) {
        Glide.with(requireContext())
            .load(uri)
            .into(binding.ivProductImage)
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        if (args.uri != null && !isImageFromGallery) {
            setProductImage(args.uri!!.toUri())
            currentImageUri = args.uri!!.toUri()
        }
    }

    override fun onPause() {
        super.onPause()

        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }
}