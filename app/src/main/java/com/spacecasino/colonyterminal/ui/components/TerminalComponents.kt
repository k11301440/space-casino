package com.spacecasino.colonyterminal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import java.text.DecimalFormat

val creditFormatter = DecimalFormat("#,###")

fun formatCredits(amount: Long): String = "${creditFormatter.format(amount)} CR"
fun formatNumber(amount: Long): String = creditFormatter.format(amount)

fun formatDayToDate(day: Long): String {
    // 宇宙歴 2142年 1月 1日 起点
    val totalDays = day - 1
    val year = 2142 + (totalDays / 360)
    val rem = totalDays % 360
    val month = (rem / 30) + 1
    val d = (rem % 30) + 1
    return "宇宙歴${year}年 %02d月%02d日".format(month, d)
}

@Composable
fun TerminalPanel(
    modifier: Modifier = Modifier,
    title: String? = null,
    borderColor: Color? = null,
    backgroundColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalTerminalColors.current
    val actualBorder = borderColor ?: colors.border
    val actualBg = backgroundColor ?: colors.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(actualBg)
            .border(1.dp, actualBorder, RoundedCornerShape(4.dp))
            .padding(12.dp)
    ) {
        if (!title.isNullOrBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "▶ $title",
                    color = colors.secondary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
        content()
    }
}

@Composable
fun TerminalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false,
    isDanger: Boolean = false
) {
    val colors = LocalTerminalColors.current

    val bg = when {
        !enabled -> colors.surfaceVariant.copy(alpha = 0.5f)
        isDanger -> colors.negative.copy(alpha = 0.15f)
        isPrimary -> colors.primary.copy(alpha = 0.2f)
        else -> colors.surfaceVariant
    }

    val border = when {
        !enabled -> colors.border.copy(alpha = 0.4f)
        isDanger -> colors.negative
        isPrimary -> colors.primary
        else -> colors.border
    }

    val textColor = when {
        !enabled -> colors.textSecondary.copy(alpha = 0.5f)
        isDanger -> colors.negative
        isPrimary -> colors.primary
        else -> colors.textPrimary
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(3.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

// ミニチャートコンポーネント (直近の価格推移を描画)
@Composable
fun MiniSparkline(
    history: List<Number>,
    modifier: Modifier = Modifier,
    lineColor: Color? = null
) {
    val colors = LocalTerminalColors.current
    val color = lineColor ?: colors.primary

    if (history.size < 2) {
        Box(modifier = modifier)
        return
    }

    val doubles = history.map { it.toDouble() }
    val min = doubles.minOrNull() ?: 0.0
    val max = doubles.maxOrNull() ?: 1.0
    val range = if (max == min) 1.0 else (max - min)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stepX = w / (doubles.size - 1)

        val path = Path()
        doubles.forEachIndexed { i, value ->
            val x = i * stepX
            val norm = (value - min) / range
            val y = (h - (norm * (h - 8f)) - 4f).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // 終点に点滅ドット風マークを描画
        val lastX = (doubles.size - 1) * stepX
        val lastNorm = (doubles.last() - min) / range
        val lastY = (h - (lastNorm * (h - 8f)) - 4f).toFloat()
        drawCircle(
            color = color,
            radius = 3.dp.toPx(),
            center = Offset(lastX, lastY)
        )
    }
}
