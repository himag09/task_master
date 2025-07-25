package com.himag09.taskmaster.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himag09.taskmaster.ui.AppViewModelProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.himag09.taskmaster.R

@Composable
fun LoginScreen(
    navigateToHome: (Int) -> Unit, // Recibimos el id del usuario para navegar
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    /*
    uiState para recopilar el estado de la IU de LoginViewModel.
    Usa collectAsState(), que recopila valores de este StateFlow y representa su
    valor más reciente mediante State.
    */
    val uiState by viewModel.uiState.collectAsState()

    // Si el estado cambia a "logueado", navega a la pantalla principal
    LaunchedEffect(uiState.isUserLoggedIn) {
        if (uiState.isUserLoggedIn && uiState.loggedInUserId != null) {
            navigateToHome(uiState.loggedInUserId!!)
        }
    }

    Scaffold { innerPadding ->
        if (uiState.isLoading) {
            LoadingScreen(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        } else if (!uiState.isUserLoggedIn) {
            LoginBody(
                uiState = uiState,
                onNameChange = viewModel::updateName,
                onEmailChange = viewModel::updateEmail,
                onLoginClick = viewModel::saveUser,
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun LoginBody(
    uiState: LoginUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.bienvenido_a_taskmaster),
            style = typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(R.string.name)) },
            singleLine = true,
            isError = validateName(uiState.name),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (validateName(uiState.name)) {
            Text(
                text = stringResource(R.string.min_character_name),
                color = colorScheme.error,
                style = typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        OutlinedTextField(
            value = uiState.email ?: "",
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(R.string.email)) },
            singleLine = true,
            isError = validateEmail(uiState.email ?: ""),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (validateEmail(uiState.email ?: "")
        ) {
            Text(
                text = stringResource(R.string.email_not_valid),
                color = colorScheme.error,
                style = typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Button(
            onClick = onLoginClick,
            enabled = uiState.isInputValid,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(R.string.register))
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.taskmaster),
            style = typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

private fun validateName(name: String): Boolean {
    return name.isNotEmpty() && name.length < 3
}

private fun validateEmail(email: String): Boolean {
    return email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview(showBackground = true, name = "Login Body Preview")
@Composable
fun LoginBodyPreview() {
    val uiState = LoginUiState(
        name = "Eduardo",
        email = "eduardo@correo.com",
        isUserLoggedIn = false,
        loggedInUserId = null
    )

    LoginBody(
        uiState = uiState,
        onNameChange = {},
        onEmailChange = {},
        onLoginClick = {}
    )
}
