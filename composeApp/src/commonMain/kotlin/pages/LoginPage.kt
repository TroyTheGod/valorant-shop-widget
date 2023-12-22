package pages

import Http
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import store.TokenStore

@Composable
fun LoginPage(
    onLoginStateChange: (Boolean) -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var startLogin by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1.0f))
            Text(
                text = "Riot Username",
                modifier = Modifier.align(alignment = Alignment.Start)
            )
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
                        val response = Http().getToken("CafeEv0", "xillxiii13021")
                        startLogin = false

                        if (response?.success == true) {
                            TokenStore().setToken(response.authTokenModel)
                            onLoginStateChange(true)
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(response?.reason ?: "")
                            }
                        }
                    }
                }
            ) {
                if (!startLogin) Text("Login") else CircularProgressIndicator()
            }
            OutlinedButton(
                modifier = Modifier.align(alignment = Alignment.End),
                onClick = {
                    onLoginStateChange(true)
                }
            ) {
                Text("Test")
            }
            Spacer(Modifier.weight(1.0f))
        }
    }
}
