package store

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class TokenStore {
    private val settings: Settings by lazy { Settings() }

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
        val cookie = settings.getString("cookie", "");
        if (cookie.isNotEmpty()) {
            return cookie;
        }
        return null
    }

    fun clearToken() {
        settings.clear()
    }
}