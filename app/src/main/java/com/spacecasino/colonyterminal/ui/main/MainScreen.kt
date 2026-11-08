package com.spacecasino.colonyterminal.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spacecasino.colonyterminal.model.MarketRegime
import com.spacecasino.colonyterminal.model.ThemeMode
import com.spacecasino.colonyterminal.theme.ColonyAssetTerminalTheme
import com.spacecasino.colonyterminal.theme.CrtScanlineOverlay
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.GameDialogState
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.MainTab
import com.spacecasino.colonyterminal.ui.components.*
import com.spacecasino.colonyterminal.ui.tabs.*

@Composable
fun MainScreen(
    viewModel: GameViewModel = viewModel()
) {
    val state by viewModel.gameState.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val dialogState by viewModel.currentDialog.collectAsStateWithLifecycle()

    ColonyAssetTerminalTheme(themeMode = state.theme) {
        val colors = LocalTerminalColors.current

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // 1. 常時表示ステータスヘッダー
                TerminalGlobalHeader(
                    day = state.day,
                    cash = state.credits,
                    totalNetWorth = state.totalNetWorth,
                    marketRegime = state.marketRegime,
                    regimeDaysLeft = state.regimeDaysLeft,
                    hasPendingEvent = state.pendingEvent != null,
                    onAdvanceOneDay = { viewModel.advanceTime(1) },
                    onAdvanceMonth = { viewModel.advanceTime(30) }
                )

                // 2. メインコンテンツ
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (currentTab) {
                        MainTab.DASHBOARD -> DashboardTab(state, viewModel)
                        MainTab.INVEST -> InvestmentsTab(state, viewModel)
                        MainTab.FACILITIES -> FacilitiesTab(state, viewModel)
                        MainTab.CASINO -> GamblingTab(state, viewModel)
                        MainTab.LOGS -> LogsTab(state)
                        MainTab.SETTINGS -> SettingsTab(state, viewModel)
                    }
                }

                // 3. ボトムナビゲーションバー
                TerminalBottomBar(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.setTab(it) }
                )
            }

            // CRT スキャンライン効果 (CRTテーマ時のみ表示)
            if (state.theme == ThemeMode.CRT_GREEN || state.theme == ThemeMode.CRT_AMBER) {
                CrtScanlineOverlay(
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.06f
                )
            }

            // 4. 中央ポップアップ (ストーリー、オフライン報告、クリア等)
            dialogState?.let { dialog ->
                CenterPopupDialog(
                    dialog = dialog,
                    onDismiss = { viewModel.dismissDialog() },
                    onEventOption = { index -> viewModel.resolveEvent(index) }
                )
            }
        }
    }
}

// 常時固定のヘッダー
@Composable
private fun TerminalGlobalHeader(
    day: Long,
    cash: Long,
    totalNetWorth: Long,
    marketRegime: MarketRegime,
    regimeDaysLeft: Int,
    hasPendingEvent: Boolean,
    onAdvanceOneDay: () -> Unit,
    onAdvanceMonth: () -> Unit
) {
    val colors = LocalTerminalColors.current

    val regimeColor = when (marketRegime) {
        MarketRegime.BOOM -> colors.positive
        MarketRegime.STABLE -> colors.primary
        MarketRegime.SLOWDOWN -> colors.textSecondary
        MarketRegime.CRISIS -> colors.hazard
    }
    val headerBorderColor = if (hasPendingEvent || marketRegime == MarketRegime.CRISIS) colors.hazard else colors.border

    Surface(
        color = colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, headerBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CAT-OS v2.4 // ${formatDayToDate(day)}",
                    color = colors.secondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TerminalButton(
                        text = "+1日",
                        onClick = onAdvanceOneDay,
                        isPrimary = true
                    )
                    TerminalButton(
                        text = "+30日",
                        onClick = onAdvanceMonth
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "REGIME:",
                            color = colors.textSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${marketRegime.displayName} (${regimeDaysLeft}日)",
                            color = regimeColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (hasPendingEvent) {
                            Text(
                                text = "●EVENT",
                                color = colors.hazard,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "手持ち現金",
                        color = colors.textSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = formatCredits(cash),
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "純資産総額",
                        color = colors.textSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = formatCredits(totalNetWorth),
                        color = colors.positive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// ボトムナビゲーションバー
@Composable
private fun TerminalBottomBar(
    currentTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    val colors = LocalTerminalColors.current

    Surface(
        color = colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MainTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isSelected) colors.primary.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 6.dp)
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) colors.primary else colors.textSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// 画面中央ポップアップダイアログ
@Composable
private fun CenterPopupDialog(
    dialog: GameDialogState,
    onDismiss: () -> Unit,
    onEventOption: (Int) -> Unit = {}
) {
    val colors = LocalTerminalColors.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(colors.surface)
                .border(2.dp, colors.primary, RoundedCornerShape(6.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (dialog) {
                    is GameDialogState.StoryEvent -> {
                        Text(
                            text = "【STORY MILESTONE】",
                            color = colors.hazard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.milestone.title,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.milestone.description,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                    is GameDialogState.OfflineReport -> {
                        Text(
                            text = "【SYSTEM NOTICE】",
                            color = colors.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.message,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                    is GameDialogState.GameClear -> {
                        Text(
                            text = "★ GAME CLEAR ★",
                            color = colors.hazard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "コロニー『アルカディア』買収成功！",
                            color = colors.positive,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "宇宙歴 Day ${dialog.day}、総資産 ${formatCredits(dialog.netWorth)} にて、コロニー全域の統帥権を完全掌握しました！\nスラム街の粗大ゴミ置き場から始まったあなたの資産運用は、今や一つの天体都市を統治するに至りました。",
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                    is GameDialogState.EventChoice -> {
                        Text(
                            text = "【EVENT: 要判断】",
                            color = colors.hazard,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.event.title,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.event.description,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                        dialog.event.options.forEachIndexed { index, option ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, colors.border, RoundedCornerShape(4.dp))
                                    .clickable { onEventOption(index) }
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "▶ ${option.label}",
                                    color = colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = option.description,
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        return@Dialog
                    }
                    is GameDialogState.IntelReport -> {
                        Text(
                            text = "【INTEL REPORT】",
                            color = colors.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.message,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                    is GameDialogState.Notice -> {
                        Text(
                            text = dialog.title,
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = dialog.message,
                            color = colors.textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                TerminalButton(
                    text = "了解 (Close)",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
        }
    }
}
