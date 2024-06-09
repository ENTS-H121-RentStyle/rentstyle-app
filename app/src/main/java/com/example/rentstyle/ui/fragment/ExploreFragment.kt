package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        shopListAdapter.setOnClickListener(object : RecyclerDummyShopAdapter.OnClickListener {
            override fun onClick(position: Int) {
                findNavController().navigate(ExploreFragmentDirections.actionNavigationExploreToNavigationShopDetail())
            }
        })

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ExploreFragmentDirections.actionNavigationExploreToNavigationShoppingCart())
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                findNavController().navigate(ExploreFragmentDirections.actionNavigationExploreToNavigationExploreResult())
                binding.searchView.setQuery(null, false)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return binding.root
    }
}