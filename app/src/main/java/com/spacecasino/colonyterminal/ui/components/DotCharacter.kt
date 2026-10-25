package com.spacecasino.colonyterminal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.theme.LocalTerminalColors

/**
 * 宇宙コロニーの住人/マスコット ドット絵キャラクター
 * 16x16 のドットマトリクスを Compose Canvas で描画し、足踏みや呼吸アニメーションを行う
 */
@Composable
fun DotCharacter(
    modifier: Modifier = Modifier,
    statusText: String = "STATUS: STANDBY",
    scale: Float = 4f
) {
    val colors = LocalTerminalColors.current

    // アニメーション用タイマー (0, 1, 2, 3 の4フレームループ)
    val infiniteTransition = rememberInfiniteTransition(label = "dot_char_anim")
    val frame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "frame_counter"
    )

    // 16x16 ドットパターン定義
    // 0: 空白, 1: 輪郭/スーツ主線, 2: バイザー(発光), 3: スーツ本体(明), 4: アクセント(胸部ランプ/推進器)
    // フレーム0: 直立・待機
    // フレーム1: 呼吸で体が1ドット上へ
    // フレーム2: 足踏み(左足上げ)
    // フレーム3: 足踏み(右足上げ)

    val patternFrame0 = listOf(
        "....111111......",
        "...13333331.....",
        "..1322222231....",
        "..1322222231....",
        "..1333333331....",
        "...13344331.....",
        "....111111......",
        "..1133333311....",
        ".131334433131...",
        ".131333333131...",
        "..1133333311....",
        "...13311331.....",
        "...131..131.....",
        "...131..131.....",
        "..1111..1111....",
        "................"
    )

    val patternFrame1 = listOf(
        "...111111.......",
        "..13333331......",
        ".1322222231.....",
        ".1322222231.....",
        ".1333333331.....",
        "..13344331......",
        "...111111.......",
        ".1133333311.....",
        "131334433131....",
        "131333333131....",
        ".1133333311.....",
        "..13311331......",
        "..131..131......",
        "..131..131......",
        ".1111..1111.....",
        "................"
    )

    val patternFrame2 = listOf(
        "....111111......",
        "...13333331.....",
        "..1322222231....",
        "..1322222231....",
        "..1333333331....",
        "...13344331.....",
        "....111111......",
        "..1133333311....",
        ".131334433131...",
        ".131333333131...",
        "..1133333311....",
        "...13311331.....",
        "...111..131.....",
        "........131.....",
        ".......1111.....",
        "................"
    )

    val patternFrame3 = listOf(
        "....111111......",
        "...13333331.....",
        "..1322222231....",
        "..1322222231....",
        "..1333333331....",
        "...13344331.....",
        "....111111......",
        "..1133333311....",
        ".131334433131...",
        ".131333333131...",
        "..1133333311....",
        "...13311331.....",
        "...131..111.....",
        "...131..........",
        "..1111..........",
        "................"
    )

    val currentPattern = when (frame % 4) {
        0 -> patternFrame0
        1 -> patternFrame1
        2 -> patternFrame2
        else -> patternFrame3
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ドットキャンバス
        Canvas(
            modifier = Modifier.size((16 * scale).dp, (16 * scale).dp)
        ) {
            val pixelSize = size.width / 16f

            for (y in 0 until 16) {
                val row = currentPattern[y]
                for (x in 0 until 16) {
                    val char = row[x]
                    val pixelColor = when (char) {
                        '1' -> colors.textPrimary // 輪郭
                        '2' -> colors.secondary   // バイザー発光
                        '3' -> colors.primary     // スーツ本体
                        '4' -> colors.positive    // 胸部ランプ
                        else -> Color.Transparent
                    }
                    if (pixelColor != Color.Transparent) {
                        drawRect(
                            color = pixelColor,
                            topLeft = Offset(x * pixelSize, y * pixelSize),
                            size = Size(pixelSize, pixelSize)
                        )
                    }
                }
            }
        }

        // キャラクター横のセリフ/ステータスバナー
        Column {
            Text(
                text = "PILOT: ARCADIA RESIDENT",
                color = colors.primary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = statusText,
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
