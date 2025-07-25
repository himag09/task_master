package com.himag09.taskmaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.himag09.taskmaster.ui.detail.TaskDetailScreen
import com.himag09.taskmaster.ui.entry.TaskEntryScreen
import com.himag09.taskmaster.ui.home.TaskListScreen
import com.himag09.taskmaster.ui.login.LoginScreen
import com.himag09.taskmaster.ui.profile.ProfileScreen
import com.himag09.taskmaster.ui.search.SearchScreen

/**
 * componente de Jetpack Compose que gestiona la pantalla que se va mostrar segun la ruta actual
 */
@Composable
fun TaskMasterNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination.route,
        modifier = modifier
    ) {
        // Ruta para la pantalla de Login
        composable(route = LoginDestination.route) {
            LoginScreen(
                navigateToHome = { userId ->
                    // Navegamos a home y limpiamos el backStage para que
                    // no se pueda volver atras con el boton de atras.
                    navController.navigate("${HomeDestination.route}/$userId") {
                        popUpTo(LoginDestination.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Ruta para la pantalla de Home (Lista de Tareas)
        composable(
            route = HomeDestination.routeWithArgs,
            arguments = listOf(navArgument(HomeDestination.USER_ID_ARG) {
                type = NavType.IntType
            })
        ) { backStackEntry -> // Obtenemos el backStackEntry
            // obtenemos el usuario id
            val userId = backStackEntry.arguments?.getInt(HomeDestination.USER_ID_ARG)
            if (userId != null) {
                TaskListScreen(
                    navigateToTaskEntry = {
                        // navegamos a crear tarea pasando el userId
                        navController.navigate("${TaskEntryDestination.route}/$userId")
                    },
                    navigateToTaskDetail = { taskId ->
                        // para ir al detalle de tarea
                        navController.navigate("${TaskDetailDestination.route}/$taskId")
                    },
                    navigateToProfile = {
                        // para ir al perfil del usuario
                        navController.navigate("${ProfileDestination.route}/$userId")
                    },
                    navigateToSearch = {
                        navController.navigate("${SearchDestination.route}/$userId")
                    }
                )
            }
        }

        // ruta para crear una tarea
        // espera un argumento que es el userId
        composable(
            route = TaskEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskEntryDestination.USER_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            TaskEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }

        // Ruta de detalle de tarea
        composable(
            route = TaskDetailDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskDetailDestination.TASK_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            TaskDetailScreen(
                navigateBack = { navController.navigateUp() },
                navigateToEditTask = { taskId ->
                    navController.navigate("${TaskEditDestination.route}/$taskId")
                }
            )
        }
        // Ruta para editar tarea
        composable(
            route = TaskEditDestination.routeWithArgs,
            arguments = listOf(navArgument(TaskEditDestination.TASK_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            TaskEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        // Ruta para perfil del usuario
        composable(
            route = ProfileDestination.routeWithArgs,
            arguments = listOf(navArgument(ProfileDestination.USER_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            ProfileScreen(
                navigateBack = { navController.navigateUp() },
                navigateToLogin = {
                    navController.navigate(LoginDestination.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        // Ruta para busqueda de tareas
        composable(
            route = SearchDestination.routeWithArgs,
            arguments = listOf(navArgument(SearchDestination.USER_ID_ARG) {
                type = NavType.IntType
            })
        ) {
            SearchScreen(
                navigateBack = { navController.navigateUp() },
                navigateToTaskDetail = { taskId -> navController.navigate("${TaskDetailDestination.route}/$taskId") }
            )
        }
    }
}