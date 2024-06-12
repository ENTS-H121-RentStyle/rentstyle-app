package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentShoppingCartBinding
import com.example.rentstyle.helpers.adapter.RecyclerShoppingCartAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

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

        val dataset = arrayListOf(1,2,3,4,5)

        binding.apply {
            rvProductShoppingCart = rvShopList
            bottomSheetProductAmount = tvSelectedProductAmount
            bottomSheetTotalCost = tvSubtotalCost
            bottomSheetDepositCost = tvDepositCost
        }

        shoppingCartAdapter = RecyclerShoppingCartAdapter(dataset)
        rvProductShoppingCart.adapter = shoppingCartAdapter

        val peekHeightInDp = 140
        val peekHeightInPx = (peekHeightInDp * requireContext().resources.displayMetrics.density).toInt()

        BottomSheetBehavior.from(binding.sheetShoppingCartDetail).apply {
            peekHeight = peekHeightInPx
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        shoppingCartAdapter.setOnClickListener(object : RecyclerShoppingCartAdapter.OnClickListener {
            override fun onClick(position: Int) {
                findNavController().navigate(ShoppingCartFragmentDirections.actionNavigationShoppingCartToNavigationProductDetail())
            }

        })

        bottomSheetProductAmount.text = "Total for 5 days"
        bottomSheetTotalCost.text = "Rp. 638.000"
        bottomSheetDepositCost.text = "+ Rp. 58.000 (Deposit)"

        return binding.root
    }
}