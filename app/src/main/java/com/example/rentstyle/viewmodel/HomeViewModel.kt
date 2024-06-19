package com.example.rentstyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.Product

class HomeViewModel: ViewModel() {
    var highestRatingData = MutableLiveData<List<Product>>()
    var newProductData = MutableLiveData<List<Product>>()
}