package pages

import Http
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.AuthTokenModel
import store.TokenStore


@Composable
fun StorePage(onLoginStateChange: (Boolean) -> Unit) {
    var token: AuthTokenModel? = null
    fun logout() {
        TokenStore().clearToken()
        onLoginStateChange(false)
    }
    LaunchedEffect(Unit) {
        token = TokenStore().getToken()
        if (token == null) {
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
        Http().getStoreFront(token!!, uuid!!, entitlementToken!!)
    }
    Column {
        Text(
            text = "Store Page"
        )
        OutlinedButton(
            onClick = {
                GlobalScope.launch {

                }
            }
        ) {
            Text("Test")
        }
    }

}
