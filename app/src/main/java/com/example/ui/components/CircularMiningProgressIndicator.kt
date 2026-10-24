package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-tech circular mining progress indicator showing real-time active mining state,
 * dynamic hash rate progression, orbital particle telemetry, and session countdown.
 */
@Composable
fun CircularMiningProgressIndicator(
    isMining: Boolean,
    autoMiningActive: Boolean,
    sessionCompleted: Boolean,
    progress: Float,
    remainingSeconds: Int,
    baseHashRate: Double,
    multiplier: Double,
    minedSoFar: Double,
    size: Dp = 270.dp,
    onStartMiningClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Dynamic animated progress with smooth interpolation
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "MiningProgressAnimation"
    )

    // Infinite transitions for active telemetry (orbital rotation, pulse glow, and hash micro-fluctuations)
    val infiniteTransition = rememberInfiniteTransition(label = "MiningTelemetryTransition")
    
    // Orbital node rotation around the circular track
    val orbitalRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isMining || autoMiningActive) 4000 else 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalRotation"
    )

    // Breathing pulse for active glow
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    // Real-time micro-fluctuations simulating genuine proof-of-work cryptographic hashing
    var hashJitter by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isMining, autoMiningActive) {
        if (isMining || autoMiningActive) {
            while (true) {
                delay(850)
                // Small ±2.5% variation
                hashJitter = ((Math.random().toFloat() - 0.5f) * 0.05f)
            }
        } else {
            hashJitter = 0f
        }
    }

    val effectiveHashRate = if (isMining || autoMiningActive) {
        (baseHashRate * multiplier * (1f + hashJitter)).coerceAtLeast(0.1)
    } else {
        0.0
    }

    // Hash rate progression ratio (percentage of peak efficiency, ramping up as mining proceeds)
    val targetHashProgression = when {
        isMining -> (0.85f + (progress * 0.15f) + hashJitter).coerceIn(0.1f, 1.0f)
        autoMiningActive -> (0.92f + hashJitter).coerceIn(0.1f, 1.0f)
        else -> 0f
    }

    val animatedHashProgression by animateFloatAsState(
        targetValue = targetHashProgression,
        animationSpec = tween(durationMillis = 800),
        label = "HashProgressionAnimation"
    )

    // Color definitions based on state
    val primaryAccent = when {
        sessionCompleted -> MonetaEmeraldGreen
        autoMiningActive -> MonetaElectricBlue
        multiplier > 1.0 -> MonetaGoldenYellow
        isMining -> MonetaEmeraldGreen
        else -> MonetaSilver
    }

    val secondaryAccent = when {
        sessionCompleted -> MonetaElectricBlue
        autoMiningActive -> MonetaRoyalPurple
        multiplier > 1.0 -> MonetaOrange
        isMining -> MonetaElectricBlue
        else -> MonetaGraphiteGray
    }

    val trackColor = MonetaGraphiteGray.copy(alpha = 0.45f)

    Box(
        modifier = modifier
            .size(size)
            .testTag("circular_mining_indicator"),
        contentAlignment = Alignment.Center
    ) {
        // Multi-layered Canvas rendering circular rings, hash progression ticks, and orbital nodes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val innerStrokeWidth = 4.dp.toPx()
            val canvasRadius = (size.toPx() - strokeWidth) / 2f
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)

            // 1. Ambient Background Pulse Glow (when active)
            if (isMining || autoMiningActive) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryAccent.copy(alpha = 0.22f * pulseGlow),
                            primaryAccent.copy(alpha = 0.06f * pulseGlow),
                            Color.Transparent
                        ),
                        center = center,
                        radius = canvasRadius * 1.15f
                    ),
                    radius = canvasRadius * 1.15f,
                    center = center
                )
            }

            // 2. Background Track (Outer Circle)
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - canvasRadius, center.y - canvasRadius),
                size = Size(canvasRadius * 2, canvasRadius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Ticks / Hash Rate Telemetry Segments around Inner Periphery (48 segments)
            val numTicks = 48
            val tickRadiusInner = canvasRadius - strokeWidth * 0.9f
            val tickRadiusOuter = canvasRadius - strokeWidth * 0.35f
            for (i in 0 until numTicks) {
                val angleDeg = (i * (360f / numTicks)) - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val tickProgression = i.toFloat() / numTicks.toFloat()
                
                // Color tick based on whether active hash rate progression reached this segment
                val isReached = (isMining || autoMiningActive) && tickProgression <= animatedHashProgression
                val tickColor = when {
                    isReached -> primaryAccent.copy(alpha = 0.7f + (0.3f * pulseGlow))
                    (isMining || autoMiningActive) -> MonetaGraphiteGray.copy(alpha = 0.35f)
                    else -> MonetaGraphiteGray.copy(alpha = 0.2f)
                }

                val startOffset = Offset(
                    x = (center.x + tickRadiusInner * cos(angleRad)).toFloat(),
                    y = (center.y + tickRadiusInner * sin(angleRad)).toFloat()
                )
                val endOffset = Offset(
                    x = (center.x + tickRadiusOuter * cos(angleRad)).toFloat(),
                    y = (center.y + tickRadiusOuter * sin(angleRad)).toFloat()
                )

                drawLine(
                    color = tickColor,
                    start = startOffset,
                    end = endOffset,
                    strokeWidth = if (isReached) 2.2.dp.toPx() else 1.2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 4. Active Progress Arc (From Top -90 degrees)
            if (animatedProgress > 0f) {
                val sweepAngle = (animatedProgress * 360f).coerceIn(1f, 360f)
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(primaryAccent, secondaryAccent, primaryAccent),
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - canvasRadius, center.y - canvasRadius),
                    size = Size(canvasRadius * 2, canvasRadius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Leading glowing head of the progress arc
                val leadAngleRad = Math.toRadians((sweepAngle - 90.0))
                val leadX = (center.x + canvasRadius * cos(leadAngleRad)).toFloat()
                val leadY = (center.y + canvasRadius * sin(leadAngleRad)).toFloat()
                
                drawCircle(
                    color = Color.White,
                    radius = strokeWidth * 0.45f,
                    center = Offset(leadX, leadY)
                )
                drawCircle(
                    color = primaryAccent.copy(alpha = 0.6f),
                    radius = strokeWidth * 0.85f,
                    center = Offset(leadX, leadY)
                )
            }

            // 5. Inner Hash Rate Progression Ring
            val innerRingRadius = canvasRadius - 26.dp.toPx()
            drawArc(
                color = trackColor.copy(alpha = 0.25f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - innerRingRadius, center.y - innerRingRadius),
                size = Size(innerRingRadius * 2, innerRingRadius * 2),
                style = Stroke(width = innerStrokeWidth, cap = StrokeCap.Round)
            )

            if (animatedHashProgression > 0f) {
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(secondaryAccent, primaryAccent)
                    ),
                    startAngle = 135f,
                    sweepAngle = (animatedHashProgression * 270f).coerceIn(2f, 270f),
                    useCenter = false,
                    topLeft = Offset(center.x - innerRingRadius, center.y - innerRingRadius),
                    size = Size(innerRingRadius * 2, innerRingRadius * 2),
                    style = Stroke(width = innerStrokeWidth, cap = StrokeCap.Round)
                )
            }

            // 6. Orbital telemetry node (revolving satellite particle)
            if (isMining || autoMiningActive) {
                val orbitAngleRad = Math.toRadians(orbitalRotation.toDouble())
                val orbitRadius = canvasRadius + 8.dp.toPx()
                val nodeX = (center.x + orbitRadius * cos(orbitAngleRad)).toFloat()
                val nodeY = (center.y + orbitRadius * sin(orbitAngleRad)).toFloat()

                // Particle glow
                drawCircle(
                    color = primaryAccent.copy(alpha = 0.4f * pulseGlow),
                    radius = 7.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(nodeX, nodeY)
                )
            }
        }

        // Inner Core Content (Typography, Live Status, Telemetry Readout)
        Column(
            modifier = Modifier
                .fillMaxSize(0.72f)
                .clip(CircleShape)
                .background(MonetaCharcoalBlack.copy(alpha = 0.88f))
                .clickable(enabled = (!isMining && !sessionCompleted && !autoMiningActive && onStartMiningClick != null)) {
                    onStartMiningClick?.invoke()
                }
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // A. Status Pill Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    sessionCompleted -> MonetaEmeraldGreen.copy(alpha = 0.2f)
                    autoMiningActive -> MonetaElectricBlue.copy(alpha = 0.2f)
                    isMining -> MonetaEmeraldGreen.copy(alpha = 0.18f)
                    else -> MonetaGraphiteGray
                },
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when {
                        sessionCompleted -> MonetaEmeraldGreen
                        autoMiningActive -> MonetaElectricBlue
                        isMining -> MonetaEmeraldGreen
                        else -> MonetaSilver.copy(alpha = 0.3f)
                    }
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pulsing Status Dot
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (isMining || autoMiningActive || sessionCompleted) primaryAccent else MonetaSilver
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = when {
                            sessionCompleted -> "COMPLETED"
                            autoMiningActive -> "AUTO-MINE"
                            isMining -> "MINING ON"
                            else -> "STANDBY"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = when {
                            sessionCompleted -> MonetaEmeraldGreen
                            autoMiningActive -> MonetaElectricBlue
                            isMining -> MonetaEmeraldGreen
                            else -> MonetaSilver
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // B. Live Hash Rate Progression Readout
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isMining || autoMiningActive) {
                        String.format(Locale.US, "%.1f", effectiveHashRate)
                    } else {
                        "0.0"
                    },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = (-0.5).sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = if (isMining || autoMiningActive) MonetaWhite else MonetaSilver
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "MH/s",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = primaryAccent,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Hash rate progression indicator label
            Text(
                text = if (isMining || autoMiningActive) {
                    val eff = (animatedHashProgression * 100).toInt()
                    "HASH ENGINE: $eff%"
                } else {
                    "HASH ENGINE: IDLE"
                },
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                ),
                color = MonetaSilver
            )

            Spacer(modifier = Modifier.height(6.dp))

            // C. Session Time Countdown or Tap Action
            if (sessionCompleted) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Session Complete",
                    tint = MonetaEmeraldGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "CLAIM REWARD",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    color = MonetaEmeraldGreen
                )
            } else if (isMining) {
                val hours = remainingSeconds / 3600
                val minutes = (remainingSeconds % 3600) / 60
                val seconds = remainingSeconds % 60
                Text(
                    text = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp
                    ),
                    color = MonetaWhite
                )
                Text(
                    text = String.format(Locale.US, "+%.2f MNT", minedSoFar),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MonetaGoldenYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
            } else if (autoMiningActive) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "Auto",
                        tint = MonetaElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "CONTINUOUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MonetaElectricBlue,
                            fontSize = 11.sp
                        )
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start",
                        tint = MonetaEmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "START",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MonetaEmeraldGreen,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Accompanying Hash Rate progression telemetry strip displaying detailed hardware and cryptographic metrics.
 */
@Composable
fun HashRateTelemetryCard(
    isMining: Boolean,
    autoMiningActive: Boolean,
    hashRate: Double,
    activeBoosters: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MonetaGraphiteGray, MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(containerColor = MonetaCharcoalBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = "Telemetry",
                        tint = MonetaElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HASH RATE TELEMETRY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MonetaElectricBlue
                    )
                }

                Text(
                    text = if (isMining || autoMiningActive) "OPTIMAL ●" else "STANDBY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = if (isMining || autoMiningActive) MonetaEmeraldGreen else MonetaSilver
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Current Hash Output
                Column(modifier = Modifier.weight(1f)) {
                    Text("HASH OUTPUT", color = MonetaSilver, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isMining || autoMiningActive) "${String.format(Locale.US, "%.1f", hashRate)} MH/s" else "0.0 MH/s",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                        color = if (isMining || autoMiningActive) MonetaWhite else MonetaSilver
                    )
                }

                // Metric 2: Efficiency Rate
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EFFICIENCY", color = MonetaSilver, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                    Spacer(modifier = Modifier.height(2.dp))
                    val effText = if (isMining || autoMiningActive) "99.4%" else "--"
                    Text(
                        text = effText,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isMining || autoMiningActive) MonetaEmeraldGreen else MonetaSilver
                    )
                }

                // Metric 3: Active Multiplier
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("BOOST RATE", color = MonetaSilver, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                    Spacer(modifier = Modifier.height(2.dp))
                    val boostLabel = when {
                        activeBoosters.contains("PRO BOOST") -> "5.0× PRO"
                        activeBoosters.contains("2X MULTIPLIER") -> "2.0× BOOST"
                        autoMiningActive -> "2.0× AUTO"
                        else -> "1.0× BASE"
                    }
                    Text(
                        text = boostLabel,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (activeBoosters.isNotEmpty() || autoMiningActive) MonetaGoldenYellow else MonetaSilver
                    )
                }
            }
        }
    }
}
