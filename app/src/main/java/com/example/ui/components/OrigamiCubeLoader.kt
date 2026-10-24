package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke.Companion.DefaultMiter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun OrigamiCubeLoader(
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    text: String? = null,
    fullScreen: Boolean = false,
    colorTop: Color = Color(0xFF00E5FF), // Electric Blue
    colorLeft: Color = Color(0xFF7B1FA2), // Royal Purple
    colorRight: Color = Color(0xFF00C853) // Emerald Green
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CubeTransition")
    
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CubeRotationY"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CubeScale"
    )

    val content = @Composable {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .graphicsLayer {
                        this.rotationY = rotationY
                        this.scaleX = scale
                        this.scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.toPx()
                    val h = size.toPx()
                    val cx = w / 2
                    val cy = h / 2
                    val r = w / 2 * 0.9f // radius

                    // Isometric projection angles
                    val angle30 = Math.PI / 6
                    val dx = (r * cos(angle30)).toFloat()
                    val dy = (r * sin(angle30)).toFloat()

                    // Hexagon vertices
                    val pTop = Offset(cx, cy - r)
                    val pBottom = Offset(cx, cy + r)
                    val pTopLeft = Offset(cx - dx, cy - dy)
                    val pBottomLeft = Offset(cx - dx, cy + dy)
                    val pTopRight = Offset(cx + dx, cy - dy)
                    val pBottomRight = Offset(cx + dx, cy + dy)
                    val pCenter = Offset(cx, cy)

                    // Top Face
                    val pathTop = Path().apply {
                        moveTo(pTopLeft.x, pTopLeft.y)
                        lineTo(pTop.x, pTop.y)
                        lineTo(pTopRight.x, pTopRight.y)
                        lineTo(pCenter.x, pCenter.y)
                        close()
                    }
                    drawPath(pathTop, color = colorTop, style = Fill)

                    // Left Face
                    val pathLeft = Path().apply {
                        moveTo(pTopLeft.x, pTopLeft.y)
                        lineTo(pCenter.x, pCenter.y)
                        lineTo(pBottom.x, pBottom.y)
                        lineTo(pBottomLeft.x, pBottomLeft.y)
                        close()
                    }
                    drawPath(pathLeft, color = colorLeft, style = Fill)

                    // Right Face
                    val pathRight = Path().apply {
                        moveTo(pCenter.x, pCenter.y)
                        lineTo(pTopRight.x, pTopRight.y)
                        lineTo(pBottomRight.x, pBottomRight.y)
                        lineTo(pBottom.x, pBottom.y)
                        close()
                    }
                    drawPath(pathRight, color = colorRight, style = Fill)
                    
                    // Folding lines (origami style)
                    val stroke = Stroke(width = 1.5.dp.toPx(), miter = DefaultMiter)
                    val strokeColor = Color.White.copy(alpha = 0.4f)
                    drawPath(pathTop, color = strokeColor, style = stroke)
                    drawPath(pathLeft, color = strokeColor, style = stroke)
                    drawPath(pathRight, color = strokeColor, style = stroke)
                }
            }
            
            if (text != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }

    if (fullScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEE050505)), // Dark background overlay
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    } else {
        content()
    }
}
