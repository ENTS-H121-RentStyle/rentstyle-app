package com.example.rentstyle.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.rentstyle.R

class InterestedCategoryFragment : Fragment() {

    private lateinit var cardAnime: CardView
    private lateinit var radioGroupAnime: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interested_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardAnime = view.findViewById(R.id.card_anime)
        radioGroupAnime = view.findViewById(R.id.radio_group_anime)

        // Mengatur klik pada CardView
        cardAnime.setOnClickListener {
            toggleCardSelection(cardAnime, radioGroupAnime)
        }
    }

    private fun toggleCardSelection(cardView: CardView, radioGroup: RadioGroup) {
        val isSelected = cardView.tag as? Boolean ?: false

        // Mengubah status pemilihan
        if (!isSelected) {
            cardView.setCardBackgroundColor(Color.LTGRAY) // Warna abu-abu ketika dipilih
            cardView.tag = true
        } else {
            cardView.setCardBackgroundColor(Color.WHITE) // Kembali ke warna asli ketika tidak dipilih
            cardView.tag = false
        }

        // Mengirim data kategori dan ukuran yang dipilih
        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val selectedSize = if (selectedRadioButtonId != -1) {
            view?.findViewById<RadioButton>(selectedRadioButtonId)?.text.toString()
        } else {
            ""
        }

        // Logika untuk menangani data pilihan (contoh)
        if (isSelected) {
            println("Kategori Anime dengan ukuran: $selectedSize dipilih")
        } else {
            println("Kategori Anime tidak dipilih")
        }
    }
}
