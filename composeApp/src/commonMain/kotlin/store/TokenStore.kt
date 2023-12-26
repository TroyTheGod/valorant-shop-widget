package store

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json
import model.AuthTokenModel

class TokenStore {
    private val settings: Settings by lazy { Settings() }


    fun getToken(): AuthTokenModel? {
        val jsonString = settings.getString("token", "");
        if (jsonString.isEmpty()) {
            return null
        }
        return Json.decodeFromString(
            AuthTokenModel.serializer(),
            jsonString
        )
    }

    fun setToken(value: AuthTokenModel?) {
        if (value != null) {
            settings.set(
                "token",
                Json.encodeToString(AuthTokenModel.serializer(), value)
            )
        }
    }

    fun setUUid(value: String?) {
        if (value != null) {
            settings.set(
                "uuid", value
            )
        }
    }

    fun getUUid(): String? {
        val uuid = settings.getString("uuid", "");
        if (uuid.isNotEmpty()) {
            return uuid;
        }
        return null
    }

    fun setCookie(value: String?) {
        if (value != null) {
            settings.set(
                "cookie", value
            )
        }
    }

    fun getCookie(): String? {
        val uuid = settings.getString("cookie", "");
        if (uuid.isNotEmpty()) {
            return uuid;
        }
        return null
    }

    fun clearToken() {
        settings.clear()
    }
}