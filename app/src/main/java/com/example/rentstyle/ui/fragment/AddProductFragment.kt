package com.example.rentstyle.ui.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentAddProductBinding
import com.example.rentstyle.helpers.CacheImageManager
import com.example.rentstyle.helpers.CacheImageManager.clearTempImages
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {
    private lateinit var _binding: FragmentAddProductBinding
    private val binding get() = _binding
    private lateinit var dialogView: Dialog

    private val args: AddProductFragmentArgs by navArgs()

    private lateinit var currentImageUri: Uri
    private var isImageFromGallery = false

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

        binding.mainToolbar.apply {
            tvToolbarTitle.text = "Add product"
            ivBackButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }

        setUpProductImage()
        setUpProductCategory()

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(AddProductFragmentDirections.actionNavigationAddProductToNavigationSellerDashboard(""))
            clearTempImages(requireContext())
            Glide.with(requireContext())
                .clear(binding.ivProductImage)
        }

        return binding.root
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
            listOf("Choose", "Adat", "Formal", "Cosplay", "Pesta")
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProductCategory.adapter = adapter
    }

    private fun showDialogBox() {
        dialogView = Dialog(requireContext())
        dialogView.setContentView(R.layout.add_product_dialog_box)
        dialogView.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogView.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.dialog_custom))
        dialogView.setCanceledOnTouchOutside(true)

        dialogView.findViewById<AppCompatButton>(R.id.btn_camera).setOnClickListener {
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

    companion object{
        const val IMAGE_RESULT = "result"
        const val UPLOAD_RESULT = "upload"
    }
}