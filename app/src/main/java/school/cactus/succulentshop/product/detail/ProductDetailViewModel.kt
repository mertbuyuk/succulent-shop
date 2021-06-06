package school.cactus.succulentshop.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import school.cactus.succulentshop.R
import school.cactus.succulentshop.infra.BaseViewModel
import school.cactus.succulentshop.infra.snackbar.SnackbarAction
import school.cactus.succulentshop.infra.snackbar.SnackbarState
import school.cactus.succulentshop.product.ProductItem
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.Failure
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.Success
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.TokenExpired
import school.cactus.succulentshop.product.detail.ProductDetailRepository.ProductResult.UnexpectedError
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedFailure
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedProggresBar
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedSuccess
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedTokenExpired
import school.cactus.succulentshop.product.detail.ProductDetailRepository.RelatedResult.RelatedUnexpectedError

class ProductDetailViewModel(
    private val productId: Int,
    private val repository: ProductDetailRepository
) : BaseViewModel() {

    private val _product = MutableLiveData<ProductItem?>()
    val product: LiveData<ProductItem?> = _product

    private val _products = MutableLiveData<List<ProductItem?>>()
    val products: LiveData<List<ProductItem?>> = _products

    private val _isVisible = MutableLiveData<Boolean>()
    val isVisible: LiveData<Boolean> = _isVisible

    val itemClickListener: (ProductItem) -> Unit = {
        val action =
            ProductDetailFragmentDirections.actionProductDetailFragmentSelf(it.id)
        navigation.navigate(action)
    }

    init {
        fetchProduct()
        fetchRelatedProducts()
    }

    private fun fetchProduct() = viewModelScope.launch {
        repository.fetchProduct(productId).collect {
            when (it) {
                ProductDetailRepository.ProductResult.IsLoading -> _isVisible.value = true
                is Success -> onSucces(it.product)
                TokenExpired -> onTokenExpired()
                UnexpectedError -> onUnexpectedError()
                Failure -> onFailure()
            }
        }
    }

    fun fetchRelatedProducts() = viewModelScope.launch {
        repository.fetchRelatedProduct(productId).collect {
            when (it) {
                RelatedProggresBar -> _isVisible.value = true
                is RelatedSuccess -> onRelatedSucces(it.product)
                RelatedTokenExpired -> onTokenExpired()
                RelatedUnexpectedError -> onUnexpectedError()
                RelatedFailure -> onRelatedFail()
            }
        }
    }

    private fun onRelatedFail() {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.check_your_connection,
            length = Snackbar.LENGTH_INDEFINITE,
            action = SnackbarAction(
                text = R.string.retry,
                action = {
                    fetchRelatedProducts()
                }
            )
        )
    }

    private fun onRelatedSucces(product: List<ProductItem>?) {
        _products.value = product.orEmpty()
        _isVisible.value = false
    }

    private fun onSucces(product: ProductItem) {
        _product.value = product
        _isVisible.value = false
    }

    private fun onTokenExpired() {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.your_session_is_expired,
            length = Snackbar.LENGTH_INDEFINITE,
            action = SnackbarAction(
                text = R.string.log_in,
                action = {
                    navigateToLogin()
                }
            )
        )
    }

    private fun onUnexpectedError() {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.unexpected_error,
            length = Snackbar.LENGTH_LONG,
        )
    }

    private fun onFailure() {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.check_your_connection,
            length = Snackbar.LENGTH_INDEFINITE,
            action = SnackbarAction(
                text = R.string.retry,
                action = {
                    fetchProduct()
                }
            )
        )
    }

    private fun navigateToLogin() =
        navigation.navigate(ProductDetailFragmentDirections.actionProductDetailFragmentToLoginFragment())
}