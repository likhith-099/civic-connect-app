package com.civicconnect.presentation.screens.admin.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.civicconnect.presentation.screens.auth.AuthState
import com.civicconnect.presentation.screens.components.CivicConnectButton
import com.civicconnect.presentation.screens.components.CivicConnectTextField
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToCitizenLogin: () -> Unit,
    viewModel: AdminLoginViewModel = hiltViewModel()
) {
    var isRegistering by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var municipalOffice by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp).size(60.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Admin Portal",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "CivicConnect Management",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isRegistering) "Staff Registration" else "Staff Authentication",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (isRegistering) {
                    CivicConnectTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name",
                        leadingIcon = Icons.Default.Person
                    )
                    CivicConnectTextField(
                        value = municipalOffice,
                        onValueChange = { municipalOffice = it },
                        label = "Municipal Office",
                        leadingIcon = Icons.Default.Business
                    )
                    CivicConnectTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = "Region",
                        leadingIcon = Icons.Default.Map
                    )
                }

                CivicConnectTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Admin Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                CivicConnectTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    leadingIcon = Icons.Default.Lock,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                if (authState is AuthState.Error) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                CivicConnectButton(
                    text = if (isRegistering) "Register as Staff" else "Authorize Access",
                    onClick = { 
                        if (isRegistering) viewModel.register(name, email, password, municipalOffice, region)
                        else viewModel.login(email, password)
                    },
                    isLoading = authState is AuthState.Loading
                )

                TextButton(onClick = { isRegistering = !isRegistering }) {
                    Text(if (isRegistering) "Already have staff access? Login" else "Request Staff Access (Register)")
                }

                TextButton(onClick = onNavigateToCitizenLogin) {
                    Text("Switch to Citizen Login")
                }
            }
        }
    }
}
