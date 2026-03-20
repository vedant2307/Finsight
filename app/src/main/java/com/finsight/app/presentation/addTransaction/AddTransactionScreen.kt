package com.finsight.app.presentation.addTransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsight.app.data.local.entity.CategoryEntity
import com.finsight.app.ui.theme.Green500
import com.finsight.app.ui.theme.Red500
import com.finsight.app.ui.theme.Teal50
import com.finsight.app.ui.theme.Teal500
import com.finsight.app.ui.theme.Teal900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }

    // Navigate back when saved successfully
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
            viewModel.resetSaved()
        }
    }

    // Show error in snack bar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackBarState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.date
        )

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            viewModel.onDateChanged(it)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = Teal500)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Teal500)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Transaction",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── AMOUNT INPUT ──────────────────────────────
            AmountInput(
                amount = uiState.amount,
                onAmountChange = viewModel::onAmountChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── TYPE TOGGLE ───────────────────────────────
            TypeToggle(
                selectedType = uiState.selectedType,
                onTypeChange = viewModel::onTypeChanged
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── CATEGORY GRID ─────────────────────────────
            if (uiState.categories.isNotEmpty()) {
                CategoryGrid(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelect = viewModel::onCategorySelect
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── TITLE INPUT ───────────────────────────────
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChanged,
                label = { Text("Title") },
                placeholder = { Text("e.g. Swiggy, Salary, Uber") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    focusedLabelColor = Teal500,
                    cursorColor = Teal500
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── NOTE INPUT ────────────────────────────────
            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChanged,
                label = { Text("Note (optional)") },
                placeholder = { Text("Add a note...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    focusedLabelColor = Teal500,
                    cursorColor = Teal500
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── DATE PICKER ───────────────────────────────
            OutlinedTextField(
                value = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(uiState.date)),
                onValueChange = { },
                label = { Text("Date") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Pick date",
                            tint = Teal500
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    focusedLabelColor = Teal500,
                    cursorColor = Teal500
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── SAVE BUTTON ───────────────────────────────
            Button(
                onClick = viewModel::onSaveTransaction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal500
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Save Transaction",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── AMOUNT INPUT ──────────────────────────────────────────

@Composable
fun AmountInput(amount: String, onAmountChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Enter Amount",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "₹",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Teal500
            )
            Spacer(modifier = Modifier.width(4.dp))
            BasicTextField(
                value = amount.ifEmpty { "0" },
                onValueChange = onAmountChange,
                textStyle = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal900,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}

// ── TYPE TOGGLE ───────────────────────────────────────────

@Composable
fun TypeToggle(
    selectedType: TransactionType,
    onTypeChange: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F5F9))
            .padding(4.dp)
    ) {
        // Expense button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (selectedType == TransactionType.EXPENSE)
                        Red500 else Color.Transparent
                )
                .clickable { onTypeChange(TransactionType.EXPENSE) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Expense",
                fontWeight = FontWeight.SemiBold,
                color = if (selectedType == TransactionType.EXPENSE)
                    Color.White else Color.Gray,
                fontSize = 14.sp
            )
        }

        // Income button
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (selectedType == TransactionType.INCOME)
                        Green500 else Color.Transparent
                )
                .clickable { onTypeChange(TransactionType.INCOME) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Income",
                fontWeight = FontWeight.SemiBold,
                color = if (selectedType == TransactionType.INCOME)
                    Color.White else Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryGrid(
    categories: List<CategoryEntity>,
    selectedCategory: CategoryEntity?,
    onCategorySelect: (CategoryEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Category",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Teal900,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory?.name == category.name
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) Teal50 else Color(0xFFF8FAFC)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Teal500
                            else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onCategorySelect(category) }
                        .padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = category.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = category.name,
                        fontSize = 10.sp,
                        color = if (isSelected) Teal500 else Color.Gray,
                        fontWeight = if (isSelected)
                            FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
