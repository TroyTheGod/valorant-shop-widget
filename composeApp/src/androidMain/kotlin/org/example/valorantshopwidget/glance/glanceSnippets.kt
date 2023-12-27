package org.example.valorantshopwidget.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

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
        var loading by remember { mutableStateOf(false) }
        if (loading) {
            loadingView()
        } else {
            //TODO load cookie
            // fetch data
            // display image
        }
    }

    @Composable
    fun loadingView() {
        Text(
            "Loading...",
            style = TextStyle(color = ColorProvider(Color.White)),
        )
    }
}

class ValorantShopWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ValorantShopWidget()
}