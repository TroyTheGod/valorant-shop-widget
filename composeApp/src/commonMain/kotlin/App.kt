import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import pages.LoginPage
import pages.StorePage
import store.TokenStore

@Composable
fun App() {
    MaterialTheme {
        var isLogin by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            val token = TokenStore().getCookie()
            if (token != null) {
                isLogin = true
            }
        }
        if (isLogin) {
            StorePage(
                onLoginStateChange = {
                    isLogin = it
                }
            )
        } else {
            LoginPage(
                onLoginStateChange = {
                    isLogin = it
                }
            )
        }
    }
}