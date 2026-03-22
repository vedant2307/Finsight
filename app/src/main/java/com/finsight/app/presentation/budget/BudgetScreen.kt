package com.finsight.app.presentation.budget

import androidx.appcompat.app.AlertDialog
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsight.app.data.local.entity.BudgetEntity
import com.finsight.app.presentation.Utils
import com.finsight.app.ui.theme.Amber500
import com.finsight.app.ui.theme.Red500
import com.finsight.app.ui.theme.Teal50
import com.finsight.app.ui.theme.Teal500
import com.finsight.app.ui.theme.Teal900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.onClearError()
        }
    }

    if (showAddDialog) {
        AddBudgetDialog(
            onDismiss = {
                showAddDialog = false
            },
            onConfirm = { category, amount ->
                viewModel.addBudget(category, amount)
                showAddDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Budget",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal500
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Teal500,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Budget",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── OVERALL SUMMARY ───────────────────────────
            item {
                OverallBudgetCard(
                    totalBudget = uiState.totalBudget,
                    totalSpent = uiState.totalSpent
                )
            }

            // ── EMPTY STATE ───────────────────────────────
            if (uiState.budgetProgressList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "💰", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No budgets set",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Teal900
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap + to add your first budget",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                // ── BUDGET CARDS ──────────────────────────
                item {
                    Text(
                        text = "Category Budgets",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Teal900
                    )
                }

                items(
                    items = uiState.budgetProgressList,
                    key = { it.budget.id }
                ) { budgetProgress ->
                    BudgetCard(
                        budgetProgress = budgetProgress,
                        onDelete = { viewModel.deleteBudget(budgetProgress.budget) }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

// ── OVERALL SUMMARY CARD ──────────────────────────────────

@Composable
fun OverallBudgetCard(totalBudget: Double, totalSpent: Double) {
    val progress = if (totalBudget > 0) {
        (totalSpent / totalBudget).toFloat().coerceIn(0f, 1f)
    } else 0f

    val progressColor = when {
        progress >= 1f -> Red500
        progress >= 0.8f -> Amber500
        else -> Teal500
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Teal500)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Monthly Overview",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${totalSpent.toInt()} spent",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "of ₹${totalBudget.toInt()}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(progress * 100).toInt()}% of total budget used",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// ── BUDGET CARD ───────────────────────────────────────────

@Composable
fun BudgetCard(
    budgetProgress: BudgetProgress,
    onDelete: () -> Unit
) {
    val progress = budgetProgress.progress
    val progressColor = when {
        progress >= 1f   -> Red500
        progress >= 0.8f -> Amber500
        else             -> Teal500
    }

    val statusText = when {
        progress >= 1f   -> "Over budget!"
        progress >= 0.8f -> "Almost reached!"
        else             -> "On track"
    }

    val statusColor = when {
        progress >= 1f   -> Red500
        progress >= 0.8f -> Amber500
        else             -> Teal500
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category emoji
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Teal50),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Utils.getCategoryEmoji(budgetProgress.budget.category),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = budgetProgress.budget.category,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Teal900
                        )
                        Text(
                            text = statusText,
                            fontSize = 11.sp,
                            color = statusColor
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.LightGray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    text = "₹${budgetProgress.spent.toInt()} spent",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "₹${budgetProgress.budget.amount.toInt()} limit",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (category: String, amount: Double) -> Unit
) {
    val categories = listOf(
        "Food", "Transport", "Shopping", "Groceries",
        "Bills", "Health", "Rent", "Entertainment",
        "Travel", "Education", "Investment", "Other"
    )

    var selectedCategory by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Budget",
                fontWeight = FontWeight.Bold,
                color = Teal900
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal500,
                            focusedLabelColor = Teal500
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Amount input
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it.filter { c -> c.isDigit() || c == '.' }
                    },
                    label = { Text("Monthly limit (₹)") },
                    placeholder = { Text("e.g. 5000") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal500,
                        focusedLabelColor = Teal500,
                        cursorColor = Teal500
                    ),
                    singleLine = true
                )

                if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = Red500,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        selectedCategory.isEmpty() -> {
                            errorText = "Please select a category"
                        }
                        amount.isBlank() ||
                                amount.toDoubleOrNull() == null -> {
                            errorText = "Please enter a valid amount"
                        }
                        amount.toDouble() <= 0 -> {
                            errorText = "Amount must be greater than 0"
                        }
                        else -> {
                            onConfirm(selectedCategory, amount.toDouble())
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal500
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Add Budget")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Teal500)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}