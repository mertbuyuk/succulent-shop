package school.cactus.succulentshop.login

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import school.cactus.succulentshop.R
import school.cactus.succulentshop.auth.JwtStore
import school.cactus.succulentshop.hideKeyboard
import school.cactus.succulentshop.infra.BaseViewModel
import school.cactus.succulentshop.infra.snackbar.SnackbarAction
import school.cactus.succulentshop.infra.snackbar.SnackbarState
import school.cactus.succulentshop.login.LoginRepository.LoginResult.ClientError
import school.cactus.succulentshop.login.LoginRepository.LoginResult.Failure
import school.cactus.succulentshop.login.LoginRepository.LoginResult.Success
import school.cactus.succulentshop.login.LoginRepository.LoginResult.UnexpectedError
import school.cactus.succulentshop.login.validation.IdentifierValidator
import school.cactus.succulentshop.login.validation.PasswordValidator

class LoginViewModel(
    private val store: JwtStore,
    private val repository: LoginRepository
) : BaseViewModel() {

    private val identifierValidator = IdentifierValidator()
    private val passwordValidator = PasswordValidator()

    val identifier = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    private val _identifierErrorMessage = MutableLiveData<Int>()
    private val _passwordErrorMessage = MutableLiveData<Int>()

    val identifierErrorMessage: LiveData<Int> = _identifierErrorMessage
    val passwordErrorMessage: LiveData<Int> = _passwordErrorMessage

    init {
        if (store.loadJwt() != null) {
            navigateToList()
        }
    }

    fun onLoginButtonClicked(view: View) = viewModelScope.launch {
        if (isIdentifierValid() and isPasswordValid()) {
            view.hideKeyboard()

            val result =
                repository.sendLoginRequest(identifier.value.orEmpty(), password.value.orEmpty())

            when (result) {
                is Success -> onSuccess(result.jwt)
                is ClientError -> onClientError(result.errorMessage)
                UnexpectedError -> onUnexpectedError()
                Failure -> onFailure(view)
            }
        }
    }

    private fun onSuccess(jwt: String) {
        navigateToList()
        store.save(jwt)
    }

    private fun onClientError(errorMessage: String) {
        if (errorMessage == null) return onUnexpectedError()

        try {
            _snackbarStateData.value = SnackbarState(
                error = errorMessage,
                length = Snackbar.LENGTH_LONG,
            )
        } catch (ex: JsonSyntaxException) {
            onUnexpectedError()
        }
    }

    private fun onUnexpectedError() {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.unexpected_error,
            length = Snackbar.LENGTH_LONG,
        )
    }

    private fun onFailure(view: View) {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.onFailure,
            length = Snackbar.LENGTH_INDEFINITE,
            action = SnackbarAction(
                text = R.string.retry,
                action = {
                    onLoginButtonClicked(view)
                }
            )
        )
    }

    fun createAccountBtnClick() {
        navigation.navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
    }

    private fun isIdentifierValid(): Boolean {
        _identifierErrorMessage.value = identifierValidator.validate(identifier.value.orEmpty())
        return _identifierErrorMessage.value == null
    }

    private fun isPasswordValid(): Boolean {
        _passwordErrorMessage.value = passwordValidator.validate(password.value.orEmpty())
        return _passwordErrorMessage.value == null
    }

    private fun navigateToList() {
        val directions =
            LoginFragmentDirections.actionLoginFragmentToProductListFragment()
        navigation.navigate(directions)
    }
}