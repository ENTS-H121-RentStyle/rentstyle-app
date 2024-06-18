package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentFavoriteBinding
import com.example.rentstyle.helpers.FirebaseToken
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.GridSpacingItemDecoration
import com.example.rentstyle.helpers.adapter.FavoriteProductAdapter
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.retrofit.ApiConfig
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
        favProductAdapter = FavoriteProductAdapter()

        favProductRecyclerView.addItemDecoration(GridSpacingItemDecoration(3, 4, false))
        favProductRecyclerView.adapter = favProductAdapter

        fetchFavoriteProducts()

        return binding.root
    }

    private fun fetchFavoriteProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val loginSession = LoginSession.getInstance(requireActivity().application.dataStore)
                val token = loginSession.getSessionToken().first().toString()
                val userId = loginSession.getUserId().first().toString()

                if (token.isEmpty() || userId.isEmpty()) {
                    Log.e("FavoriteFragment", "Token or UserID is empty")
                    return@launch
                }

                val apiService = ApiConfig.getApiService(token)
                val response = apiService.getFavorites(userId)

                if (response.isSuccessful) {
                    val favoriteProducts = response.body() ?: emptyList()
                    favProductAdapter.submitList(favoriteProducts.mapNotNull { it.toProduct() })
                } else {
                    Log.e("FavoriteFragment", "Failed to fetch favorites: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FavoriteFragment", "Failed to fetch favorites: ${e.message}", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
