package com.himag09.taskmaster.ui.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.himag09.taskmaster.R
import com.himag09.taskmaster.data.Priority
import com.himag09.taskmaster.data.Task
import com.himag09.taskmaster.ui.AppViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Ruta de entrada para Home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    navigateToTaskEntry: () -> Unit,
    navigateToTaskDetail: (Int) -> Unit,
    navigateToProfile: () -> Unit,
    navigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.my_tasks)) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.primaryContainer
                ),
                actions = {
                    IconButton(onClick = navigateToSearch) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_tasks)
                        )
                    }
                    IconButton(onClick = navigateToProfile) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = stringResource(R.string.my_profile)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToTaskEntry,
                shape = shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_new_task)
                )
            }
        },
    ) { innerPadding ->
        TaskListBody(
            taskList = uiState.taskList,
            onTaskClick = navigateToTaskDetail,
            onTaskCheckedChange = viewModel::toggleTaskCompleted,
            modifier = modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun TaskListBody(
    taskList: List<Task>,
    onTaskClick: (Int) -> Unit,
    onTaskCheckedChange: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    if (taskList.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.theres_not_tasks),
                style = typography.titleLarge
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = taskList, key = { it.id }) { task ->
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
    val isPressed by interactionSource.collectIsPressedAsState()
    val context = LocalContext.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {} // Vacío porque ya manejamos con detectTapGestures
            )
            .pointerInput(onTaskCheckedChange, onTaskClick) { // detectar pulsación larga
                detectTapGestures(
                    onLongPress = {
                        onTaskCheckedChange()
                        Toast.makeText(
                            context,
                            if (!task.isCompleted) context.getString(R.string.marked_as_completed) else context.getString(
                                R.string.marked_as_pending
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onTap = { onTaskClick() }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 8.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.ends_at, task.dueDate.toFormattedString()),
                    style = typography.bodyMedium
                )
            }
            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PriorityBadge(priority = task.priority)
                Spacer(modifier = Modifier.height(8.dp))
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
                    imageVector = icon,
                    contentDescription = contentDesc,
                    tint = iconColor
                )
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val (text, color) = when (priority) {
        Priority.LOW -> stringResource(R.string.priority_low) to Color.Gray
        Priority.MEDIUM -> stringResource(R.string.priority_medium) to Color(0xFFFFA500) // Orange
        Priority.HIGH -> stringResource(R.string.priority_high) to Color.Red
    }
    Surface(
        color = color,
        shape = shapes.small,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// Funcion de extensión para ayudar a formatear la fecha
private fun Date.toFormattedString(): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}