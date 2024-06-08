package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rentstyle.databinding.FragmentExploreBinding
import com.example.rentstyle.helpers.adapter.RecyclerDummyShopAdapter

class ExploreFragment : Fragment() {
    private lateinit var _binding: FragmentExploreBinding
    private val binding get() = _binding

    private lateinit var shopListAdapter: RecyclerDummyShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        shopListAdapter = RecyclerDummyShopAdapter()
        binding.rvShopList.adapter = shopListAdapter

        return binding.root
    }
}