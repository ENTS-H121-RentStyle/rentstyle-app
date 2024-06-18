package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentShoppingCartBinding
import com.example.rentstyle.helpers.adapter.RecyclerShoppingCartAdapter
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.model.remote.response.CartResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShoppingCartFragment : Fragment() {
    private lateinit var _binding: FragmentShoppingCartBinding
    private val binding get() = _binding

    private lateinit var rvProductShoppingCart: RecyclerView
    private lateinit var shoppingCartAdapter: RecyclerShoppingCartAdapter

    private lateinit var bottomSheetProductAmount: TextView
    private lateinit var bottomSheetTotalCost: TextView
    private lateinit var bottomSheetDepositCost: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)

        binding.mainToolbar.tvToolbarTitle.text = "Shopping Cart"
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        rvProductShoppingCart = binding.rvShopList
        bottomSheetProductAmount = binding.tvSelectedProductAmount
        bottomSheetTotalCost = binding.tvSubtotalCost
        bottomSheetDepositCost = binding.tvDepositCost

        shoppingCartAdapter = RecyclerShoppingCartAdapter(emptyList())
        rvProductShoppingCart.adapter = shoppingCartAdapter

        val peekHeightInDp = 140
        val peekHeightInPx =
            (peekHeightInDp * requireContext().resources.displayMetrics.density).toInt()

        BottomSheetBehavior.from(binding.sheetShoppingCartDetail).apply {
            peekHeight = peekHeightInPx
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btnCheckOut.setOnClickListener {
            findNavController().navigate(ShoppingCartFragmentDirections.actionNavigationShoppingCartToNavigationCheckOut())
        }

        fetchCart()

        return binding.root
    }

    private fun fetchCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val loginSession = LoginSession.getInstance(requireContext().dataStore)
                val token = loginSession.getSessionToken().first() ?: ""
                val userId = loginSession.getUserId().first() ?: ""

                val apiService = ApiConfig.getApiService(token)
                val cartResponse = apiService.getCart(userId, "Bearer $token")

                updateUI(cartResponse)

            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error appropriately
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateUI(cartResponses: List<CartResponse>) {
        shoppingCartAdapter.updateData(cartResponses)
        val totalDuration = cartResponses.sumOf { it.duration }
        val totalCost = cartResponses.sumOf { it.product.rentPrice * it.duration }

        bottomSheetProductAmount.text = "Total for $totalDuration days"
        bottomSheetTotalCost.text = "Rp. $totalCost"
        bottomSheetDepositCost.text = "+ Rp. 58.000 (Deposit)" // Adjust as needed
    }
}
