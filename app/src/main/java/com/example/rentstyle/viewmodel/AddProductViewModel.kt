package com.example.rentstyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddProductViewModel (private val productRepository: ProductRepository): ViewModel() {
    var productNameLiveData = MutableLiveData<String>()
    var sellerIdLiveData = MutableLiveData<String>()
    var categoryLiveData = MutableLiveData<String>()
    var sizeLiveData = MutableLiveData<String>()
    var colorLiveData = MutableLiveData<String>()
    var descLiveData = MutableLiveData<String>()
    var rentPriceLiveData = MutableLiveData<String>()
    var productPriceLiveData = MutableLiveData<String>()

    suspend fun addNewProduct (productName: RequestBody,
                               sellerId: RequestBody,
                               category: RequestBody,
                               size: RequestBody,
                               image: MultipartBody.Part,
                               color: RequestBody,
                               desc: RequestBody,
                               rentPrice: RequestBody,
                               productPrice: RequestBody) = productRepository
                                   .uploadProduct(productName, sellerId, category,
                                       size, image, color, desc, rentPrice, productPrice)

}