package com.solutions.upaay.globalComponents.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
    ) {
        CircularProgressIndicator(modifier = Modifier
            .align(Alignment.Center)
            .size(24.dp))
    }
}

@Composable
fun CircularProgressLoadingScreenWithText(text : String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.85f))
    ) {
        CircularProgressIndicator(modifier = Modifier
            .size(24.dp))
//        Text(text = text, style = MaterialTheme.typography.bodySmall, color = Color.White, modifier = Modifier.padding(top = 15.dp))
    }
}

fun Modifier.blurBackgroundEffect(blurRadiusX: Float, blurRadiusY: Float): Modifier {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.graphicsLayer(
            renderEffect = RenderEffect.createBlurEffect(blurRadiusX, blurRadiusY, Shader.TileMode.MIRROR)
                .asComposeRenderEffect()
        )
    } else {
        this
    }
}