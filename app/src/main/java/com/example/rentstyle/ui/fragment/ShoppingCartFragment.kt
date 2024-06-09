package com.example.rentstyle.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.databinding.FragmentShoppingCartBinding
import com.example.rentstyle.helpers.adapter.RecyclerCheckOutProductAdapter
import com.example.rentstyle.helpers.adapter.RecyclerShoppingCartAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior

class ShoppingCartFragment : Fragment() {
    private lateinit var _binding: FragmentShoppingCartBinding
    private val binding get() = _binding

    private lateinit var rvProductShoppingCart: RecyclerView
    private lateinit var shoppingCartAdapter: RecyclerShoppingCartAdapter

    private lateinit var btnSelectAllProduct : AppCompatButton
    private lateinit var bottomSheetProductAmount: TextView
    private lateinit var bottomSheetTotalCost: TextView
    private lateinit var bottomSheetDepositCost: TextView

    private lateinit var rvCheckoutDetail: RecyclerView
    private lateinit var checkoutDetailAdapter: RecyclerCheckOutProductAdapter
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
            btnSelectAllProduct = btnSelectAll
            rvProductShoppingCart = rvShopList
            bottomSheetProductAmount = tvSelectedProductAmount
            bottomSheetTotalCost = tvSubtotalCost
            bottomSheetDepositCost = tvDepositCost
            rvCheckoutDetail = rvProductCoDetail
        }

        shoppingCartAdapter = RecyclerShoppingCartAdapter(dataset)
        checkoutDetailAdapter = RecyclerCheckOutProductAdapter()

        rvProductShoppingCart.adapter = shoppingCartAdapter
        rvCheckoutDetail.adapter = checkoutDetailAdapter

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

        btnSelectAllProduct.text = "Select all (5)"
        bottomSheetProductAmount.text = "Subtotal of 5 products"
        bottomSheetTotalCost.text = "Rp. 638.000"
        bottomSheetDepositCost.text = "+ Rp. 58.000 (Deposit)"

        return binding.root
    }
}