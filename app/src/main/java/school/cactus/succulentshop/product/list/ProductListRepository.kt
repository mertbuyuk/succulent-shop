package school.cactus.succulentshop.product.list

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import school.cactus.succulentshop.api.api
import school.cactus.succulentshop.product.ProductItem
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.Failure
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.Succes
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.TokenExpired
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.UnexpectedError
import school.cactus.succulentshop.product.toProductItemList

class ProductListRepository {

    suspend fun fetchProducts(): Flow<ProductList> = flow {
        emit(ProductList.ProductListProgressBar)
        val response = try {
            api.listAllProducts()
        } catch (ex: Exception) {
            null
        }

        when (response?.code()) {
            null -> emit(Failure)
            200 -> {
                emit(Succes(response.body()!!.toProductItemList()))
            }
            in 400..499 -> emit(TokenExpired)
            else -> emit(UnexpectedError)
        }
    }

    sealed class ProductList {
        class Succes(val products: List<ProductItem>) : ProductList()
        object TokenExpired : ProductList()
        object UnexpectedError : ProductList()
        object Failure : ProductList()
        object ProductListProgressBar : ProductList()
    }
}