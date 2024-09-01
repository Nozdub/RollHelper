package com.example.rollhelper.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.rollhelper.R

@Composable
fun DiceFace(diceType: String, diceValue: Int, rollTrigger: Boolean) {
    // Rotation state
    val rotation = remember { Animatable(0f) }
    var showResult by remember { mutableStateOf(false) }

    LaunchedEffect(rollTrigger) {  // Tied to the rollTrigger
        showResult = false
        rotation.snapTo(0f) // Reset rotation to 0 before starting a new rotation
        rotation.animateTo(
            targetValue = 1080f, //
            animationSpec = tween(durationMillis = 1200) // 1.2 seconds for two rotations
        )
        showResult = true
    }

    // Horizontal adjustment of text
    val offsetX = when (diceType) {
        "D4" -> -1.dp
        "D6" -> 16.dp
        "D8" -> 7.dp
        "D10" -> 18.dp
        "D12" -> 2.dp
        "D20" -> 4.dp
        else -> 0.dp
    }
    // Vertical adjustment of text
    val offsetY = when (diceType) {
        "D4" -> 10.dp
        "D6" -> 0.dp
        "D8" -> -10.dp
        "D10" -> 0.dp
        "D12" -> -4.dp
        "D20" -> -1.dp
        else -> 0.dp
    }

    Box(
        modifier = Modifier
            .size(120.dp) // modifies size of dice icons, care as it requires change in above.
            .padding(4.dp)
            .graphicsLayer {
                rotationZ = rotation.value
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f) // Centered rotation
            }
    ) {
        Image(
            painter = painterResource(id = getDiceDrawable(diceType)),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
        )
        if (showResult) {
            Text(
                text = diceValue.toString(),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetX, y = offsetY)
            )
        }
    }
}

fun getDiceDrawable(diceType: String): Int {
    return when (diceType) {
        "D4" -> R.drawable.d4_face
        "D6" -> R.drawable.d6_face
        "D8" -> R.drawable.d8_face
        "D10" -> R.drawable.d10_face
        "D12" -> R.drawable.d12_face
        "D20" -> R.drawable.d20_face
        else -> R.drawable.d6_face // Default to D6 if unknown
    }
}
