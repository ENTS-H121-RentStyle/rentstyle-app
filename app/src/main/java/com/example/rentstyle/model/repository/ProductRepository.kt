package com.example.rentstyle.model.repository

import androidx.paging.*
import com.example.rentstyle.model.database.ProductDatabase
import com.example.rentstyle.model.database.ProductRemoteMediator
import com.example.rentstyle.model.local.LocalProduct
import com.example.rentstyle.model.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPagingApi::class)
class ProductRepository(
    private val database: ProductDatabase,
    private val apiService: ApiService
) {

    fun getProducts(): Flow<PagingData<LocalProduct>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = ProductRemoteMediator(apiService, database.productDao()),
            pagingSourceFactory = { database.productDao().getProducts() }
        ).flow
    }
}