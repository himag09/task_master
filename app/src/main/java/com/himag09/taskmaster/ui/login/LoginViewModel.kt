package com.himag09.taskmaster.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.himag09.taskmaster.data.TasksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.himag09.taskmaster.data.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update


/**
 * Estado de la UI para la pantalla de Login/Registro.
 * @param name El nombre introducido por el usuario.
 * @param email El email introducido por el usuario.
 * @param isUserLoggedIn bandera para saber si el usuario ya ha iniciado sesión.
 * @param loggedInUserId ID del usuario que ingrso.
 * @param isLoading bandera para mostrar estado de carga inicial.
 */
data class LoginUiState(
    val name: String = "",
    val email: String = "",
    val isUserLoggedIn: Boolean = false,
    val loggedInUserId: Int? = null,
    val isLoading: Boolean = true,
    val isInputValid: Boolean = false
)

class LoginViewModel(private val tasksRepository: TasksRepository) : ViewModel() {
    // versión mutable y privada. Solo el ViewModel puede acceder a ella y modificarla
    private val _uiState = MutableStateFlow(LoginUiState())

    // versión inmutable para que los composables usan para leer el estado.
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Cuando iniciamos, comprobamos si ya existe un ususario.
    init {
        viewModelScope.launch {
            val user = tasksRepository.getFirstUserStream().firstOrNull()
            if (user != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false, isUserLoggedIn = true, loggedInUserId = user.id
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    // funcion para actualizar el nombre
    fun updateName(name: String) {
        _uiState.update {
            it.copy(name = name, isInputValid = validateInput(name = name, email = it.email))
        }
    }

    // funcion para actualizar el email
    fun updateEmail(email: String) {
        _uiState.update {
            it.copy(email = email, isInputValid = validateInput(name = it.name, email = email))
        }
    }

    // funcion para crear nuevo usuario
    fun saveUser() {
        if (validateInput()) {
            viewModelScope.launch {
                val newUser = User(
                    name = _uiState.value.name, email = _uiState.value.email.ifEmpty { null })
                // Insertamos el usuario, capturamos el id y actualizamos el estado.
                val newUserId = tasksRepository.insertUser(newUser).toInt()
                _uiState.update {
                    it.copy(
                        isUserLoggedIn = true, loggedInUserId = newUserId
                    )
                }
            }
        }
    }

    private fun validateInput(
        name: String = _uiState.value.name, email: String = _uiState.value.email
    ): Boolean {
        val isNameValid = name.isNotBlank() && name.length >= 3
        // Como el email es opcional, si esta vacio es valido, si escribio un caracter es invalido.
        val isEmailValid = email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()
        return isNameValid && isEmailValid
    }
}