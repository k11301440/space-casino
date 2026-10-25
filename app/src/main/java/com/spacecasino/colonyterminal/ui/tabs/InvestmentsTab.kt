package com.spacecasino.colonyterminal.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spacecasino.colonyterminal.model.*
import com.spacecasino.colonyterminal.theme.LocalTerminalColors
import com.spacecasino.colonyterminal.ui.GameViewModel
import com.spacecasino.colonyterminal.ui.components.*

enum class InvestSubTab(val title: String) {
    BANK("銀行預金"),
    FUNDS("投資信託"),
    STOCKS("株式市場"),
    FX("星間為替(FX)")
}

@Composable
fun InvestmentsTab(
    state: GameState,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val colors = LocalTerminalColors.current
    var subTab by remember { mutableStateOf(InvestSubTab.BANK) }

    Column(modifier = modifier.fillMaxSize()) {
        // サブタブバー
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            InvestSubTab.values().forEach { tab ->
                val isSelected = subTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (isSelected) colors.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { subTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) colors.primary else colors.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // サブ画面コンテンツ
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            when (subTab) {
                InvestSubTab.BANK -> BankSection(state, viewModel)
                InvestSubTab.FUNDS -> FundsSection(state, viewModel)
                InvestSubTab.STOCKS -> StocksSection(state, viewModel)
                InvestSubTab.FX -> FxSection(state, viewModel)
            }
        }
    }
}

// 1. 銀行預金
@Composable
private fun BankSection(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel(title = "コロニー第一中央銀行") {
            Text(
                text = "預金残高: ${formatCredits(state.bank.deposited)}",
                color = colors.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "低リスク・確実な成長。日歩1.0%（24時間放置で約1.0%利息）。",
                color = colors.textSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "★アプリ終了中（オフライン時間）も利息が自動積算されます。",
                color = colors.secondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        TerminalPanel(title = "預け入れ (手持ち現金 → 預金)") {
            Text(
                text = "手持ち現金: ${formatCredits(state.credits)}",
                color = colors.textPrimary,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TerminalButton(
                    text = "+1,000 CR",
                    onClick = { viewModel.depositBank(1000L) },
                    enabled = state.credits >= 1000L,
                    modifier = Modifier.weight(1f)
                )
                TerminalButton(
                    text = "+10,000 CR",
                    onClick = { viewModel.depositBank(10000L) },
                    enabled = state.credits >= 10000L,
                    modifier = Modifier.weight(1f)
                )
                TerminalButton(
                    text = "全額預入",
                    onClick = { viewModel.depositBank(state.credits) },
                    enabled = state.credits > 0L,
                    modifier = Modifier.weight(1f),
                    isPrimary = true
                )
            }
        }

        TerminalPanel(title = "引き出し (預金 → 手持ち現金)") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TerminalButton(
                    text = "-1,000 CR",
                    onClick = { viewModel.withdrawBank(1000L) },
                    enabled = state.bank.deposited >= 1000L,
                    modifier = Modifier.weight(1f)
                )
                TerminalButton(
                    text = "-10,000 CR",
                    onClick = { viewModel.withdrawBank(10000L) },
                    enabled = state.bank.deposited >= 10000L,
                    modifier = Modifier.weight(1f)
                )
                TerminalButton(
                    text = "全額引出",
                    onClick = { viewModel.withdrawBank(state.bank.deposited) },
                    enabled = state.bank.deposited > 0L,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// 2. 投資信託
@Composable
private fun FundsSection(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel {
            Text(
                text = "投資信託：中リスク・中リターン。",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "アプリ内時間進行およびオフライン時間経過の両方で基準価額が成長します。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        state.mutualFunds.forEach { fund ->
            val currentValue = (fund.ownedUnits * fund.nav).toLong()
            val profit = currentValue - fund.investedCost
            val profitRate = if (fund.investedCost > 0) (profit.toDouble() / fund.investedCost * 100) else 0.0

            TerminalPanel(title = fund.name) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "基準価額: ${formatCredits(fund.nav.toLong())}",
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "想定年利: ${(fund.expectedAnnualReturn * 100).toInt()}% (${fund.category})",
                            color = colors.secondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    MiniSparkline(
                        history = fund.history,
                        modifier = Modifier
                            .width(100.dp)
                            .height(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fund.description,
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (fund.ownedUnits > 0.0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "保有口数: ${String.format("%.2f", fund.ownedUnits)}口 (評価額: ${formatCredits(currentValue)})",
                            color = colors.textPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val profitColor = if (profit >= 0) colors.positive else colors.negative
                        val sign = if (profit >= 0) "+" else ""
                        Text(
                            text = "損益: ${sign}${formatCredits(profit)} (${sign}${String.format("%.1f", profitRate)}%)",
                            color = profitColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TerminalButton(
                        text = "+1,000 CR買付",
                        onClick = { viewModel.buyMutualFund(fund.id, 1000L) },
                        enabled = state.credits >= 1000L,
                        modifier = Modifier.weight(1f)
                    )
                    TerminalButton(
                        text = "+10,000 CR買付",
                        onClick = { viewModel.buyMutualFund(fund.id, 10000L) },
                        enabled = state.credits >= 10000L,
                        modifier = Modifier.weight(1f)
                    )
                    if (fund.ownedUnits > 0.0) {
                        TerminalButton(
                            text = "全口解約",
                            onClick = { viewModel.sellMutualFund(fund.id, fund.ownedUnits) },
                            modifier = Modifier.weight(1f),
                            isDanger = true
                        )
                    }
                }
            }
        }
    }
}

// 3. 株式市場
@Composable
private fun StocksSection(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel {
            Text(
                text = "コロニー株式市場：個別銘柄投資。",
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "ボラティリティあり。時間進行で各社の業績やニュースに応じて株価が変動します。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        state.stocks.forEach { stock ->
            val priceDiff = stock.currentPrice - stock.previousPrice
            val diffPercent = if (stock.previousPrice > 0) (priceDiff.toDouble() / stock.previousPrice * 100) else 0.0
            val diffColor = if (priceDiff >= 0) colors.positive else colors.negative
            val sign = if (priceDiff >= 0) "+" else ""

            val ownedVal = stock.ownedShares * stock.currentPrice
            val pnl = ownedVal - stock.totalCost

            TerminalPanel(title = "${stock.name} [${stock.symbol}]") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${formatCredits(stock.currentPrice)} /株",
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "前日比: ${sign}${formatCredits(priceDiff)} (${sign}${String.format("%.2f", diffPercent)}%)",
                            color = diffColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    MiniSparkline(
                        history = stock.history,
                        modifier = Modifier
                            .width(110.dp)
                            .height(36.dp),
                        lineColor = diffColor
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stock.sector} | ${stock.description}",
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )

                if (stock.ownedShares > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val avgCost = stock.totalCost.toDouble() / stock.ownedShares
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "保有: ${stock.ownedShares}株 (平均取得: ${formatNumber(avgCost.toLong())} CR)",
                            color = colors.textPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        val pnlSign = if (pnl >= 0) "+" else ""
                        Text(
                            text = "損益: ${pnlSign}${formatCredits(pnl)}",
                            color = if (pnl >= 0) colors.positive else colors.negative,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TerminalButton(
                        text = "+1株買付",
                        onClick = { viewModel.buyStock(stock.symbol, 1L) },
                        enabled = state.credits >= stock.currentPrice,
                        modifier = Modifier.weight(1f)
                    )
                    TerminalButton(
                        text = "+10株買付",
                        onClick = { viewModel.buyStock(stock.symbol, 10L) },
                        enabled = state.credits >= stock.currentPrice * 10,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (stock.ownedShares > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TerminalButton(
                            text = "1株売却",
                            onClick = { viewModel.sellStock(stock.symbol, 1L) },
                            modifier = Modifier.weight(1f)
                        )
                        TerminalButton(
                            text = "全株売却",
                            onClick = { viewModel.sellStock(stock.symbol, stock.ownedShares) },
                            modifier = Modifier.weight(1f),
                            isDanger = true
                        )
                    }
                }
            }
        }
    }
}

// 4. 星間為替 (FX)
@Composable
private fun FxSection(state: GameState, viewModel: GameViewModel) {
    val colors = LocalTerminalColors.current
    val scrollState = rememberScrollState()

    var selectedLeverage by remember { mutableStateOf(5) }
    var selectedLots by remember { mutableStateOf(1L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TerminalPanel {
            Text(
                text = "星間為替 (FX)：レバレッジ証拠金取引。",
                color = colors.hazard,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "ボラティリティ大。急騰急落します。証拠金維持率が50%を下回ると強制ロスカットされます。",
                color = colors.textSecondary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "設定レバ:",
                    color = colors.textPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
                listOf(1, 2, 5, 10, 25).forEach { lev ->
                    val isSel = selectedLeverage == lev
                    Box(
                        modifier = Modifier
                            .background(if (isSel) colors.primary else colors.surfaceVariant)
                            .clickable { selectedLeverage = lev }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${lev}倍",
                            color = if (isSel) colors.background else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 通貨ペア一覧＆注文
        state.fxPairs.forEach { pair ->
            val notional = pair.currentRate * selectedLots * 1000
            val reqMargin = (notional / selectedLeverage).toLong()

            TerminalPanel(title = "${pair.name} [${pair.symbol}]") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "レート: ${String.format("%.4f", pair.currentRate)}",
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "1ロット(1000通貨) 必要証拠金: ${formatCredits(reqMargin)}",
                            color = colors.secondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    MiniSparkline(
                        history = pair.history,
                        modifier = Modifier
                            .width(100.dp)
                            .height(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TerminalButton(
                        text = "LONG(買)注文",
                        onClick = { viewModel.openFxPosition(pair.symbol, FxDirection.LONG, selectedLots, selectedLeverage) },
                        enabled = state.credits >= reqMargin,
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                    TerminalButton(
                        text = "SHORT(売)注文",
                        onClick = { viewModel.openFxPosition(pair.symbol, FxDirection.SHORT, selectedLots, selectedLeverage) },
                        enabled = state.credits >= reqMargin,
                        modifier = Modifier.weight(1f),
                        isDanger = true
                    )
                }
            }
        }

        // オープンポジション一覧
        TerminalPanel(title = "保有ポジション (${state.fxPositions.size}件)") {
            if (state.fxPositions.isEmpty()) {
                Text(
                    text = "保有中の為替ポジションはありません。",
                    color = colors.textSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                state.fxPositions.forEach { pos ->
                    val pair = state.fxPairs.find { it.symbol == pos.pairSymbol }
                    if (pair != null) {
                        val priceDiff = if (pos.direction == FxDirection.LONG) {
                            (pair.currentRate - pos.entryRate)
                        } else {
                            (pos.entryRate - pair.currentRate)
                        }
                        val pnl = (priceDiff * pos.lots * 1000).toLong()
                        val equity = pos.marginUsed + pnl
                        val marginRatio = (equity.toDouble() / pos.marginUsed * 100).toInt()
                        val isDanger = marginRatio < 70

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
                                    text = "${pos.pairSymbol} [${if (pos.direction == FxDirection.LONG) "LONG" else "SHORT"}] x${pos.leverage}",
                                    color = colors.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "維持率: ${marginRatio}%",
                                    color = if (isDanger) colors.negative else colors.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "建て値: ${String.format("%.4f", pos.entryRate)} | 証拠金: ${formatCredits(pos.marginUsed)}",
                                    color = colors.textSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                val pnlText = if (pnl >= 0) "+${formatCredits(pnl)}" else formatCredits(pnl)
                                Text(
                                    text = pnlText,
                                    color = if (pnl >= 0) colors.positive else colors.negative,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            TerminalButton(
                                text = "ポジション決済 (成行)",
                                onClick = { viewModel.closeFxPosition(pos.id) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
