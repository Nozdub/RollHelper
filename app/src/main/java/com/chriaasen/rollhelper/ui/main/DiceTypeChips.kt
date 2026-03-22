package com.chriaasen.rollhelper.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp

private val chipShape = RoundedCornerShape(8.dp)
private val accentStripShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)

private fun accentColorFor(diceType: String): Color = when (diceType) {
    "d4"  -> Color(0xFFBB3D80)
    "d6"  -> Color(0xFFBADFFF)
    "d8"  -> Color(0xFF09B809)
    "d10" -> Color(0xFFD45800)
    "d12" -> Color(0xFFA02BFF)
    "d20" -> Color(0xFFFF2B2B)
    else  -> Color.White
}

@Composable
fun DiceTypeChips(
    onDiceSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val diceTypes = listOf("d4", "d6", "d8", "d10", "d12", "d20")
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        diceTypes.forEach { diceType ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .shadow(2.dp, chipShape)
                    .background(Color(0xFF383838).copy(alpha = 0.92f), chipShape)
                    .clickable { onDiceSelected(diceType) }
            ) {
                // Per-die accent strip at the top edge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.TopCenter)
                        .background(accentColorFor(diceType), accentStripShape)
                )
                Text(
                    text = diceType.uppercase(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.25f),
                            offset = Offset(2f, 2f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }
    }
}
