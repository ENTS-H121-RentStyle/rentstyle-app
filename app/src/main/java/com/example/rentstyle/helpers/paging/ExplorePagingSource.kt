package com.example.rentstyle.helpers.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.repository.ProductRepository

class ExplorePagingSource (
    private val repository: ProductRepository,
    private val q: String
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val currentPage = params.key ?: 1

        return try {
            val products = repository.getProductByQuery(q, currentPage)

            LoadResult.Page(
                data = products,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (products.isEmpty()) null else currentPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1)?:anchorPage?.nextKey?.minus(1)
        }
    }
}