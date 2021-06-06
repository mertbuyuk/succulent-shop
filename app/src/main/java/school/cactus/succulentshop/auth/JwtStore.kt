package school.cactus.succulentshop.auth

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class JwtStore(context: Context) {

    val prefs = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)

    fun save(jwt: String) {
        prefs.edit {
            putString(PREFS_KEY_JWT, jwt).commit()
        }
    }

    fun loadJwt(): String? = prefs.getString(PREFS_KEY_JWT, null)

    fun delete() {
        prefs.edit(commit = true) {
            remove(PREFS_KEY_JWT)
        }
    }

    companion object {
        const val PREFS_FILE_NAME = "jwt_store"
        const val PREFS_KEY_JWT = "jwt"
    }
}