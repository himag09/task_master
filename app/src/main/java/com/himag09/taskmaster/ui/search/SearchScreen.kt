package com.himag09.taskmaster.ui.search

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himag09.taskmaster.R
import com.himag09.taskmaster.data.Priority
import com.himag09.taskmaster.data.Task
import com.himag09.taskmaster.ui.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    navigateToTaskDetail: (Int) -> Unit,
    viewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.search_tasks)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(
                                R.string.go_back
                            )
                        )
                    }
                })
        }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchControls(
                uiState = uiState,
                onQueryChange = viewModel::updateSearchQuery,
                onStatusChange = viewModel::updateStatusFilter,
                onPriorityChange = viewModel::updatePriorityFilter,
                onSortChange = viewModel::updateSortOrder
            )
            Divider()
            SearchResultsList(
                tasks = uiState.filteredTasks,
                onTaskClick = navigateToTaskDetail,
                onTaskCheckedChange = viewModel::toggleTaskCompleted
            )
        }
    }
}

@Composable
private fun SearchControls(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onStatusChange: (StatusFilter) -> Unit,
    onPriorityChange: (Priority?) -> Unit,
    onSortChange: (SortOrder) -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // para buscar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onQueryChange,
            label = { Text(text = stringResource(R.string.search_by_title_description)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Default.Clear, contentDescription = stringResource(R.string.clean)
                        )
                    }
                }
            })

        // Para filtrar por estado
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            StatusFilter.values().forEach { status ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = status.ordinal, count = StatusFilter.values().size
                    ),
                    onClick = { onStatusChange(status) },
                    selected = uiState.statusFilter == status
                ) {
                    Text(
                        text = when (status) {
                            StatusFilter.ALL -> stringResource(R.string.all)
                            StatusFilter.PENDING -> stringResource(R.string.pendings)
                            StatusFilter.COMPLETED -> stringResource(R.string.completes)
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Para filtrar por prioridad
            FilterDropdown(
                label = stringResource(R.string.priority2),
                options = mapOf(
                    "Todas" to null,
                    "Alta" to Priority.HIGH,
                    "Media" to Priority.MEDIUM,
                    "Baja" to Priority.LOW
                ),
                selectedValue = uiState.priorityFilter,
                onValueSelected = onPriorityChange,
                modifier = Modifier.weight(1f)
            )
            // Para setear un orden
            FilterDropdown(
                label = stringResource(R.string.order_by),
                options = mapOf(
                    "Venc. (Asc)" to SortOrder.BY_DUE_DATE_ASC,
                    "Venc. (Desc)" to SortOrder.BY_DUE_DATE_DESC,
                    "Prioridad" to SortOrder.BY_PRIORITY
                ),
                selectedValue = uiState.sortOrder,
                onValueSelected = onSortChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> FilterDropdown(
    label: String,
    options: Map<String, T>,
    selectedValue: T,
    onValueSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = options.entries.find { it.value == selectedValue }?.key ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (text, value) ->
                DropdownMenuItem(text = { Text(text = text) }, onClick = {
                    onValueSelected(value)
                    expanded = false
                })
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    tasks: List<Task>,
    onTaskClick: (Int) -> Unit,
    onTaskCheckedChange: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.not_task_filters))
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onTaskClick = { onTaskClick(task.id) },
                    onTaskCheckedChange = { onTaskCheckedChange(task) })
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onTaskClick: () -> Unit,
    onTaskCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado para la vista visual durante el long press
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {} // Vacío porque ya manejamos con detectTapGestures
                )
                .pointerInput(onTaskCheckedChange, onTaskClick) { // detectar pulsación larga
                    detectTapGestures(onLongPress = {
                        onTaskCheckedChange()
                        Toast.makeText(
                            context,
                            if (!task.isCompleted) context.getString(R.string.marked_as_completed)
                            else context.getString(
                                R.string.marked_as_pending
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }, onTap = { onTaskClick() })
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Vence: ${task.dueDate.toFormattedString()}",
                    style = typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            PriorityBadge(priority = task.priority)
            val (icon, iconColor, contentDesc) = if (task.isCompleted) {
                Triple(
                    Icons.Filled.CheckCircle,
                    Color(0xFF2E7D32),
                    stringResource(R.string.completed_task)
                )
            } else {
                Triple(
                    Icons.Rounded.DateRange,
                    colorScheme.onSurface.copy(alpha = 0.6f),
                    stringResource(R.string.pending_task)
                )
            }

            Icon(
                imageVector = icon, contentDescription = contentDesc, tint = iconColor
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> colorScheme.errorContainer
        Priority.MEDIUM -> colorScheme.tertiaryContainer
        Priority.LOW -> colorScheme.secondaryContainer
    }
    val textPriority = when (priority) {
        Priority.LOW -> "Baja"
        Priority.MEDIUM -> "Media"
        Priority.HIGH -> "Alta"
    }
    Text(
        text = textPriority,
        style = typography.labelSmall,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        color = colorScheme.onSurface
    )
}

private fun Date.toFormattedString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}
