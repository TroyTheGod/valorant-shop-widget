package org.example.valorantshopwidget.glance

import Http
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.AuthTokenModel
import store.TokenStore

class ValorantShopWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Box(modifier = GlanceModifier.background(GlanceTheme.colors.primary)) {
                    content()
                }
            }
        }
    }

    suspend fun downloadImageAsBitmap(url: String): Bitmap? {
        val client = HttpClient()

        // Making the network call on the IO dispatcher
        return withContext(Dispatchers.IO) {
            try {
                // Execute the GET request
                val byteArray = client.get(url).body<ByteArray>()

                // Convert ByteArray to Bitmap
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null // Handle exceptions or return a default image
            } finally {
                client.close()
            }
        }
    }

    @Composable
    fun content() {
        var loadFinish by remember { mutableStateOf(false) }
        var weapons = remember { arrayListOf<String>().toMutableStateList() }

        val size = LocalSize.current

        fun getImageUrl(index: Int): String {
            return "https://media.valorant-api.com/weaponskinlevels/${weapons[index]}/displayicon.png"
        }

        if (!loadFinish) {
            loadingView(
                loadFinish = {
                    loadFinish = it
                },
                setSkins = {
                    weapons.clear()
                    weapons.addAll(it)
                }
            )
        } else {
            Column(GlanceModifier.padding(20.0.dp)) {
                Row {
                    displayImage(
                        getImageUrl(0),
                        modifier = GlanceModifier.size(
                            size.width / 2 - 20.0.dp,
                            size.height / 2 - 20.0.dp
                        )
                    )
                    displayImage(
                        getImageUrl(1),
                        modifier = GlanceModifier.size(
                            size.width / 2 - 20.0.dp,
                            size.height / 2 - 20.0.dp
                        )
                    )
                }
                Row {
                    displayImage(
                        getImageUrl(2),
                        modifier = GlanceModifier.size(
                            size.width / 2 - 20.0.dp,
                            size.height / 2 - 20.0.dp
                        )
                    )
                    displayImage(
                        getImageUrl(3),
                        modifier = GlanceModifier.size(
                            size.width / 2 - 20.0.dp,
                            size.height / 2 - 20.0.dp
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun displayImage(url: String, modifier: GlanceModifier) {
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(Unit) {
            bitmap = downloadImageAsBitmap(url)
        }
        if (bitmap == null) {
            CircularProgressIndicator()
        } else {
            Image(
                provider = ImageProvider(bitmap!!),
                "skins",
                modifier = modifier,
            )
        }
    }

    @Composable
    fun loadingView(
        loadFinish: (Boolean) -> Unit,
        setSkins: (List<String>) -> Unit,
    ) {
        LaunchedEffect(Unit) {
            val http = Http()
            var token: AuthTokenModel? = null

            val success = http.restoreCookie()
            if (!success) {
                return@LaunchedEffect
            }

            val wrapper = http.cookieReAuth()
            if (wrapper.success) {
                token = wrapper.authTokenModel
            } else {
                return@LaunchedEffect
            }

            var uuid = TokenStore().getUUid()
            if (uuid == null) {
                val result = Http().getPlayerUuid(token!!)
                if (result == null) {
                    return@LaunchedEffect
                } else {
                    uuid = result
                }
            }

            val entitlementToken = Http().getEntitlementToken(token!!)
            if (entitlementToken == null) {
                return@LaunchedEffect
            }
            val newWeapons = Http().getStoreFront(token!!, uuid!!, entitlementToken!!) ?: listOf()
            setSkins(newWeapons)
            loadFinish(true)
        }

        Text(
            "Loading...",
            style = TextStyle(color = ColorProvider(Color.White)),
        )
    }
}

class ValorantShopWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ValorantShopWidget()
}