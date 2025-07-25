package com.himag09.taskmaster.ui.entry

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himag09.taskmaster.R
import com.himag09.taskmaster.data.Priority
import com.himag09.taskmaster.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    // Usamos CoroutineScope para llamar a funciones suspend
    val coroutineScope = rememberCoroutineScope()
    // Obtenemos el titulo dinamicamente
    val isEditing = uiState.id != 0
    val title =
        if (isEditing) stringResource(R.string.edit_task) else stringResource(R.string.new_task)
    val message =
        if (isEditing) stringResource(R.string.task_updated) else stringResource(R.string.task_created)

    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = title) }, navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }
            })
        }) { innerPadding ->
        TaskEntryBody(
            uiState = uiState, onStateChange = viewModel::updateUiState, onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveTask()
                    Toast.makeText(
                        context, message, Toast.LENGTH_SHORT
                    ).show()
                    navigateBack()
                }
            }, modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
fun TaskEntryBody(
    uiState: TaskUiState,
    onStateChange: (TaskUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Titulo
        OutlinedTextField(
            value = uiState.title,
            onValueChange = { onStateChange(uiState.copy(title = it)) },
            label = { Text(text = stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = !uiState.isEntryValid && uiState.title.isNotEmpty()
        )
        // Descripcion
        OutlinedTextField(
            value = uiState.description,
            onValueChange = { onStateChange(uiState.copy(description = it)) },
            label = { Text(text = stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )
        // Seleccioon de fecha
        DatePickerField(
            selectedDate = uiState.dueDate,
            onDateSelected = { onStateChange(uiState.copy(dueDate = it)) })
        // Elegir prioridad
        PrioritySelector(
            selectedPriority = uiState.priority,
            onPrioritySelected = { onStateChange(uiState.copy(priority = it)) })
        // Para marcar tarea como completada
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.complete), style = typography.bodyLarge)
            Switch(
                checked = uiState.isCompleted,
                onCheckedChange = { onStateChange(uiState.copy(isCompleted = it)) })
        }
        Spacer(modifier = Modifier.weight(1f))
        // Boton para guardar
        Button(
            onClick = onSaveClick,
            enabled = uiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_task))
        }
    }
}


@Composable
private fun DatePickerField(
    selectedDate: Date, onDateSelected: (Date) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(newCalendar.time)
        }, year, month, day
    )

    OutlinedTextField(
        value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate),
        onValueChange = {},
        readOnly = true,
        label = { Text(text = stringResource(R.string.end_date)) },
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.select_date)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority, onPrioritySelected: (Priority) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.priority2), style = typography.bodyLarge)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Priority.values().forEach { priority ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (priority == selectedPriority),
                        onClick = { onPrioritySelected(priority) })
                    Text(
                        text = priority.toLocalizedString(), style = typography.bodyMedium
                    )
                }
            }
        }
    }
}

// funcion para mostrar en español la prioridad
private fun Priority.toLocalizedString(): String {
    return when (this) {
        Priority.LOW -> "Baja"
        Priority.MEDIUM -> "Media"
        Priority.HIGH -> "Alta"
    }
}