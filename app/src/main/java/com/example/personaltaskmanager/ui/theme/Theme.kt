package com.example.personaltaskmanager.ui.theme

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp


private val LightColors = lightColors(
    primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF534A8A),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6)
)


@Composable
fun TaskManagerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColors,
        shapes = MaterialTheme.shapes.copy(medium = MaterialTheme.shapes.medium),
        content = content
    )
}