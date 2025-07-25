package com.himag09.taskmaster.ui.profile

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himag09.taskmaster.data.TasksRepository
import com.himag09.taskmaster.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val name: String = "",
    val email: String = "",
    val isInputValid: Boolean = true
)

class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    // Esto no necesitamos porque solo manejamos un usuario en esta version.
    // private val userId: Int = checkNotNull(savedStateHandle[ProfileDestination.USER_ID_ARG])

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tasksRepository.getFirstUserStream().collect { user ->
                _uiState.update {
                    it.copy(
                        user = user,
                        name = user?.name ?: "",
                        email = user?.email ?: ""
                    )
                }
            }
        }
    }

    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name, isInputValid = validateInput(name = name, email = it.email))
        }
    }

    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email, isInputValid = validateInput(name = it.name, email = email))
        }
    }

    fun saveChanges() {
        if (validateInput()) {
            viewModelScope.launch {
                val currentUser = _uiState.value.user!!
                val updatedUser = currentUser.copy(
                    name = _uiState.value.name,
                    email = _uiState.value.email.ifBlank { null }
                )
                tasksRepository.updateUser(updatedUser)
            }
        }
    }

    fun confirmReset() {
        viewModelScope.launch {
            // Eliminamos todas las tareas
            tasksRepository.deleteAllTasks()
            // Eliminamos el usuario
            tasksRepository.deleteUser(_uiState.value.user!!)

        }
    }

    private fun validateInput(
        name: String = _uiState.value.name,
        email: String = _uiState.value.email
    ): Boolean {
        val isNameValid = name.isNotBlank() && name.length >= 3
        // Como el email es opcional, si esta vacio es valido, si escribio un caracter
        // es invalido.
        val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()

        return isNameValid && isEmailValid
    }
}