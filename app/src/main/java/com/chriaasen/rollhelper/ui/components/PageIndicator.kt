package com.chriaasen.rollhelper.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun PageIndicator(
    currentPage: Int,
    pageCount: Int,
    scrollOffsetFraction: Float,
    modifier: Modifier = Modifier
) {
    val dotSize = 10.dp
    val dotSpacing = 8.dp
    val onBg = MaterialTheme.colorScheme.onBackground
    val dotColor = onBg.copy(alpha = 0.22f)
    val wormColor = onBg.copy(alpha = 0.70f)
    val canvasWidth = dotSize * pageCount + dotSpacing * (pageCount - 1)

    val rawScrollPos = (currentPage + scrollOffsetFraction).coerceIn(0f, (pageCount - 1).toFloat())

    // Animate the scroll position with a sluggish spring so the droplet trails behind
    // the real pager position, giving it a heavy liquid feel.
    val scrollPos by animateFloatAsState(
        targetValue = rawScrollPos,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "indicatorScroll"
    )

    // Track swipe direction across frames so the droplet head always faces the right way.
    var movingRight by remember { mutableStateOf(true) }
    val prevScrollPos = remember { mutableStateOf(scrollPos) }
    SideEffect {
        if (abs(scrollPos - prevScrollPos.value) > 0.001f) {
            movingRight = scrollPos > prevScrollPos.value
            prevScrollPos.value = scrollPos
        }
    }

    Canvas(modifier = modifier.size(width = canvasWidth, height = dotSize)) {
        val dotSizePx = dotSize.toPx()
        val stepPx = (dotSize + dotSpacing).toPx()
        val r = dotSizePx / 2f
        val cy = center.y

        // Background dots
        for (page in 0 until pageCount) {
            drawCircle(
                color = dotColor,
                radius = r,
                center = Offset(page * stepPx + r, cy)
            )
        }

        val leftIndex = floor(scrollPos).toInt().coerceIn(0, pageCount - 1)
        val rightIndex = ceil(scrollPos).toInt().coerceIn(0, pageCount - 1)
        val t = scrollPos - leftIndex

        fun smoothstep(x: Float) = x * x * (3f - 2f * x)
        // Leading edge moves immediately, trailing edge starts at t = 0.5
        val leadT = smoothstep((t * 2f).coerceIn(0f, 1f))
        val trailT = smoothstep(((t - 0.5f) * 2f).coerceIn(0f, 1f))

        val leftCx = leftIndex * stepPx + r
        val rightCx = rightIndex * stepPx + r

        val wormLeft  = leftCx + (rightCx - leftCx) * trailT - r
        val wormRight = leftCx + (rightCx - leftCx) * leadT  + r

        if (leftIndex == rightIndex) {
            // Perfectly settled on a dot — clean circle
            drawCircle(color = wormColor, radius = r, center = Offset(leftCx, cy))
        } else if (movingRight) {
            // ── Droplet: circular head on right, pointed tail on left ──────────
            val headCx = wormRight - r
            val tailX  = wormLeft

            val path = Path()
            path.moveTo(tailX, cy)
            // Top edge: bezier from tail point up to the top of the head circle
            path.cubicTo(
                tailX + (headCx - tailX) * 0.5f, cy,   // depart tail horizontally
                headCx - r * 0.4f, cy - r,              // arrive tangentially at head top
                headCx, cy - r
            )
            // Right semicircle (the head)
            path.arcTo(
                rect = Rect(headCx - r, cy - r, headCx + r, cy + r),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            // Bottom edge: bezier from head bottom back to the tail point
            path.cubicTo(
                headCx - r * 0.4f, cy + r,              // depart head bottom tangentially
                tailX + (headCx - tailX) * 0.5f, cy,   // arrive at tail horizontally
                tailX, cy
            )
            path.close()
            drawPath(path = path, color = wormColor)
        } else {
            // ── Droplet: circular head on left, pointed tail on right ───────────
            val headCx = wormLeft + r
            val tailX  = wormRight

            val path = Path()
            path.moveTo(tailX, cy)
            // Top edge: bezier from tail point up to the top of the head circle
            path.cubicTo(
                tailX - (tailX - headCx) * 0.5f, cy,   // depart tail horizontally
                headCx + r * 0.4f, cy - r,              // arrive tangentially at head top
                headCx, cy - r
            )
            // Left semicircle (the head) — counterclockwise
            path.arcTo(
                rect = Rect(headCx - r, cy - r, headCx + r, cy + r),
                startAngleDegrees = -90f,
                sweepAngleDegrees = -180f,
                forceMoveTo = false
            )
            // Bottom edge: bezier from head bottom back to the tail point
            path.cubicTo(
                headCx + r * 0.4f, cy + r,              // depart head bottom tangentially
                tailX - (tailX - headCx) * 0.5f, cy,   // arrive at tail horizontally
                tailX, cy
            )
            path.close()
            drawPath(path = path, color = wormColor)
        }
    }
}
