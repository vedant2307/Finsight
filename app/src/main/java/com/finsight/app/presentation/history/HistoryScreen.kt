package com.finsight.app.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsight.app.data.local.entity.TransactionEntity
import com.finsight.app.presentation.home.TransactionItem
import com.finsight.app.presentation.home.getCategoryEmoji
import com.finsight.app.ui.theme.Teal500
import com.finsight.app.ui.theme.Teal900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction History",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal500
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ── SEARCH BAR ────────────────────────────────
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                placeholder = { Text("Search transactions...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Teal500
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onSearchQueryChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    cursorColor = Teal500
                ),
                singleLine = true
            )

            // ── FILTER TABS ───────────────────────────────
            FilterTabs(
                selectedFilter = uiState.selectedFilter,
                onFilterChange = viewModel::onFilterChange
            )

            // ── CONTENT ───────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Teal500)
                    }
                }

                uiState.filteredTransactions.isEmpty() -> {
                    EmptyHistoryState(
                        isSearching = uiState.searchQuery.isNotEmpty()
                    )
                }

                else -> {
                    TransactionList(
                        transactions = uiState.filteredTransactions,
                        onDeleteTransaction = viewModel::deleteTransaction
                    )
                }
            }
        }
    }
}

// ── FILTER TABS ───────────────────────────────────────────

@Composable
fun FilterTabs(
    selectedFilter: TransactionFilter,
    onFilterChange: (TransactionFilter) -> Unit
)  {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionFilter.entries.forEach { filter ->
            val isSelected = selectedFilter == filter
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) Teal500 else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable {
                        onFilterChange(filter)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.name.lowercase()
                        .replaceFirstChar { it.uppercase() },
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) Color.White else Color.Gray
                )
            }
        }
    }
}

// ── TRANSACTION LIST ──────────────────────────────────────

@Composable
fun TransactionList(
    transactions: List<TransactionEntity>,
    onDeleteTransaction: (TransactionEntity) -> Unit
) {
    // Group transactions by date
    val groupedTransactions = transactions.groupBy { transaction ->
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.date))
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        groupedTransactions.forEach { (date, transactionsForDate) ->

            // Date header
            item {
                Text(
                    text = formatDateHeader(date),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    )
                )
            }

            // Transactions for this date
            items(
                items = transactionsForDate,
                key = { it.id }
            ) { transaction ->
                TransactionItem(transaction = transaction)
            }

            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .padding(horizontal = 16.dp)
                        .background(Color(0xFFF1F5F9))
                )
            }
        }
    }
}

// ── EMPTY STATE ───────────────────────────────────────────

@Composable
fun EmptyHistoryState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSearching) "🔍" else "📋",
            fontSize = 48.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isSearching) "No transactions found"
            else "No transactions yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Teal900
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isSearching) "Try a different search term"
            else "Add your first transaction using the + button",
            fontSize = 13.sp,
            color = Color.Gray
        )
    }
}

// ── HELPERS ───────────────────────────────────────────────

fun formatDateHeader(dateString: String): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val today = sdf.format(Date())
    val yesterday = sdf.format(Date(System.currentTimeMillis() - 86400000))

    return when (dateString) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> dateString
    }
}