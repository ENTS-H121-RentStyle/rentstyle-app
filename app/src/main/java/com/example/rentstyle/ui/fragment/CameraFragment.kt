package com.example.rentstyle.ui.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentCameraBinding
import com.example.rentstyle.helpers.CacheImageManager.saveTempImage
import com.example.rentstyle.helpers.CameraHelpers.bitmapToUri
import com.example.rentstyle.helpers.CameraHelpers.rotateBitmap
import com.yalantis.ucrop.UCrop
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var _binding: FragmentCameraBinding
    private val binding get() = _binding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private var inputUriToDelete: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                startCamera()
            } else {
                showToast(getString(R.string.permission_denied))
                findNavController().navigateUp()
            }
        }

    private val uCropContract = object: ActivityResultContract<List<Uri>, Uri>(){
        override fun createIntent(context: Context, input: List<Uri>): Intent {
            val inputUri = input[0]
            val outputUri = input[1]

            inputUriToDelete = inputUri

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

    private val cropImage = registerForActivityResult(uCropContract){ uri ->
        if (uri != Uri.EMPTY){
            try {
                requireContext().contentResolver.delete(inputUriToDelete!!, null, null)
                findNavController().navigate(CameraFragmentDirections.actionNavigationCameraToNavigationAddProduct(uri.toString()))
            } catch (e: IOException) {
                showToast(resources.getString(R.string.error_toast, e.message))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        binding.btnCapture.setOnClickListener {
            takePhoto()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

        return binding.root
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    showToast(resources.getString(R.string.error_toast, exc.message))
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.capacity())
                    buffer.get(bytes)

                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val rotatedBitmap = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        rotateBitmap(bitmap, 90)
                    } else {
                        bitmap
                    }

                    val inputUri = bitmapToUri(rotatedBitmap, requireContext())
                    val outputUri = saveTempImage(requireContext())

                    val listOfUri = listOf(inputUri, outputUri)
                    cropImage.launch(listOfUri)

                    image.close()
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY).build()

            val preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .also {
                    it.surfaceProvider = binding.previewView.surfaceProvider
                }

            imageCapture = ImageCapture.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                showToast(resources.getString(R.string.error_toast, exc.message))
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()

        cameraExecutor.shutdown()
    }
}