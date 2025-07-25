package com.himag09.taskmaster.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himag09.taskmaster.R
import com.himag09.taskmaster.ui.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var hasUserBeenLoaded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Al borrar al usuario, volvemos al login.
    LaunchedEffect(uiState.user) {
        if (uiState.user != null) {
            // Usuario Cargado
            hasUserBeenLoaded = true
        } else if (hasUserBeenLoaded) {
            // Si el usuario es nulo pero ya se cargo antes, es porque se borro
            // y volvemos al login
            navigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.my_profile)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.go_back
                            )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (!hasUserBeenLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(text = stringResource(R.string.name)) },
                    isError = validateName(uiState.name),
                    modifier = Modifier.fillMaxWidth()
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
                    onValueChange = viewModel::updateEmail,
                    label = { Text(text = stringResource(R.string.email)) },
                    isError = validateEmail(uiState.email ?: ""),
                    modifier = Modifier.fillMaxWidth()
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
                    onClick = {
                        viewModel.saveChanges()
                        Toast.makeText(
                            context,
                            context.getString(R.string.profile_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateBack() // volvemos a la pantalla anterior
                    },
                    enabled = uiState.isInputValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.save_changes))
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.delete_account_tasks))
                }
            }

            if (showDeleteDialog) {
                ResetConfirmationDialog(
                    onConfirm = {
                        showDeleteDialog = false
                        viewModel.confirmReset()
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}

private fun validateName(name: String): Boolean {
    return name.isNotEmpty() && name.length < 3
}

private fun validateEmail(email: String): Boolean {
    return email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
private fun ResetConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.are_you_sure)) },
        text = { Text(text = stringResource(R.string.this_action_is_irreversible)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
            ) {
                Text(text = stringResource(R.string.yes_delete_all))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}