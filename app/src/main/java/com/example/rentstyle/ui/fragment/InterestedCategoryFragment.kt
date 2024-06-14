package com.example.rentstyle.ui.fragment

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentInterestedCategoryBinding

class InterestedCategoryFragment : Fragment() {
    private lateinit var _binding: FragmentInterestedCategoryBinding
    private val binding get() = _binding

    private lateinit var frontAnimator: AnimatorSet
    private lateinit var backAnimator: AnimatorSet

    private lateinit var cardTraditionalFront: CardView
    private lateinit var cardTraditionalBack: CardView
    private var isTraditionalFront = true

    private lateinit var cardPartyFront: CardView
    private lateinit var cardPartyBack: CardView
    private var isPartyFront = true

    private lateinit var cardFormalFront: CardView
    private lateinit var cardFormalBack: CardView
    private var isFormalFront = true

    private lateinit var cardCharCosplayFront: CardView
    private lateinit var cardCharCosplayBack: CardView
    private var isCharCosplayFront = true

    private lateinit var nextButton: AppCompatButton
    private val userPreference: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInterestedCategoryBinding.inflate(inflater, container, false)

        binding.apply {
            cardTraditionalFront = cvTraditionalFront
            cardPartyFront = cvPartyFront
            cardFormalFront = cvFormalFront
            cardCharCosplayFront = cvCharCosplayFront

            cardTraditionalBack = cvTraditionalBack
            cardPartyBack = cvPartyBack
            cardFormalBack = cvFormalBack
            cardCharCosplayBack = cvCharCosplayBack

            nextButton = btnNext
        }

        setCardScale(cardTraditionalFront, cardTraditionalBack)
        setCardScale(cardPartyFront, cardPartyBack)
        setCardScale(cardFormalFront, cardFormalBack)
        setCardScale(cardCharCosplayFront, cardCharCosplayBack)

        frontAnimator = AnimatorInflater.loadAnimator(context, R.animator.front_card) as AnimatorSet
        backAnimator = AnimatorInflater.loadAnimator(context, R.animator.back_card) as AnimatorSet

        cardListener()

        nextButton.setOnClickListener {
            if (userPreference.size in 1..3) {
                findNavController().navigate(InterestedCategoryFragmentDirections.actionNavigationInterestedCategoryToNavigationInterestedColor(userPreference.toTypedArray()))
            } else {
                Toast.makeText(requireContext(), getString(R.string.txt_choose_one_category), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun cardListener() {
        cardTraditionalFront.setOnClickListener {
            isTraditionalFront = if (isTraditionalFront) {
                if (userPreference.size <= 2) {
                    flipFrontCard(cardTraditionalFront, cardTraditionalBack)
                    userPreference.add("Adat")
                    false
                } else {
                    Toast.makeText(requireContext(), getString(R.string.txt_maximum_three_category), Toast.LENGTH_SHORT).show()
                    true
                }
            } else {
                flipBackCard(cardTraditionalFront, cardTraditionalBack)
                userPreference.remove("Adat")
                true
            }
        }

        cardPartyFront.setOnClickListener {
            isPartyFront = if (isPartyFront) {
                if (userPreference.size <= 2) {
                    flipFrontCard(cardPartyFront, cardPartyBack)
                    userPreference.add("Pesta")
                    false
                } else {
                    Toast.makeText(requireContext(), getString(R.string.txt_maximum_three_category), Toast.LENGTH_SHORT).show()
                    true
                }
            } else {
                flipBackCard(cardPartyFront, cardPartyBack)
                userPreference.remove("Pesta")
                true
            }
        }

        cardFormalFront.setOnClickListener {
            isFormalFront = if (isFormalFront) {
                if (userPreference.size <= 2) {
                    flipFrontCard(cardFormalFront, cardFormalBack)
                    userPreference.add("Formal")
                    false
                } else {
                    Toast.makeText(requireContext(), getString(R.string.txt_maximum_three_category), Toast.LENGTH_SHORT).show()
                    true
                }
            } else {
                flipBackCard(cardFormalFront, cardFormalBack)
                userPreference.remove("Formal")
                true
            }
        }

        cardCharCosplayFront.setOnClickListener {
            isCharCosplayFront = if (isCharCosplayFront) {
                if (userPreference.size <= 2) {
                    flipFrontCard(cardCharCosplayFront, cardCharCosplayBack)
                    userPreference.add("Cosplay")
                    false
                } else {
                    Toast.makeText(requireContext(), getString(R.string.txt_maximum_three_category), Toast.LENGTH_SHORT).show()
                    true
                }
            } else {
                flipBackCard(cardCharCosplayFront, cardCharCosplayBack)
                userPreference.remove("Cosplay")
                true
            }
        }
    }

    private fun flipFrontCard (frontCard : View, backCard: View) {
        frontAnimator.setTarget(frontCard)
        backAnimator.setTarget(backCard)
        frontAnimator.start()
        backAnimator.start()
    }

    private fun flipBackCard (frontCard : View, backCard: View) {
        frontAnimator.setTarget(backCard)
        backAnimator.setTarget(frontCard)
        backAnimator.start()
        frontAnimator.start()
    }

    private fun setCardScale(front: View, back: View) {
        val context = requireActivity().application
        val scale = context.resources.displayMetrics.density

        front.cameraDistance = 8000 * scale
        back.cameraDistance = 8000 * scale
    }
}
