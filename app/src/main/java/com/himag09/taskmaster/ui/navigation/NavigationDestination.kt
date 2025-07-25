package com.himag09.taskmaster.ui.navigation

/**
 * Interface para describir los destinos de navegacionn para la aplicacion.
 */
interface NavigationDestination {
    /**
     * Ruta unica para el destino.
     */
    val route: String

}

object LoginDestination : NavigationDestination {
    override val route = "login"
}

object HomeDestination : NavigationDestination {
    override val route = "home"
    const val USER_ID_ARG = "userId"

    // Ruta con el argumento que necesita
    val routeWithArgs = "$route/{$USER_ID_ARG}"
}

object TaskEntryDestination : NavigationDestination {
    override val route = "task_entry"
    const val USER_ID_ARG = "userId"
    val routeWithArgs = "$route/{$USER_ID_ARG}"
}

object TaskEditDestination : NavigationDestination {
    override val route = "task_edit"
    const val TASK_ID_ARG = "taskId"
    val routeWithArgs = "$route/{$TASK_ID_ARG}"
}

object TaskDetailDestination : NavigationDestination {
    override val route = "task_detail"
    const val TASK_ID_ARG = "taskId"
    val routeWithArgs = "$route/{$TASK_ID_ARG}"
}

object ProfileDestination : NavigationDestination {
    override val route = "profile"

    // La pantalla de perfil tambien necesita saber que usuario es
    const val USER_ID_ARG = "userId"
    val routeWithArgs = "$route/{$USER_ID_ARG}"
}

object SearchDestination : NavigationDestination {
    override val route = "search"
    const val USER_ID_ARG = "userId"
    val routeWithArgs = "$route/{$USER_ID_ARG}"
}