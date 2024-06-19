package com.example.rentstyle.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rentstyle.R
import com.example.rentstyle.databinding.FragmentShoppingCartBinding
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.FirebaseToken
import com.example.rentstyle.helpers.FirebaseToken.updateTokenId
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.helpers.adapter.RecyclerShoppingCartAdapter
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.response.CartResponse
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.viewmodel.CartViewModel
import com.example.rentstyle.viewmodel.ProductViewModelFactory
import com.example.rentstyle.viewmodel.UserViewModel
import com.example.rentstyle.viewmodel.UserViewModelFactory
import com.github.ybq.android.spinkit.style.WanderingCubes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ShoppingCartFragment : Fragment() {
    private lateinit var _binding: FragmentShoppingCartBinding
    private val binding get() = _binding

    private val viewModel: CartViewModel by activityViewModels {
        ProductViewModelFactory.getInstance(this.requireActivity().application)
    }

    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModelFactory.getInstance(this.requireActivity().application)
    }

    private lateinit var rvProductShoppingCart: RecyclerView
    private lateinit var shoppingCartAdapter: RecyclerShoppingCartAdapter

    private lateinit var bottomSheetProductAmount: TextView
    private lateinit var bottomSheetTotalCost: TextView
    private lateinit var bottomSheetDepositCost: TextView

    private var productId: String = ""
    private var productName: String = ""
    private var rentPrice: Int = 0
    private var rentDuration: Int = 0
    private var productImage: String = ""

    private var run = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)

        updateTokenId(requireContext(), viewLifecycleOwner)

        binding.mainToolbar.tvToolbarTitle.text = getString(R.string.title_shopping_cart)
        binding.mainToolbar.ivBackButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            rvProductShoppingCart = rvShopList
            bottomSheetProductAmount = tvSelectedProductAmount
            bottomSheetTotalCost = tvSubtotalCost
            bottomSheetDepositCost = tvDepositCost
        }

        updateBottomSheet(null, null)

        fetchCart()
        checkProfile()

        return binding.root
    }

    private fun checkProfile() {
        binding.btnCheckOut.setOnClickListener {
            if (bottomSheetProductAmount.text != getString(R.string.txt_total_days, "0")) {
                binding.ivLoadingSpinner.apply {
                    isVisible = true
                    setIndeterminateDrawable(WanderingCubes())
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    userViewModel.getUserProfile().observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is DataResult.Loading -> { }

                                is DataResult.Success -> {
                                    val data = result.data

                                    if (run) {
                                        run = false
                                        if (data.address != null && data.phone != null && rentDuration != 0 && productId != "" && rentPrice != 0 && productName != "" && productImage != "") {
                                            findNavController().navigate(ShoppingCartFragmentDirections.actionNavigationShoppingCartToNavigationCheckOut(rentDuration = rentDuration,
                                                productId = productId, rentPrice = rentPrice, productName = productName, productImage = productImage))
                                        } else {
                                            Toast.makeText(requireContext(), "Please complete your profile", Toast.LENGTH_SHORT).show()
                                            findNavController().navigate(ShoppingCartFragmentDirections.actionNavigationShoppingCartToNavigationEditUserProfile())
                                        }
                                    }
                                }

                                is DataResult.Error -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Toast.makeText(requireContext(), getString(R.string.error_toast, result.error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please select a product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.ivLoadingSpinner.apply {
                isVisible = true
                setIndeterminateDrawable(WanderingCubes())
            }
            try {
                val loginSession = LoginSession.getInstance(requireActivity().application.dataStore)
                val token = loginSession.getSessionToken().first().toString()
                val userId = loginSession.getUserId().first().toString()

                val apiService = ApiConfig.getApiService(token)
                val cartResponse = apiService.getCart(userId, "Bearer $token")

                updateUI(cartResponse)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(cartResponses: List<CartResponse>) {
        binding.ivLoadingSpinner.isVisible = false
        shoppingCartAdapter = RecyclerShoppingCartAdapter(cartResponses.toMutableList())
        rvProductShoppingCart.adapter = shoppingCartAdapter

        if (cartResponses.isEmpty()) {
            binding.tvEmptyCart.isVisible = true
        }

        shoppingCartAdapter.setOnClickListener(object : RecyclerShoppingCartAdapter.OnClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(position: Int, id: String) {
                binding.ivLoadingSpinner.apply {
                    isVisible = true
                    setIndeterminateDrawable(WanderingCubes())
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteItemInCart(id).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is StatusResult.Loading -> {}

                                is StatusResult.Success -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    shoppingCartAdapter.deleteItem(position, id)
                                    if (shoppingCartAdapter.itemCount == 0) {
                                        binding.tvEmptyCart.isVisible = true
                                    }
                                    Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()
                                }

                                is StatusResult.Error -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Log.d("error", result.error)
                                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error delete item from cart", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onAddClick(position: Int, id: String, duration: Int, model: CartResponse, isChecked: Boolean
            ) {
                binding.ivLoadingSpinner.apply {
                    isVisible = true
                    setIndeterminateDrawable(WanderingCubes())
                }

                viewLifecycleOwner.lifecycleScope.launch {

                    val bodyDuration = (duration + 1).toString().toRequestBody("text/plain".toMediaType())

                    viewModel.updateItemInCart(id, bodyDuration).observe(viewLifecycleOwner) { result ->
                        if (result != null) {
                            when (result) {
                                is StatusResult.Loading -> {}

                                is StatusResult.Success -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    shoppingCartAdapter.updateItemDuration(position, id, duration + 1)
                                    if (isChecked) {
                                        updateBottomSheet(model, duration+1 )
                                    }
                                }

                                is StatusResult.Error -> {
                                    binding.ivLoadingSpinner.isVisible = false
                                    Log.d("error", result.error)
                                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Error delete item from cart", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onReduceClick(
                position: Int,
                id: String,
                duration: Int,
                model: CartResponse,
                isChecked: Boolean
            ) {
                binding.ivLoadingSpinner.apply {
                    isVisible = true
                    setIndeterminateDrawable(WanderingCubes())
                }

                if (duration > 1) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val bodyDuration = (duration - 1).toString().toRequestBody("text/plain".toMediaType())

                        viewModel.updateItemInCart(id, bodyDuration).observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is StatusResult.Loading -> {}

                                    is StatusResult.Success -> {
                                        binding.ivLoadingSpinner.isVisible = false
                                        shoppingCartAdapter.updateItemDuration(position, id, duration-1)
                                        if (isChecked) {
                                            updateBottomSheet(model, duration-1 )
                                        }
                                    }

                                    is StatusResult.Error -> {
                                        binding.ivLoadingSpinner.isVisible = false
                                        Log.d("error", result.error)
                                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error delete item from cart", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.deleteItemInCart(id).observe(viewLifecycleOwner) { result ->
                            if (result != null) {
                                when (result) {
                                    is StatusResult.Loading -> {}

                                    is StatusResult.Success -> {
                                        binding.ivLoadingSpinner.isVisible = false
                                        shoppingCartAdapter.deleteItem(position, id)
                                        if (shoppingCartAdapter.itemCount == 0) {
                                            binding.tvEmptyCart.isVisible = true
                                        }
                                        Toast.makeText(requireContext(), result.success, Toast.LENGTH_SHORT).show()
                                    }

                                    is StatusResult.Error -> {
                                        binding.ivLoadingSpinner.isVisible = false
                                        Log.d("error", result.error)
                                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error delete item from cart", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onItemSelected(position: Int, id: String, model: CartResponse) {
                updateBottomSheet(model, null)
            }

            override fun onItemUnSelected() {
                updateBottomSheet(null, null)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateBottomSheet(model: CartResponse?, newDuration: Int?) {
        if (model != null) {
            if (newDuration != null) {
                bottomSheetProductAmount.text = getString(R.string.txt_total_days, newDuration.toString())
                bottomSheetTotalCost.text = "Rp. ${model.product.rentPrice * newDuration} "
                bottomSheetDepositCost.text = "+ Rp. ${model.product.rentPrice * newDuration * 10 / 100} (Deposit)"

//                productId = model.productId
                productName = model.product.productName
                rentPrice = model.product.rentPrice
                rentDuration = newDuration
                productImage = model.product.image
            } else {
                bottomSheetProductAmount.text = getString(R.string.txt_total_days, model.duration.toString())
                bottomSheetTotalCost.text = "Rp. ${model.product.rentPrice * model.duration} "
                bottomSheetDepositCost.text = "+ Rp. ${model.product.rentPrice * model.duration * 10 / 100} (Deposit)"

                productId = "00003a3e-4aaa-4e3e-9e32-5ed1c4ee5f81"
                productName = model.product.productName
                rentPrice = model.product.rentPrice
                rentDuration = model.duration
                productImage = model.product.image
            }
        } else {
            bottomSheetProductAmount.text = getString(R.string.txt_total_days, "0")
            bottomSheetTotalCost.text = "Rp. 0"
            bottomSheetDepositCost.text = "+ Rp. 0 (Deposit)"

            productId = ""
            productName = ""
            rentPrice = 0
            rentDuration = 0
            productImage = ""
        }
    }
}
