package school.cactus.succulentshop.signup

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
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.Failure
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.RequestFail
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.Succes
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.UnexpectedError
import school.cactus.succulentshop.signup.validation.IdentifierValidator
import school.cactus.succulentshop.signup.validation.PasswordValidator
import school.cactus.succulentshop.signup.validation.UsernameValidator

class SignupViewModel(
    val store: JwtStore,
    val repository: SignupRepository
) : BaseViewModel() {

    private val usernameValidator = UsernameValidator()
    private val identifierValidator = IdentifierValidator()
    private val passwordValidator = PasswordValidator()

    val identifier = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val _usernameErrorMessage = MutableLiveData<Int>()
    val _identifierErrorMessage = MutableLiveData<Int>()
    val _passwordErrorMessage = MutableLiveData<Int>()

    val usernameErrorMessage: LiveData<Int> = _usernameErrorMessage
    val identifierErrorMessage: LiveData<Int> = _identifierErrorMessage
    val passwordErrorMessage: LiveData<Int> = _passwordErrorMessage

    fun onSignupButtonClicked(view: View) = viewModelScope.launch {
        if (isUsernameValid() and isIdentifierValid() and isPasswordValid()) {
            view.hideKeyboard()

            val result = repository.sendSignupRequest(
                identifier.value.orEmpty(),
                password.value.orEmpty(),
                username.value.orEmpty()
            )

            when (result) {
                is Succes -> onSucces(result.jwt)
                is RequestFail -> onRequestFail(result.errorMessage)
                UnexpectedError -> unexpectedError()
                Failure -> onFailure(view)
            }
        }
    }

    private fun onSucces(jwt: String) {
        val directions = SignupFragmentDirections.actionSignupFragmentToProductListFragment()
        navigation.navigate(directions)
        store.save(jwt)
    }

    private fun onRequestFail(errorMessage: String) {
        if (errorMessage == null) return unexpectedError()

        try {
            _snackbarStateData.value = SnackbarState(
                error = errorMessage,
                length = Snackbar.LENGTH_LONG,
            )
        } catch (ex: JsonSyntaxException) {
            unexpectedError()
        }
    }

    private fun unexpectedError() {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.unexpected_error,
            length = Snackbar.LENGTH_LONG
        )
    }

    private fun onFailure(view: View) {
        _snackbarStateData.value = SnackbarState(
            errorRes = R.string.onFailure,
            length = Snackbar.LENGTH_INDEFINITE,
            action = SnackbarAction(
                text = R.string.retry,
                action = {
                    onSignupButtonClicked(view)
                }
            )
        )
    }

    fun haveAnAccountBtnClicked() {
        val directions = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
        navigation.navigate(directions)
    }

    fun isUsernameValid(): Boolean {
        _usernameErrorMessage.value = usernameValidator.validate(username.value.orEmpty())
        return _usernameErrorMessage.value == null
    }

    fun isIdentifierValid(): Boolean {
        _identifierErrorMessage.value = identifierValidator.validate(identifier.value.orEmpty())
        return _identifierErrorMessage.value == null
    }

    fun isPasswordValid(): Boolean {
        _passwordErrorMessage.value = passwordValidator.validate(password.value.orEmpty())
        return _passwordErrorMessage.value == null
    }
}