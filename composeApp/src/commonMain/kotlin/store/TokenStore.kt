package store

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json
import model.AuthTokenModel

class TokenStore {
    private val settings by lazy { Settings }
    private val observableSettings by lazy { settings as ObservableSettings }


    fun getToken(): AuthTokenModel? {
        return Json.decodeFromString(
            AuthTokenModel.serializer(),
            observableSettings.getString("token", "")
        )
    }

    fun setToken(value: AuthTokenModel?) {
        if (value != null) {
            observableSettings.set(
                "token",
                Json.encodeToString(AuthTokenModel.serializer(), value)
            )
        }
    }
}