package school.cactus.succulentshop.product.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import school.cactus.succulentshop.R
import school.cactus.succulentshop.auth.JwtStore
import school.cactus.succulentshop.infra.BaseViewModel
import school.cactus.succulentshop.infra.snackbar.SnackbarAction
import school.cactus.succulentshop.infra.snackbar.SnackbarState
import school.cactus.succulentshop.product.ProductItem
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.Failure
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.ProductListProgressBar
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.Succes
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.TokenExpired
import school.cactus.succulentshop.product.list.ProductListRepository.ProductList.UnexpectedError

class ProductListViewModel(
    private val store: JwtStore,
    val repository: ProductListRepository
) : BaseViewModel() {

    private val _products = MutableLiveData<List<ProductItem>?>()
    val products: LiveData<List<ProductItem>?> = _products

    private val _isVisible = MutableLiveData<Boolean>()
    val isVisible: LiveData<Boolean> = _isVisible

    val itemClickListener: (ProductItem) -> Unit = {
        val action =
            ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(it.id)
        navigation.navigate(action)
    }

    init {
        fetchProducts()
    }

    private fun fetchProducts() = viewModelScope.launch {
        repository.fetchProducts().collect {
            when (it) {
                ProductListProgressBar -> _isVisible.value = true
                is Succes -> onSucces(it.products)
                TokenExpired -> onTokenExpired()
                UnexpectedError -> onUnexpectedError()
                Failure -> onFailure()
            }
        }


    }

    private fun onSucces(products: List<ProductItem>) {
        _products.value = products
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
        _isVisible.value = true
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.check_your_connection,
            length = Snackbar.LENGTH_INDEFINITE,
            action = SnackbarAction(
                text = R.string.retry,
                action = {
                    fetchProducts()
                }
            )
        )
    }

    fun toLoginFragment() {
        store.delete()
        navigation.navigate(ProductListFragmentDirections.actionProductListFragmentToLoginFragment())
    }

    private fun navigateToLogin() =
        navigation.navigate(ProductListFragmentDirections.actionProductListFragmentToLoginFragment())
}