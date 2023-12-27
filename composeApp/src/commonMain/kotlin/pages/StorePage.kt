package pages

import Http
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.AuthTokenModel
import store.TokenStore


@Composable
fun StorePage(onLoginStateChange: (Boolean) -> Unit) {
    var weapons = remember { arrayListOf<String>().toMutableStateList() }

    var token: AuthTokenModel? = null

    fun logout() {
        TokenStore().clearToken()
        onLoginStateChange(false)
    }
    LaunchedEffect(Unit) {
        val http = Http()
        val success = http.restoreCookie()
        if (!success) {
            logout()
            return@LaunchedEffect
        }

        val wrapper = http.cookieReAuth()
        if (wrapper.success) {
            token = wrapper.authTokenModel
        } else {
            logout()
            return@LaunchedEffect
        }

        var uuid = TokenStore().getUUid()
        if (uuid == null) {
            val result = Http().getPlayerUuid(token!!)
            if (result == null) {
                logout()
                return@LaunchedEffect
            } else {
                uuid = result
            }
        }
        val entitlementToken = Http().getEntitlementToken(token!!)
        if (entitlementToken == null) {
            logout()
            return@LaunchedEffect
        }
        val newWeapons = Http().getStoreFront(token!!, uuid!!, entitlementToken!!) ?: listOf()
        weapons.clear()
        weapons.addAll(newWeapons)
    }

    Column {
        Text(
            text = if (weapons.isEmpty()) "Store Page" else weapons.first()
        )
        for (weapon in weapons) {
            val url = "https://media.valorant-api.com/weaponskinlevels/$weapon/displayicon.png"
            KamelImage(
                asyncPainterResource(url),
                "skins:$weapon",
                modifier = Modifier.height(100.0.dp),
                onLoading = {
                    Text("Loading...$url")
                },
                onFailure = {
                    println(url)
                    Text("$url fail, ${it.toString()}")
                }
            )
        }

    }

}
