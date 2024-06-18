package com.example.rentstyle.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.rentstyle.helpers.paging.RecommendationPagingSource
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow

class RecommendationViewModel(private val repository: RecommendationRepository) : ViewModel() {

    private fun getRecommendationProducts(userId: String): LiveData<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { RecommendationPagingSource(repository, userId) }
        ).liveData
    }

    fun recommendationFlow(userId: String) = getRecommendationProducts(userId).cachedIn(viewModelScope)
}
