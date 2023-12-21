import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        var userName by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var startLogin by remember { mutableStateOf(false) }

        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1.0f))
            Text(text = "Riot Username", modifier = Modifier.align(alignment = Alignment.Start))
            OutlinedTextField(
                value = userName,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Username")
                },
                onValueChange = {
                    userName = it
                }
            )
            Spacer(Modifier.height(20.0.dp))
            Text(text = "Password", modifier = Modifier.align(alignment = Alignment.Start))
            OutlinedTextField(
                value = password,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                placeholder = {
                    Text("Password")
                },
                onValueChange = {
                    password = it
                }

            )
            Spacer(Modifier.height(20.0.dp))
            OutlinedButton(
                enabled = !startLogin,
                modifier = Modifier.align(alignment = Alignment.End),
                onClick = {
                    GlobalScope.launch {
                        startLogin = true
                        Http().getToken("CafeEv0", "xillxiii13021")
                        startLogin = false
                    }
                }
            ) {
                if (!startLogin) Text("Login") else CircularProgressIndicator()
            }
            Spacer(Modifier.weight(1.0f))
        }
    }
}