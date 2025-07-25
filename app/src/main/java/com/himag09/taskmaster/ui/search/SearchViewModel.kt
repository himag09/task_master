package com.himag09.taskmaster.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himag09.taskmaster.data.Priority
import com.himag09.taskmaster.data.Task
import com.himag09.taskmaster.data.TasksRepository
import com.himag09.taskmaster.ui.navigation.SearchDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date


enum class SortOrder {
    NONE,
    BY_DUE_DATE_ASC,
    BY_DUE_DATE_DESC,
    BY_PRIORITY
}

enum class StatusFilter {
    ALL, PENDING, COMPLETED
}

data class SearchUiState(
    val searchQuery: String = "",
    val priorityFilter: Priority? = null, // null significa todos
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val sortOrder: SortOrder = SortOrder.BY_DUE_DATE_ASC,
    val allTasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList()
)

class SearchViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val userId: Int = checkNotNull(savedStateHandle[SearchDestination.USER_ID_ARG])

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tasksRepository.getAllTasksStream(userId).collect { tasks ->
                _uiState.update { it.copy(allTasks = tasks) }
                applyFilters()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun updatePriorityFilter(priority: Priority?) {
        _uiState.update { it.copy(priorityFilter = priority) }
        applyFilters()
    }

    fun updateStatusFilter(status: StatusFilter) {
        _uiState.update { it.copy(statusFilter = status) }
        applyFilters()
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        applyFilters()
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            // La lista se actualiza automáticamente con el Flow del init
            tasksRepository.updateTask(
                task.copy(
                    isCompleted = !task.isCompleted,
                    lastModifiedDate = Date()
                )
            )
        }
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        var results = currentState.allTasks

        // Para filtrar por titulo y descripcion
        if (currentState.searchQuery.isNotBlank()) {
            results = results.filter {
                it.title.contains(currentState.searchQuery, ignoreCase = true) ||
                        it.description?.contains(
                            currentState.searchQuery,
                            ignoreCase = true
                        ) ?: false
            }
        }

        // Para filtrar por estado completado/no completado
        results = when (currentState.statusFilter) {
            StatusFilter.PENDING -> results.filter { !it.isCompleted }
            StatusFilter.COMPLETED -> results.filter { it.isCompleted }
            StatusFilter.ALL -> results
        }

        // Para filtrar por prioridad alta, media, baja
        currentState.priorityFilter?.let { priority ->
            results = results.filter { it.priority == priority }
        }

        // Para agregar un orden: fecha de entrega, prioridad, ninguno.
        results = when (currentState.sortOrder) {
            SortOrder.BY_DUE_DATE_ASC -> results.sortedBy { it.dueDate }
            SortOrder.BY_DUE_DATE_DESC -> results.sortedByDescending { it.dueDate }
            SortOrder.BY_PRIORITY -> results.sortedByDescending { it.priority.ordinal }
            SortOrder.NONE -> results
        }

        _uiState.update { it.copy(filteredTasks = results) }
    }
}