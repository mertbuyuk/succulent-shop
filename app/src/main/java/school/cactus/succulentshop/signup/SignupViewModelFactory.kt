package school.cactus.succulentshop.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import school.cactus.succulentshop.auth.JwtStore

class SignupViewModelFactory(
    private val store: JwtStore,
    private val repository: SignupRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        SignupViewModel(store, repository) as T
}