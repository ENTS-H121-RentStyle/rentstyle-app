package com.example.rentstyle.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import com.example.rentstyle.helpers.DataResult
import com.example.rentstyle.helpers.StatusResult
import com.example.rentstyle.model.database.ProductDatabase
import com.example.rentstyle.model.database.ProductRemoteMediator
import com.example.rentstyle.model.local.LocalProduct
import com.example.rentstyle.model.remote.response.SellerResponseData
import com.example.rentstyle.model.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

@OptIn(ExperimentalPagingApi::class)
class ProductRepository(
    private val database: ProductDatabase,
    private val apiService: ApiService
) {
    private val statusResult = MediatorLiveData<StatusResult>()
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

    suspend fun uploadProduct (productName: RequestBody,
                               sellerId: RequestBody,
                               category: RequestBody,
                               size: RequestBody,
                               image: MultipartBody.Part,
                               color: RequestBody,
                               desc: RequestBody,
                               rentPrice: RequestBody,
                               productPrice: RequestBody
    ) : LiveData<StatusResult> {
        statusResult.value = StatusResult.Loading

        try {
            val response = apiService.createNewProduct(productName, sellerId,
                category, size, image, color, desc, rentPrice, productPrice)

            if (response.code() == 201) {
                statusResult.value = StatusResult.Success("Success adding new product")
            } else {
                statusResult.value = StatusResult.Error("Failed adding new product")
            }
        } catch (e: Exception) {
            statusResult.value = StatusResult.Error(e.toString())
        }

        return statusResult
    }
}