package pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun StorePage(onLoginStateChange: (Boolean) -> Unit) {
    Text(
        text = "Store Page"
    )
}
