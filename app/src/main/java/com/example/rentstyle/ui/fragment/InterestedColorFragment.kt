package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentInterestedColorBinding
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.response.Pref
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.ui.VerificationActivity
import com.example.rentstyle.ui.customview.CustomButton
import com.github.ybq.android.spinkit.style.WanderingCubes
import com.google.android.material.slider.RangeSlider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class InterestedColorFragment : Fragment() {
    private lateinit var _binding: FragmentInterestedColorBinding
    private val binding get() = _binding

    private lateinit var pref: LoginSession

    private val args: InterestedColorFragmentArgs by navArgs()

    private lateinit var sizeSlider: RangeSlider
    private lateinit var sizeIndicator: TextView

    private lateinit var redButton: CustomButton
    private lateinit var orangeButton: CustomButton
    private lateinit var yellowButton: CustomButton
    private lateinit var greyButton: CustomButton
    private lateinit var blackButton: CustomButton
    private lateinit var greenButton: CustomButton
    private lateinit var whiteButton: CustomButton
    private lateinit var blueButton: CustomButton
    private lateinit var purpleButton: CustomButton
    private lateinit var brownButton: CustomButton
    private lateinit var pinkButton: CustomButton
    private lateinit var creamButton: CustomButton

    private var prefSize = "0.0"
    private val userColorPreference: MutableList<String> = mutableListOf()
    private lateinit var submitButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterestedColorBinding.inflate(inflater, container, false)

        binding.apply {
            redButton = btnRed
            orangeButton = btnOrange
            yellowButton = btnYellow
            greyButton = btnGrey
            blackButton = btnBlack
            greenButton = btnGreen
            whiteButton = btnWhite
            blueButton = btnBlue
            purpleButton = btnPurple
            brownButton = btnBrown
            pinkButton = btnPink
            creamButton = btnCream

            sizeIndicator = tvHeading2
            sizeSlider = rangeSliderSize

            submitButton = btnNext
        }

        setUpSlider()
        colorButtonOnClickListener()

        pref = LoginSession.getInstance(requireActivity().application.dataStore)

        submitButton.setOnClickListener {

            if (userColorPreference.size in 1..3) {
                binding.ivLoadingSpinner.apply {
                    isVisible = true
                    setIndeterminateDrawable(WanderingCubes())
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    val userId = pref.getUserId().first()
                    val sessionToken = pref.getSessionToken().first()

                    try {
                        val apiService = ApiConfig.getApiService(sessionToken!!)
                        val response = apiService.uploadUserPreference(Pref(userId!!, args.category.toList(), userColorPreference, getSize(prefSize)))

                        if (response.code() == 201 || response.code() == 500) {
                            pref.setPrefCheck()
                            (activity as VerificationActivity).navigateToMainActivity()
                        } else {
                            Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "$e", Toast.LENGTH_SHORT).show()
                    }
                    binding.ivLoadingSpinner.isVisible = false
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.txt_choose_one_color), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun colorButtonOnClickListener() {
        redButton.setOnClickListener {
            checkButtonColor(redButton, "Merah")
        }

        orangeButton.setOnClickListener {
            checkButtonColor(orangeButton, "Oranye")
        }

        yellowButton.setOnClickListener {
            checkButtonColor(yellowButton, "Kuning")
        }

        greyButton.setOnClickListener {
            checkButtonColor(greyButton, "Abu-abu")
        }

        blackButton.setOnClickListener {
            checkButtonColor(blackButton, "Hitam")
        }

        greenButton.setOnClickListener {
            checkButtonColor(greenButton, "Hijau")
        }

        whiteButton.setOnClickListener {
            checkButtonColor(whiteButton, "Putih")
        }

        blueButton.setOnClickListener {
            checkButtonColor(blueButton, "Biru")
        }

        purpleButton.setOnClickListener {
            checkButtonColor(purpleButton, "Ungu")
        }

        brownButton.setOnClickListener {
            checkButtonColor(brownButton, "Coklat")
        }

        pinkButton.setOnClickListener {
            checkButtonColor(pinkButton, "Merah muda")
        }

        creamButton.setOnClickListener {
            checkButtonColor(creamButton, "Krem")
        }

    }

    private fun setUpSlider() {
        sizeIndicator.text = getString(R.string.txt_cloth_size, getSize(prefSize))

        sizeSlider.addOnChangeListener { _, value, _ ->
            prefSize = value.toString()
            sizeIndicator.text = getString(R.string.txt_cloth_size, getSize(prefSize))
        }

        sizeSlider.setLabelFormatter { value ->
            when (value.toInt()) {
                0 -> "S"
                1 -> "M"
                2 -> "L"
                3 -> "XL"
                else -> value.toString()
            }
        }
    }

    private fun checkButtonColor (button: CustomButton, color: String) {
        if (userColorPreference.size <= 2) {
            if (button.isActivate) {
                userColorPreference.add(color)
            } else {
                userColorPreference.remove(color)
            }
        } else {
            button.resetButton()
            userColorPreference.remove(color)
            Toast.makeText(requireContext(), getString(R.string.txt_maximum_three_color), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSize (value: String): String {
        return when (value) {
            "0.0" -> "S"
            "1.0" -> "M"
            "2.0" -> "L"
            else -> "XL"
        }
    }
}
