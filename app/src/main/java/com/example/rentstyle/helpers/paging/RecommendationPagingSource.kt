package com.example.rentstyle.helpers.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.repository.RecommendationRepository

class RecommendationPagingSource(
    private val repository: RecommendationRepository,
    private val userId: String
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: 1

        return try {
            val products = repository.getRecommendationProducts(userId, position)

            LoadResult.Page(
                data = products,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (products.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        // Not implemented here, handle if needed
        return null
    }
}
