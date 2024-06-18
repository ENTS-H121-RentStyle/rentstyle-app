package com.example.rentstyle.helpers.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.remote.retrofit.ApiService

class ProductPagingSource(
    private val apiService: ApiService,
    private val userId: String
) : PagingSource<Int, Product>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getRecommendations(userId, page)
            if (response.isSuccessful) {
                val products = response.body()?.products ?: emptyList()
                LoadResult.Page(
                    data = products,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (page < 3) page + 1 else null // Hanya load hingga page 3
                )
            } else {
                LoadResult.Error(Exception("API Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
