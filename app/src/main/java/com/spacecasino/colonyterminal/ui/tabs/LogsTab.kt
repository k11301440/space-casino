package com.spacecasino.colonyterminal.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.model.GameState
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.components.TerminalPanel
import com.spacecasino.colonyterminal.ui.components.formatCredits

enum class LogSubTab(val title: String) {
    STORY("ストーリー履歴"),
    OPERATIONS("全取引ログ")
}

@Composable
fun LogsTab(
    state: GameState,
    modifier: Modifier = Modifier
) {
    val colors = LocalTerminalColors.current
    var subTab by remember { mutableStateOf(LogSubTab.STORY) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LogSubTab.values().forEach { tab ->
                val isSel = subTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSel) colors.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { subTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSel) colors.primary else colors.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            when (subTab) {
                LogSubTab.STORY -> StoryHistoryView(state)
                LogSubTab.OPERATIONS -> OperationsLogView(state)
            }
        }
    }
}

@Composable
private fun StoryHistoryView(state: GameState) {
    val colors = LocalTerminalColors.current
    val unlocked = state.milestones.filter { it.isUnlocked }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(unlocked) { m ->
            TerminalPanel(title = "★ ${m.title} (Day ${m.unlockedDay})") {
                Text(
                    text = m.description,
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun OperationsLogView(state: GameState) {
    val colors = LocalTerminalColors.current

    if (state.logs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "通信ログはありません。",
                color = colors.textSecondary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.logs) { log ->
                TerminalPanel {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Day ${log.day} [${log.category}]",
                            color = colors.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (log.amountChange != 0L) {
                            val sign = if (log.amountChange > 0) "+" else ""
                            Text(
                                text = "${sign}${formatCredits(log.amountChange)}",
                                color = if (log.amountChange > 0) colors.positive else colors.negative,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = log.message,
                        color = colors.textPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
