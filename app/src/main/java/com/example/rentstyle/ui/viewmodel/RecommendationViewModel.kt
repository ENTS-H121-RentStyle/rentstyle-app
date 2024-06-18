package com.example.rentstyle.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rentstyle.helpers.paging.RecommendationPagingSource
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow

class RecommendationViewModel(private val repository: RecommendationRepository) : ViewModel() {

    fun getRecommendationProducts(userId: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { RecommendationPagingSource(repository, userId) }
        ).flow
    }
}
