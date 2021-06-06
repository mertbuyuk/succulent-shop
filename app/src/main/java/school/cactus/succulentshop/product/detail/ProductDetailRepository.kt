package school.cactus.succulentshop.product.detail

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import school.cactus.succulentshop.api.api
import school.cactus.succulentshop.product.ProductItem
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.Failure
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.IsLoading
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.Success
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.TokenExpired
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.UnexpectedError
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedFailure
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedProggresBar
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedSuccess
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedTokenExpired
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedUnexpectedError
import school.cactus.succulentshop.product.toProductItem
import school.cactus.succulentshop.product.toProductItemList

class ProductDetailRepository {

    suspend fun fetchProduct(productId: Int): Flow<ProductResult> = flow {

        emit(IsLoading)

        val result = try {
            api.getProductById(productId)
        } catch (ex: Exception) {
            null
        }

        when (result?.code()) {
            null -> emit(Failure)
            200 -> emit(Success(result.body()!!.toProductItem()))
            401 -> emit(TokenExpired)
            else -> emit(UnexpectedError)
        }
    }

    suspend fun fetchRelatedProduct(productId: Int): Flow<RelatedResult> = flow {

        emit(RelatedProggresBar)

        val relatedResult = try {
            api.relatedProducts(productId)
        } catch (ex: Exception) {
            null
        }

        when (relatedResult?.code()) {
            null -> emit(RelatedFailure)
            200 -> emit(RelatedSuccess(relatedResult.body()!!.products.toProductItemList()))
            401 -> emit(RelatedTokenExpired)
            else -> emit(RelatedUnexpectedError)
        }


    }

    sealed class RelatedResult {
        class RelatedSuccess(val product: List<ProductItem>?) : RelatedResult()
        object RelatedTokenExpired : RelatedResult()
        object RelatedUnexpectedError : RelatedResult()
        object RelatedFailure : RelatedResult()
        object RelatedProggresBar : RelatedResult()
    }

    sealed class ProductResult {
        class Success(val product: ProductItem) : ProductResult()
        object TokenExpired : ProductResult()
        object UnexpectedError : ProductResult()
        object Failure : ProductResult()
        object IsLoading : ProductResult()
    }
}