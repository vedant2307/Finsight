package com.finsight.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsight.app.data.local.entity.TransactionEntity
import com.finsight.app.ui.theme.Green100
import com.finsight.app.ui.theme.Green500
import com.finsight.app.ui.theme.Red100
import com.finsight.app.ui.theme.Red500
import com.finsight.app.ui.theme.Teal500
import com.finsight.app.ui.theme.Teal900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onAddTransaction : () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = Teal500,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Teal500)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            item { HomeHeader(uiState = uiState) }

            // Budget Progress
            item {
                BudgetProgressSection(
                    totalSpent = uiState.totalExpense,
                    totalBudget = 50000.0 // hardcoded for now
                )
            }

            // Recent Transactions Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Teal900
                    )
                    Text(
                        text = "See all",
                        fontSize = 13.sp,
                        color = Teal500
                    )
                }
            }

            // Transaction list
            if (uiState.recentTransactions.isEmpty()) {
                item { EmptyTransactionState() }
            } else {
                items(uiState.recentTransactions) { transaction ->
                    TransactionItem(transaction = transaction)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// ── HEADER ────────────────────────────────────────────────

@Composable
fun HomeHeader(uiState: HomeUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Teal500)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Good morning! 👋",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.85f)
            )

            Text(
                text = "Vedant",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Balance",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Text(
                        text = "₹${String.format("%,.2f", uiState.totalBalance)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Income and Expense Row
                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Income
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "↑", color = Color.White, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "Income",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "₹${String.format("%,.0f", uiState.totalIncome)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        // Income
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "↓", color = Color.White, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(
                                    text = "Expense",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "₹${String.format("%,.0f", uiState.totalExpense)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }


                    }
                }
            }
        }
    }
}

// ── BUDGET PROGRESS ───────────────────────────────────────

@Composable
fun BudgetProgressSection(totalSpent: Double, totalBudget: Double) {
    val progress = if (totalBudget > 0) {
        (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f)
    } else 0f

    val progressColor = when {
        progress >= 1f   -> Red500
        progress >= 0.8f -> Color(0xFFF59E0B)
        else             -> Teal500
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Monthly Budget",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Teal900
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = Color(0xFFF1F5F9)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: ₹${totalSpent.toInt()}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Budget: ₹${totalBudget.toInt()}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }


        }
    }
}

// ── TRANSACTION ITEM ──────────────────────────────────────

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    val isExpense = transaction.type == "EXPENSE"
    val amountColor = if (isExpense) Red500 else Green500
    val amountPrefix = if (isExpense) "-" else "+"
    val bgColor = if (isExpense) Red100 else Green100

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(text = getCategoryEmoji(transaction.category), fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Title and category
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1A1A)
            )
            Text(
                text = transaction.category,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Amount and date
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$amountPrefix₹${String.format("%,.0f", transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
            Text(
                text = formatDate(transaction.date),
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

// ── EMPTY STATE ───────────────────────────────────────────

@Composable
fun EmptyTransactionState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "💸", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No transactions yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to add your first transaction",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

// ── HELPERS ───────────────────────────────────────────────

fun getCategoryEmoji(category: String): String {
    return when (category.lowercase()) {
        "food"          -> "🍔"
        "transport"     -> "🚗"
        "shopping"      -> "🛍️"
        "groceries"     -> "🛒"
        "bills"         -> "💡"
        "health"        -> "💊"
        "rent"          -> "🏠"
        "entertainment" -> "🎬"
        "travel"        -> "✈️"
        "education"     -> "📚"
        "salary"        -> "💰"
        "freelance"     -> "💻"
        "investment"    -> "📈"
        else            -> "📦"
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}