package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MonetaElectricBlue
import com.example.ui.theme.MonetaSilver

@Composable
fun OrigamiLoadingSpinner(
    modifier: Modifier = Modifier,
    size: Dp = 60.dp,
    color: Color = MonetaElectricBlue,
    text: String? = "Loading...",
    fullScreen: Boolean = false
) {
    val content = @Composable {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.size(size)) {
                val transition = rememberInfiniteTransition()
                
                // Overall container continuously rotates in 3D space
                val containerRotation by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationZ = containerRotation }
                ) {
                    val tileSize = size / 2
                    
                    @Composable
                    fun FoldingTile(index: Int, xOffset: Dp, yOffset: Dp, origin: TransformOrigin, startDelay: Int) {
                        // Simulating the CSS cubic-bezier folding transition using Compose keyframes
                        val tileRotation by transition.animateFloat(
                            initialValue = 0f,
                            targetValue = 180f,
                            animationSpec = infiniteRepeatable(
                                animation = keyframes {
                                    durationMillis = 2400
                                    0f at startDelay with FastOutSlowInEasing
                                    180f at (startDelay + 600)
                                    180f at durationMillis
                                },
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                        
                        val tileAlpha by transition.animateFloat(
                            initialValue = 1f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(
                                animation = keyframes {
                                    durationMillis = 2400
                                    1f at startDelay
                                    0.3f at (startDelay + 600)
                                    0.3f at durationMillis
                                },
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(tileSize)
                                .offset(x = xOffset, y = yOffset)
                                .graphicsLayer {
                                    transformOrigin = origin
                                    // Fold logic based on index
                                    if (index % 2 == 0) rotationX = tileRotation else rotationY = tileRotation
                                    alpha = tileAlpha
                                }
                                // Distinct color-accented face tiles (adjusting alpha/brightness of base color)
                                .background(color.copy(alpha = 1f - (index * 0.15f))) 
                        )
                    }

                    // 4 distinct geometric tiles
                    // Top Left
                    FoldingTile(index = 0, xOffset = 0.dp, yOffset = 0.dp, origin = TransformOrigin(1f, 1f), startDelay = 0)
                    // Top Right
                    FoldingTile(index = 1, xOffset = tileSize, yOffset = 0.dp, origin = TransformOrigin(0f, 1f), startDelay = 300)
                    // Bottom Right
                    FoldingTile(index = 2, xOffset = tileSize, yOffset = tileSize, origin = TransformOrigin(0f, 0f), startDelay = 600)
                    // Bottom Left
                    FoldingTile(index = 3, xOffset = 0.dp, yOffset = tileSize, origin = TransformOrigin(1f, 0f), startDelay = 900)
                }
            }
            
            // Text with subtle pulse styling
            if (text != null) {
                Spacer(modifier = Modifier.height(24.dp))
                val pulseTransition = rememberInfiniteTransition()
                val textAlpha by pulseTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Text(
                    text = text.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MonetaSilver,
                    modifier = Modifier.alpha(textAlpha)
                )
            }
        }
    }

    if (fullScreen) {
        Box(
            modifier = modifier
                .fillMaxSize()
                // Subtle backdrop blur effect simulation with semi-transparent overlay
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            content()
        }
    }
}
