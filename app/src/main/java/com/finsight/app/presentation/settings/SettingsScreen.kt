package com.finsight.app.presentation.settings

import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsight.app.ui.theme.Teal50
import com.finsight.app.ui.theme.Teal500
import com.finsight.app.ui.theme.Teal900
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showEditSalaryDialog by remember { mutableStateOf(false) }

    // Edit dialogs
    if (showEditNameDialog) {
        EditDialog(
            title = "Edit Name",
            currentValue = uiState.name,
            keyboardType = KeyboardType.Text,
            onDismiss = { showEditNameDialog = false },
            onConfirm = {
                viewModel.updateName(it)
                showEditNameDialog = false
            }
        )
    }

    if (showEditSalaryDialog) {
        EditDialog(
            title = "Edit Monthly Salary",
            currentValue = if (uiState.salary == 0.0) ""
            else uiState.salary.toInt().toString(),
            keyboardType = KeyboardType.Decimal,
            prefix = uiState.currency,
            onDismiss = { showEditSalaryDialog = false },
            onConfirm = { salaryString ->
                viewModel.updateSalary(salaryString.toDoubleOrNull() ?: 0.0) // ← convert here
                showEditSalaryDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile & Settings",
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

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Teal500)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── PROFILE AVATAR ────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Teal50),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.name.firstOrNull()?.uppercaseChar()
                            ?.toString() ?: "?",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Teal500
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = uiState.name.ifEmpty { "Your Name" },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Teal900
                )

                Text(
                    text = "Android Developer",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // ── PROFILE SECTION ───────────────────────────
            SettingsSection(title = "Profile") {
                SettingsRow(
                    label = "Name",
                    value = uiState.name.ifEmpty { "Not set" },
                    onEdit = { showEditNameDialog = true }
                )
                SettingsRow(
                    label = "Monthly Salary",
                    value = if (uiState.salary == 0.0) "Not set"
                    else "${uiState.currency}${uiState.salary.toInt()}",
                    onEdit = { showEditSalaryDialog = true }
                )
                SettingsRow(
                    label = "Currency",
                    value = "${uiState.currency} INR",
                    onEdit = null // currency not editable for now
                )
            }

            // ── APP SECTION ───────────────────────────────
            SettingsSection(title = "App") {
                SettingsRow(
                    label = "Version",
                    value = "1.0.0",
                    onEdit = null
                )
                SettingsRow(
                    label = "GitHub",
                    value = "github.com/vedant2307/Finsight",
                    onEdit = null,
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/vedant2307/Finsight".toUri()
                        )
                        context.startActivity(intent)
                    }
                )
                SettingsRow(
                    label = "Built with",
                    value = "Kotlin + Jetpack Compose",
                    onEdit = null
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── SETTINGS SECTION ──────────────────────────────────────

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Teal500,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            content()
        }
    }
}

// ── SETTINGS ROW ──────────────────────────────────────────

@Composable
fun SettingsRow(
    label: String,
    value: String,
    onEdit: (() -> Unit)?,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (onClick != null) Teal500 else Teal900
            )
            if (onEdit != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.LightGray,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onEdit() }
                )
            }
        }
    }
}

// ── EDIT DIALOG ───────────────────────────────────────────

@Composable
fun EditDialog(
    title: String,
    currentValue: String,
    keyboardType: KeyboardType,
    prefix: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var value by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Teal900
            )
        },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType
                ),
                prefix = if (prefix.isNotEmpty()) {
                    { Text(prefix, color = Teal500) }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal500,
                    focusedLabelColor = Teal500,
                    cursorColor = Teal500
                ),
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = { if (value.isNotBlank()) onConfirm(value) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal500
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save")
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