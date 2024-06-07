package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.rentstyle.R

class InteresColorFragment : Fragment() {

    private lateinit var tvFavoriteColors: TextView
    private val selectedColors = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_interes_color, container, false)

        tvFavoriteColors = view.findViewById(R.id.tv_favorite_colors)

        setupButton(view, R.id.btn_red, getString(R.string.red))
        setupButton(view, R.id.btn_blue, getString(R.string.blue))
        setupButton(view, R.id.btn_green, getString(R.string.green))
        // Repeat for other buttons

        return view
    }

    private fun setupButton(view: View, buttonId: Int, colorName: String) {
        val button = view.findViewById<Button>(buttonId)
        button.setOnClickListener {
            val isSelected = selectedColors.contains(colorName)
            if (isSelected) {
                selectedColors.remove(colorName)
                button.background = resources.getDrawable(R.drawable.button_false, null)
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus, 0, 0, 0)
            } else {
                selectedColors.add(colorName)
                button.background = resources.getDrawable(R.drawable.button, null)
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
            }
        }
    }

}
