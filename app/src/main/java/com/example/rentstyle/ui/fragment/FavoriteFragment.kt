package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentFavoriteBinding
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.FavoriteProductAdapter
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginSession: LoginSession
    private lateinit var favProductRecyclerView: RecyclerView
    private lateinit var favProductAdapter: FavoriteProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        loginSession = LoginSession.getInstance(requireContext().dataStore)

        favProductRecyclerView = binding.rvFavProduct
        favProductRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 5, false))

        fetchFavoriteProducts()

        return binding.root
    }

    private fun fetchFavoriteProducts() {
        binding.ivLoadingSpinner.apply {
            isVisible = true
            setIndeterminateDrawable(WanderingCubes())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val loginSession = LoginSession.getInstance(requireActivity().application.dataStore)
                val token = loginSession.getSessionToken().first().toString()
                val userId = loginSession.getUserId().first().toString()

                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getFavorites(userId)

                if (response.code() == 200) {
                    favProductAdapter = FavoriteProductAdapter(response.body()!!)
                    favProductRecyclerView.adapter = favProductAdapter
                } else if (response.code() == 404) {
                    binding.tvNoFavorite.isVisible = true
                }
                else {
                    Toast.makeText(requireContext(), "Fail to get favorite data", Toast.LENGTH_SHORT).show()
                }
                binding.ivLoadingSpinner.isVisible = false
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Fail to get favorite data", Toast.LENGTH_SHORT).show()
            }
            binding.ivLoadingSpinner.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
