package com.himag09.taskmaster.ui.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himag09.taskmaster.R
import com.himag09.taskmaster.data.Priority
import com.himag09.taskmaster.ui.AppViewModelProvider
import com.himag09.taskmaster.ui.entry.TaskDetails
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navigateToEditTask: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    // estado para la alerta de borrar tarea
    var showDeleteDialog by remember { mutableStateOf(false) }
    // para toast
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.task_detail)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                })
        }) { innerPadding ->
        TaskDetailBody(
            taskDetails = uiState.taskDetails,
            onEdit = { navigateToEditTask(uiState.taskDetails.id) },
            onDelete = { showDeleteDialog = true },
            modifier = modifier.padding(innerPadding)
        )
    }

    // Dialogo para eliminar tarea
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = stringResource(R.string.confirm)) },
            text = { Text(text = stringResource(R.string.are_you_sure_delete_task)) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        coroutineScope.launch {
                            viewModel.deleteTask()
                            Toast.makeText(
                                context,
                                context.getString(R.string.deleted_task), Toast.LENGTH_SHORT
                            ).show()
                            navigateBack()
                        }
                    }) {
                    Text(text = stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            })
    }
}


@Composable
private fun TaskDetailBody(
    taskDetails: TaskDetails,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = taskDetails.title, style = typography.headlineLarge
        )

        Divider()

        if (taskDetails.description.isNotBlank()) {
            Text(
                text = stringResource(R.string.description),
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = taskDetails.description, style = typography.bodyLarge)
        }

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.ends))
                }
                append(taskDetails.dueDate.toFormattedString())
            }, style = typography.bodyLarge
        )
        Row {
            Text(
                text = stringResource(R.string.priority),
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = taskDetails.priority.toLocalizedString(), style = typography.bodyLarge
            )
        }

        Row {
            Text(
                text = stringResource(R.string.status),
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (taskDetails.isCompleted) stringResource(R.string.completed)
                else stringResource(
                    R.string.pending
                ), style = typography.bodyLarge
            )
        }


        Spacer(Modifier.weight(1f))

        Divider()

        DateInfo(label = stringResource(R.string.creation_date), date = taskDetails.creationDate)
        DateInfo(
            label = stringResource(R.string.last_updated_at),
            date = taskDetails.lastModifiedDate
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.edit))
            }
            OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.delete))
            }
        }
    }
}

@Composable
private fun DateInfo(label: String, date: Date?) {
    if (date != null) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(date.toFormattedDateTimeString(), style = typography.bodySmall)
        }
    }
}

// funcion para formatear fecha en dd de mm de yyyy
private fun Date.toFormattedString(): String {
    val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    return sdf.format(this)
}

// funcion para mostrar fecha + hora
private fun Date.toFormattedDateTimeString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(this)
}

// funcion para mostrar en español la prioridad
private fun Priority.toLocalizedString(): String {
    return when (this) {
        Priority.LOW -> "Baja"
        Priority.MEDIUM -> "Media"
        Priority.HIGH -> "Alta"
    }
}