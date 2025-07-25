package com.himag09.taskmaster.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himag09.taskmaster.data.TasksRepository
import com.himag09.taskmaster.ui.entry.TaskDetails
import com.himag09.taskmaster.ui.entry.toTask
import com.himag09.taskmaster.ui.entry.toTaskDetails
import com.himag09.taskmaster.ui.navigation.TaskDetailDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UiState para la pantalla de detalle de la tarea.
 */
data class TaskDetailUiState(
    val taskDetails: TaskDetails = TaskDetails()
)

class TaskDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val taskId: Int = checkNotNull(savedStateHandle[TaskDetailDestination.TASK_ID_ARG])

    val uiState: StateFlow<TaskDetailUiState> =
        tasksRepository.getTaskStream(taskId)
            .filterNotNull()
            .map { TaskDetailUiState(taskDetails = it.toTaskDetails()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TaskDetailUiState()
            )

    fun deleteTask() {
        viewModelScope.launch {
            tasksRepository.deleteTask(uiState.value.taskDetails.toTask())
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}