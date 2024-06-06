package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentFavoriteBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.RecyclerDummyAdapter2

class FavoriteFragment : Fragment() {
    private lateinit var _binding: FragmentFavoriteBinding
    private val binding get() = _binding

    private lateinit var favProductRecyclerView: RecyclerView
    private lateinit var favProductAdapter: RecyclerDummyAdapter2
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        favProductRecyclerView = binding.rvFavProduct
        favProductAdapter = RecyclerDummyAdapter2()

        favProductRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 4, false))
        favProductRecyclerView.adapter = favProductAdapter

        return binding.root
    }
}