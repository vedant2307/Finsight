package com.finsight.app.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.finsight.app.ui.theme.Teal50
import com.finsight.app.ui.theme.Teal500
import com.finsight.app.ui.theme.Teal900

@Composable
fun OnboardingScreen(
    onBoardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onBoardingComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            StepIndicator(currentStep = uiState.currentStep)

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    0 -> WelcomeStep()
                    1 -> SetupStep(
                        name = uiState.name,
                        salary = uiState.salary,
                        errorMessage = uiState.errorMessage,
                        onNameChange = viewModel::onNameChange,
                        onSalaryChange = viewModel::onSalaryChange
                    )

                    2 -> CurrencyStep(
                        selectedCurrency = uiState.selectedCurrency,
                        onCurrencySelect = viewModel::onCurrencySelect
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.currentStep > 0) {
                    TextButton(onClick = viewModel::previousStep) {
                        Text(
                            text = "Back",
                            color = Teal500,
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }

                Button(
                    onClick = {
                        if (uiState.currentStep < 2) {
                            viewModel.nextStep()
                        } else {
                            viewModel.completeOnBoarding()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal500),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(48.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.padding(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = if (uiState.currentStep < 2) "Continue" else "Let's Go!",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── STEP INDICATOR ────────────────────────────────────────

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (index == currentStep) 24.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index <= currentStep) Teal500
                        else Color(0xFFE2E8F0)
                    )
            )
        }
    }
}

// ── STEP 0 — WELCOME ──────────────────────────────────────

@Composable
fun WelcomeStep() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "💰",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to\nFinsight",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Teal900,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your smart money companion.\nTrack, understand, and grow your finances.",
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

// ── STEP 1 — PERSONAL SETUP ───────────────────────────────

@Composable
fun SetupStep(
    name: String,
    salary: String,
    errorMessage: String?,
    onNameChange: (String) -> Unit,
    onSalaryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Let's set up\nyour profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Teal900,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Your name") },
            placeholder = { Text("e.g. Vedant") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal500,
                focusedLabelColor = Teal500,
                cursorColor = Teal500
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Salary field
        OutlinedTextField(
            value = salary,
            onValueChange = onSalaryChange,
            label = { Text("Monthly salary") },
            placeholder = { Text("e.g. 50000") },
            prefix = { Text("₹  ") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal500,
                focusedLabelColor = Teal500,
                cursorColor = Teal500
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

// ── STEP 2 — CURRENCY ─────────────────────────────────────

@Composable
fun CurrencyStep(
    selectedCurrency: String,
    onCurrencySelect: (String) -> Unit
) {
    val currencies = listOf(
        Pair("₹", "Indian Rupee"),
        Pair("$", "US Dollar"),
        Pair("€", "Euro"),
        Pair("£", "British Pound"),
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pick your\ncurrency",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Teal900,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        currencies.forEach { (symbol, name) ->
            val isSelected = selectedCurrency == symbol
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Teal50 else Color.White)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Teal500 else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onCurrencySelect(symbol) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = symbol, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Teal500 else Color.Black
                    )
                }
                if (isSelected) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "✓", color = Teal500, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}