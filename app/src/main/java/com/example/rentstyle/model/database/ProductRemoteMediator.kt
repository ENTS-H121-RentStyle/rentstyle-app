package com.example.rentstyle.model.database


import androidx.paging.*
import com.example.rentstyle.model.Product
import com.example.rentstyle.model.local.LocalProduct
import com.example.rentstyle.model.remote.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val apiService: ApiService,
    private val productDao: ProductDAO
) : RemoteMediator<Int, LocalProduct>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, LocalProduct>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                if (lastItem == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                } else {
                    state.pages.size + 1
                }
            }
        }

        return try {
            val pageSize = state.config.pageSize
            val response = apiService.getProducts(page, pageSize)
            if (response.isSuccessful) {
                val products = response.body().orEmpty()
                productDao.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        productDao.clearAll()
                    }
                    productDao.insertAll(products.map { it.toLocalProduct() })
                }
                MediatorResult.Success(endOfPaginationReached = products.isEmpty())
            } else {
                MediatorResult.Error(HttpException(response))
            }
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
}
