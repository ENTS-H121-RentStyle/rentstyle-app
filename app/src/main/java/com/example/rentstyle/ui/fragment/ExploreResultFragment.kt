package com.example.rentstyle.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentExploreResultBinding
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.ProductPagingAdapter
import com.example.rentstyle.helpers.adapter.ProductSkeletonAdapter
import com.example.rentstyle.model.Product
import com.example.rentstyle.viewmodel.ExploreViewModel
import com.example.rentstyle.viewmodel.ProductViewModelFactory
import kotlinx.coroutines.launch
import kotlin.math.abs

class ExploreResultFragment : Fragment() {
    private lateinit var _binding : FragmentExploreResultBinding
    private val binding get() = _binding

    private val args: ExploreResultFragmentArgs by navArgs()

    private val viewModel: ExploreViewModel by activityViewModels {
        ProductViewModelFactory.getInstance(this.requireActivity().application)
    }

    private lateinit var productList: RecyclerView
    private lateinit var productAdapter: ProductPagingAdapter

    private lateinit var searchBar: SearchView
    private lateinit var keyword: TextView

    private lateinit var exploreSkeleton: RecyclerView
    private lateinit var exploreSkeletonAdapter: ProductSkeletonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreResultBinding.inflate(inflater, container, false)

        binding.apply {
            productList = rvProductExploreResult
            searchBar = searchView
            keyword = tvExploreKeyword
            exploreSkeleton = rvProductExploreSkeleton
        }

        val screenWidth = getDisplayWidthInDp(requireContext())
        val spacing = abs((screenWidth - 340) / 3)

        exploreSkeletonAdapter = ProductSkeletonAdapter(8)
        exploreSkeleton.addItemDecoration(GridSpacingItemDecoration(2,spacing,true))
        exploreSkeleton.adapter = exploreSkeletonAdapter

        if (args.query.isNotEmpty()) {
            searchBar.setQuery(args.query, false)
            keyword.text = args.query
            viewModel.productFlow(args.query).observe(viewLifecycleOwner) {
                productAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        productAdapter = ProductPagingAdapter()
        productList.addItemDecoration(GridSpacingItemDecoration(2,spacing,true))
        productList.adapter = productAdapter

        productAdapter.addLoadStateListener {
            binding.shimmerViewProductExplore.isVisible = it.source.refresh is LoadState.Loading
            productList.isVisible = it.source.refresh is LoadState.NotLoading && productAdapter.itemCount > 0
        }

        productAdapter.setOnClickListener(object : ProductPagingAdapter.OnClickListener {
            override fun onClick(position: Int, model: Product) {
                findNavController().navigate(ExploreResultFragmentDirections.actionNavigationExploreResultToNavigationProductDetail(model.id))
            }
        })

        searchBar.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    productList.isVisible = false
                    keyword.text = query
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.productFlow(query).observe(viewLifecycleOwner) {
                            productAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.btnShoppingCart.setOnClickListener {
            findNavController().navigate(ExploreResultFragmentDirections.actionNavigationExploreResultToNavigationShoppingCart())
        }

        return binding.root
    }

    private fun getDisplayWidthInDp(context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        return dpWidth.toInt()
    }
}