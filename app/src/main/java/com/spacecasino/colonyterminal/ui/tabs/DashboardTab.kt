package com.spacecasino.colonyterminal.ui.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.model.FacilityType
import com.spacecasino.colonyterminal.model.GameState
import com.spacecasino.colonyterminal.model.MarketRegime
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.components.*

@Composable
fun DashboardTab(
    state: GameState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    val totalWorth = state.totalNetWorth
    val targetClear = 100000000L
    val progress = (totalWorth.toFloat() / targetClear.toFloat()).coerceIn(0f, 1f)

    // 施設日収合計
    var totalDailyIncome = 0L
    state.facilities.forEach {
        if (it.isOwned) totalDailyIncome += it.currentDailyIncome
    }

    // 銀行預金
    val bankAmount = state.bank.deposited

    // 投信評価額
    val fundValue = state.mutualFunds.sumOf { (it.ownedUnits * it.nav).toLong() }

    // 株式評価額
    val stockValue = state.stocks.sumOf { it.ownedShares * it.currentPrice }

    // 施設累計投資額
    val facilityValue = state.facilities.filter { it.isOwned }.sumOf { it.baseCost * it.level }

    val hasGravity = state.facilities.find { it.type == FacilityType.GRAVITY && it.isOwned } != null

    val charStatus = when {
        state.colonyPurchased -> "STATUS: COLONY RULER"
        state.credits < 10000L -> "STATUS: BASIC INCOME ACTIVE"
        totalWorth >= targetClear -> "STATUS: BUYOUT READY"
        else -> "STATUS: OPERATING TERMINAL"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ドット絵キャラクターバナー
        TerminalPanel {
            com.spacecasino.colonyterminal.ui.components.DotCharacter(
                modifier = Modifier.fillMaxWidth(),
                statusText = charStatus,
                scale = 3.5f
            )
        }

        // 景気レジーム状況
        val regimePanelColor = when (state.marketRegime) {
            MarketRegime.BOOM -> colors.positive
            MarketRegime.STABLE -> colors.primary
            MarketRegime.SLOWDOWN -> colors.textSecondary
            MarketRegime.CRISIS -> colors.hazard
        }
        TerminalPanel(title = "市場レジーム", borderColor = regimePanelColor) {
            val regimeColor = when (state.marketRegime) {
                MarketRegime.BOOM -> colors.positive
                MarketRegime.STABLE -> colors.primary
                MarketRegime.SLOWDOWN -> colors.textSecondary
                MarketRegime.CRISIS -> colors.hazard
            }
            Text(
                text = "現在: ${state.marketRegime.displayName}",
                color = regimeColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = state.marketRegime.description,
                color = colors.textPrimary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "レジーム転換まで: 約${state.regimeDaysLeft}日 / 施設収入倍率: ${
                    when (state.marketRegime) {
                        MarketRegime.BOOM -> "x1.15"
                        MarketRegime.STABLE -> "x1.00"
                        MarketRegime.SLOWDOWN -> "x0.90"
                        MarketRegime.CRISIS -> "x0.70"
                    }
                }",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // 未対応の判断イベント
        state.pendingEvent?.let { event ->
            TerminalPanel(title = "イベント: 要判断", borderColor = colors.hazard) {
                Text(
                    text = event.title,
                    color = colors.hazard,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = event.description,
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                event.options.forEachIndexed { index, option ->
                    Spacer(modifier = Modifier.height(4.dp))
                    TerminalButton(
                        text = "${index + 1}. ${option.label}",
                        onClick = { viewModel.resolveEvent(index) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = option.description,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // 市場ニュースフィード
        TerminalPanel(title = "市場ニュースフィード") {
            if (state.news.isEmpty()) {
                Text(
                    text = "ニュースはありません。",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                state.news.take(5).forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        val kindColor = when (item.kind) {
                            "レジーム" -> colors.primary
                            "情報" -> colors.positive
                            "イベント" -> colors.hazard
                            else -> colors.secondary
                        }
                        Text(
                            text = "Day ${item.day} [${item.kind}] ${item.headline}",
                            color = kindColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = item.body,
                            color = colors.textPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // コロニー買収進捗
        TerminalPanel(title = "コロニー『アルカディア』買収計画") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "買収資金目標: 100,000,000 CR",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = colors.primary,
                trackColor = colors.surfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.colonyPurchased) "コロニー統帥権: 【掌握済】" else "買収権利: 【資金1億CRで即時執行可】",
                    color = if (state.colonyPurchased) colors.positive else colors.textPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                if (totalWorth >= targetClear && !state.colonyPurchased) {
                    val canAffordCash = state.credits >= targetClear
                    TerminalButton(
                        text = if (canAffordCash) "コロニー買収執行！" else "買収(現金不足)",
                        onClick = { viewModel.purchaseColony() },
                        enabled = canAffordCash,
                        isPrimary = canAffordCash
                    )
                } else if (state.colonyPurchased) {
                    Text(
                        text = "★買収完了・統帥者就任★",
                        color = colors.positive,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            if (totalWorth >= targetClear && !state.colonyPurchased && state.credits < targetClear) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "※買収執行には現金1億CRが必要です。預金や株式等を換金してください。",
                    color = colors.hazard,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // 時間進行操作パネル
        TerminalPanel(title = "コロニー時間進行") {
            Text(
                text = "現在のコロニー時間: ${formatDayToDate(state.day)} (Day ${state.day})",
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TerminalButton(
                    text = "▶ 1日進める",
                    onClick = { viewModel.advanceTime(1) },
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
                TerminalButton(
                    text = "⏩ 30日進める",
                    onClick = { viewModel.advanceTime(30) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "※日数を進めると、パッシブ収入・預金利息・投信・株価・為替が一括更新されます。ギャンブル中は時間は進みません。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // 資産内訳一覧
        TerminalPanel(title = "資産ポートフォリオ内訳") {
            AssetRow("手持ち現金", formatCredits(state.credits), colors.primary)
            AssetRow("銀行預金", formatCredits(bankAmount), colors.textPrimary)
            AssetRow("投資信託評価額", formatCredits(fundValue), colors.textPrimary)
            AssetRow("株式市場評価額", formatCredits(stockValue), colors.textPrimary)
            AssetRow("施設資産価値", formatCredits(facilityValue), colors.textPrimary)
            AssetRow("施設日収合計", "+${formatCredits(totalDailyIncome)} /日", colors.positive)
        }

        // 直近のイベント・ログ
        TerminalPanel(title = "直近の通信ログ") {
            if (state.logs.isEmpty()) {
                Text(
                    text = "ログはありません。",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                state.logs.take(4).forEach { log ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Day ${log.day} [${log.category}]",
                                color = colors.secondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            if (log.amountChange != 0L) {
                                val changeText = if (log.amountChange > 0) "+${formatCredits(log.amountChange)}" else formatCredits(log.amountChange)
                                Text(
                                    text = changeText,
                                    color = if (log.amountChange > 0) colors.positive else colors.negative,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
}

@Composable
private fun AssetRow(label: String, value: String, valueColor: Color) {
    val colors = LocalTerminalColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
