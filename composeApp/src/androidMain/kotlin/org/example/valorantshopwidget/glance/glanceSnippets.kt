package org.example.valorantshopwidget.glance

import Http
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import model.AuthTokenModel
import store.TokenStore

class ValorantShopWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                content()
            }
        }
    }

    @Composable
    fun content() {
        var loadFinish by remember { mutableStateOf(false) }
        var weapons = remember { arrayListOf<String>().toMutableStateList() }

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
            Column {
                Row {
                    //TODO kamelImage may not be able to use here
                    KamelImage(
                        asyncPainterResource(getImageUrl(0)),
                        "skins",
                        onLoading = {
                            Text(
                                "Loading...$${getImageUrl(0)}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        },
                        onFailure = {
                            Text(
                                "$${getImageUrl(0)} fail, ${it.toString()}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        }
                    )
                    KamelImage(
                        asyncPainterResource(getImageUrl(1)),
                        "skins",
                        onLoading = {
                            Text(
                                "Loading...$${getImageUrl(1)}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        },
                        onFailure = {
                            Text(
                                "$${getImageUrl(1)} fail, ${it.toString()}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        }
                    )
                }
                Row {
                    KamelImage(
                        asyncPainterResource(getImageUrl(2)),
                        "skins",
                        onLoading = {
                            Text(
                                "Loading...$${getImageUrl(2)}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        },
                        onFailure = {
                            Text(
                                "$${getImageUrl(2)} fail, ${it.toString()}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        }
                    )
                    KamelImage(
                        asyncPainterResource(getImageUrl(3)),
                        "skins",
                        onLoading = {
                            Text(
                                "Loading...$${getImageUrl(3)}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        },
                        onFailure = {
                            Text(
                                "$${getImageUrl(3)} fail, ${it.toString()}",
                                style = TextStyle(color = ColorProvider(Color.White)),
                            )
                        }
                    )
                }
            }
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