package school.cactus.succulentshop.signup


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import school.cactus.succulentshop.api.GenericErrorResponse
import school.cactus.succulentshop.api.api
import school.cactus.succulentshop.api.signup.data.SignupRequest
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.Failure
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.RequestFail
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.Succes
import school.cactus.succulentshop.signup.SignupRepository.SignupResults.UnexpectedError


class SignupRepository {
    suspend fun sendSignupRequest(
        email: String,
        password: String,
        username: String
    ): SignupResults {

        val requestSignup = SignupRequest(email, password, username)
        val response = try {
            api.register(requestSignup)
        } catch (ex: Exception) {
            null
        }

        return when (response?.code()) {
            null -> Failure
            200 -> Succes(response.body()!!.jwt)
            in 400..499 -> RequestFail(response.errorBody()!!.errorMessage())
            else -> UnexpectedError
        }
    }

    private fun ResponseBody.errorMessage(): String {
        val errorBody = string()
        val gson: Gson = GsonBuilder().create()
        val signupErrorResponse = gson.fromJson(errorBody, GenericErrorResponse::class.java)
        return signupErrorResponse.message[0].messages[0].message
    }

    sealed class SignupResults {
        class Succes(val jwt: String) : SignupResults()
        class RequestFail(val errorMessage: String) : SignupResults()
        object UnexpectedError : SignupResults()
        object Failure : SignupResults()
    }
}