package com.example.rentstyle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rentstyle.model.repository.ProductRepository
import androidx.paging.liveData
import com.example.rentstyle.helpers.paging.ExplorePagingSource
import com.example.rentstyle.model.Product

class ExploreViewModel (private val productRepository: ProductRepository): ViewModel() {

    private fun getProductByQuery(q: String): LiveData<PagingData<Product>> {
        return Pager(
            PagingConfig(pageSize = 1)
        ) {
            ExplorePagingSource(productRepository, q)
        }.liveData
    }

    fun productFlow(q: String) = getProductByQuery(q).cachedIn(viewModelScope)
}