package com.chriaasen.rollhelper.ui.components

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.chriaasen.rollhelper.R

import com.example.rollhelper4.audio.AudioManager
import com.chriaasen.rollhelper.ui.storage.DataStoreManager
import kotlinx.coroutines.delay

@Composable
fun DragonAnimation(
    context: Context,
    dataStoreManager: DataStoreManager, // Added parameter
    isTriggered: Boolean,
    onFrameUpdate: (Int) -> Unit, // New callback for dice-clearing logic
    onAnimationEnd: () -> Unit
) {
    // Dragon frames (resource IDs for `tile000` to `tile014`)
    val dragonFrames = listOf(
        R.drawable.tile000, R.drawable.tile001, R.drawable.tile002,
        R.drawable.tile003, R.drawable.tile004, R.drawable.tile005,
        R.drawable.tile006, R.drawable.tile007, R.drawable.tile008,
        R.drawable.tile009, R.drawable.tile010, R.drawable.tile011,
        R.drawable.tile012, R.drawable.tile013, R.drawable.tile014
    )

    // State to control the current frame and vertical offset
    var currentDragonFrame by remember { mutableStateOf(dragonFrames[0]) }
    var dragonVerticalOffset by remember { mutableStateOf(1200) } // Start well below the screen
    var animationComplete by remember { mutableStateOf(false) }

    if (isTriggered) {
        // Play the dragon roar sound if the animation is triggered and audio is enabled
        LaunchedEffect(Unit) {
            val audioManager = AudioManager(context, dataStoreManager)

            // Play sound only if audio is enabled
            audioManager.playAudio(R.raw.dragon)
        }
    }

    // Frame update loop with callback for dice-clearing logic
    LaunchedEffect(Unit) {
        while (!animationComplete) {
            for ((index, frame) in dragonFrames.withIndex()) {
                currentDragonFrame = frame
                onFrameUpdate(index) // Trigger callback on each frame
                delay(40L) // Adjust for frame speed
            }
        }
    }

    // Vertical movement animation
    LaunchedEffect(Unit) {
        val startOffset = 1200 // Start well below the screen
        val endOffset = -500 // Exit well above the screen
        val durationMs = 2500L // Total duration of the movement
        val steps = 50 // Number of animation steps
        val stepSize = (startOffset - endOffset) / steps
        val stepDuration = durationMs / steps

        dragonVerticalOffset = startOffset
        repeat(steps) {
            dragonVerticalOffset -= stepSize
            delay(stepDuration)
        }

        animationComplete = true
        onAnimationEnd() // Trigger callback when animation ends
    }

    // Render the dragon
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .offset(y = dragonVerticalOffset.dp)
            .zIndex(1f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = currentDragonFrame),
            contentDescription = "Flying dragon animation",
            modifier = Modifier.size(370.dp) // Dragon size
        )
    }
}
