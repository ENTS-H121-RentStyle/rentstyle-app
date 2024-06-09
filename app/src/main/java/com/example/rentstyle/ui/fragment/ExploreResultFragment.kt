package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentExploreResultBinding
import com.example.rentstyle.helpers.FilterModel
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.RecyclerDummyAdapter
import com.example.rentstyle.helpers.adapter.RecyclerFilterAdapter

class ExploreResultFragment : Fragment() {
    private lateinit var _binding : FragmentExploreResultBinding
    private val binding get() = _binding

    private lateinit var filterList: RecyclerView
    private lateinit var filterAdapter: RecyclerFilterAdapter
    private lateinit var productList: RecyclerView
    private lateinit var productAdapter: RecyclerDummyAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreResultBinding.inflate(inflater, container, false)

        binding.apply {
            filterList = rvFilterExploreResult
            productList = rvProductExploreResult
        }

        filterAdapter = RecyclerFilterAdapter(FilterModel.getExploreFilter())
        filterList.adapter = filterAdapter

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterAdapter.resetFilterAppearance()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        productAdapter = RecyclerDummyAdapter()
        productList.addItemDecoration(GridSpacingItemDecoration(2,25,true))
        productList.adapter = productAdapter

        productAdapter.setOnClickListener(object : RecyclerDummyAdapter.OnClickListener {
            override fun onClick(position: Int, image: ImageView) {
                val extras = FragmentNavigatorExtras(image to "shared_product_image")
                findNavController().navigate(ExploreResultFragmentDirections.actionNavigationExploreResultToNavigationProductDetail(), navigatorExtras = extras)
            }
        })

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ExploreResultFragmentDirections.actionNavigationExploreResultToNavigationShoppingCart())
        }

        return binding.root
    }
}