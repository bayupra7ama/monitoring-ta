package com.tugas.route

import EditProjectScreen
import android.annotation.SuppressLint
import com.tugas.layout.ui.auth.RegisterScreen
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.tugas.layout.ui.mahasiswa.AddProjectScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tugas.data.repository.ProjectRepository
import com.tugas.data.repository.UserPreferences
import com.tugas.layout.ui.dosen.NotificationScreen
import com.tugas.layout.ui.mahasiswa.AddTaskScreen
import com.tugas.layout.ui.mahasiswa.DashboardScreen
import com.tugas.layout.ui.auth.LoginScreen
import com.tugas.layout.ui.mahasiswa.ProfileScreen
import com.tugas.layout.ui.mahasiswa.ProjectListScreen
import com.tugas.layout.ui.mahasiswa.TaskDetailScreen
import com.tugas.layout.navigasi.BottomNavItem
import com.tugas.layout.ui.dosen.DosenDashboardScreen
import com.tugas.layout.ui.dosen.DosenProgressReportScreen
import com.tugas.layout.ui.document.PdfViewerScreen
import com.tugas.layout.ui.dosen.ReportDetailScreen
import com.tugas.layout.ui.mahasiswa.ProgressReportScreen
import com.tugas.layout.ui.splashscreen.SplashScreen
import com.tugas.viewmodel.UserViewModel
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val roleState = prefs.roleFlow.collectAsState(initial = "")
    val role = roleState.value
    val isLoggedIn = role == "mahasiswa" || role == "dosen"

    val bottomNavItems = when (role) {
        "mahasiswa" -> listOf(
            BottomNavItem(AppRoute.HOME, "Home", Icons.Default.Home),
            BottomNavItem("progress", "Progress", Icons.Default.DateRange),
            BottomNavItem("profile", "Profil", Icons.Default.Person),

        )
        "dosen" -> listOf(
            BottomNavItem(AppRoute.DOSENHOME, "Dashboard", Icons.Default.Home),
            BottomNavItem("dosen_reports", "Laporan", Icons.Default.Description),
            BottomNavItem("profile", "Profil", Icons.Default.Person),

        )
        else -> emptyList()
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in listOf(AppRoute.LOGIN, AppRoute.REGISTER, AppRoute.SPLASH)

    Scaffold(
        bottomBar = {
            if (showBottomBar && bottomNavItems.isNotEmpty()) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoute.SPLASH, // <-- UBAH MENJADI INI
            modifier = Modifier.padding(innerPadding)
        ) {

            // Auth

            composable(AppRoute.SPLASH) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(AppRoute.LOGIN) {
                            // Hapus splash screen dari back stack
                            popUpTo(AppRoute.SPLASH) { inclusive = true }
                        }
                    },
                    onNavigateToHome = { userRole ->
                        val destination = when (userRole) {
                            "mahasiswa" -> AppRoute.HOME
                            "dosen" -> AppRoute.DOSENHOME
                            else -> AppRoute.LOGIN // Fallback
                        }
                        navController.navigate(destination) {
                            // Hapus splash screen dari back stack
                            popUpTo(AppRoute.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(AppRoute.LOGIN) {
                val scope = rememberCoroutineScope()
                val projectRepo = ProjectRepository()

                LoginScreen(
                    onLoginClick = { token, user ->
                        scope.launch {
                            val projects = projectRepo.getProjects(token)

                            if (user.role == "mahasiswa") {
                                if (projects.data.isEmpty()) {
                                    navController.navigate(AppRoute.ADD_PROJECT) {
                                        popUpTo(AppRoute.LOGIN) { inclusive = true }
                                    }
                                } else {
                                    val projectId = projects.data[0].id
                                    navController.navigate("projects/$projectId") {
                                        popUpTo(AppRoute.LOGIN) { inclusive = true }
                                    }
                                }
                            } else {
                                navController.navigate(AppRoute.DOSENHOME) {
                                    popUpTo(AppRoute.LOGIN) { inclusive = true }
                                }
                            }
                        }
                    },
                    onRegisterNavigate = { navController.navigate(AppRoute.REGISTER) }
                )
            }



            composable(AppRoute.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = { token, role ->
                        navController.navigate(
                            when (role) {
                                "mahasiswa" -> AppRoute.HOME
                                "dosen" -> AppRoute.DOSENHOME
                                else -> AppRoute.LOGIN
                            }
                        ) {
                            popUpTo(AppRoute.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onLoginNavigate = {

                        navController.popBackStack() // Ini akan menghapus RegisterScreen dari back stack dan kembali ke screen sebelumnya

                    }
                )
            }

            // Mahasiswa Routes
            composable(AppRoute.HOME) {
                val context = LocalContext.current
                val prefs = remember { UserPreferences(context) }
                val scope = rememberCoroutineScope()
                val projectRepo = ProjectRepository()

                LaunchedEffect(Unit) {
                    scope.launch {
                        val token = prefs.getToken()
                        val projects = projectRepo.getProjects(token!!)

                        if (projects.data.isEmpty()) {
                            // belum punya project → tampilkan project list screen
                        } else {
                            val projectId = projects.data[0].id
                            navController.navigate("projects/$projectId") {
                                popUpTo(AppRoute.HOME) { inclusive = true }
                            }
                            return@launch
                        }
                    }
                }

                // tampilkan ProjectListScreen hanya jika belum punya project
                ProjectListScreen(
                    onProjectClick = { navController.navigate("projects/${it.id}") },
                    onAddProjectClick = { navController.navigate(AppRoute.ADD_PROJECT) },
                    onNavigateToNotifications = { navController.navigate("notifications") },
                    navController = navController
                )
            }



            composable("progress") {
                ProgressReportScreen(
                    onBackClick = { navController.navigate(AppRoute.HOME)},
                    navController = navController
                )
            }

            composable("profile") {
                val context = LocalContext.current
                val prefs = remember { UserPreferences(context) }
                val viewModel: UserViewModel = viewModel()
                val user by viewModel.user.collectAsState()
                val isLoading by viewModel.isLoading.collectAsState()
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    val token = prefs.getToken()
                    if (token != null) {
                        viewModel.fetchProfile(token)
                    }
                }

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    user != null -> {
                        ProfileScreen(
                            user = user!!,
                            onLogout = {
                                coroutineScope.launch {
                                    prefs.clear()
                                    viewModel.clearState()
                                    navController.navigate(AppRoute.LOGIN) {
                                        popUpTo(AppRoute.LOGIN) { inclusive = true }
                                    }
                                }
                            },
                            onEditProfile = {
                                // Navigasi ke edit profile (jika kamu punya)
                                // navController.navigate("edit_profile")
                            }
                        )
                    }

                    else -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Gagal memuat profil", color = Color.Red)
                        }
                    }
                }
            }


            composable(AppRoute.ADD_PROJECT) {
                AddProjectScreen(onSubmit = { navController.popBackStack() }, navController = navController,)
            }

            composable("projects/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
                if (projectId != null) {
                    DashboardScreen(
                        projectId = projectId,
                        onAddTaskClick = { navController.navigate("add_task/$projectId") },
                        onTaskClick = { taskId ->
                            navController.navigate("tasks/$taskId/detail")
                        },
                        onNavigateToNotifications = {
                            navController.navigate("notifications")
                        },
                         onEditProjectClick = { id ->
                        navController.navigate("edit_project/$id")
                         } )  // ✅ pergi ke screen edit
                    }

                }



            composable("add_task/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
                if (projectId != null) {
                    AddTaskScreen(
                        projectId = projectId,
                        onSubmit = { navController.popBackStack() },
                        navController = navController
                    )
                }
            }

            composable("tasks/{taskId}/detail") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
                if (taskId != null) {
                    TaskDetailScreen(
                        taskId = taskId,
                        navController = navController,
                        onBackClick = { navController.popBackStack() } // ✅ balik ke halaman sebelumnya
                    )
                }
            }

            // Dosen Routes

            composable(AppRoute.DOSENHOME) {
                DosenDashboardScreen(
                    onNavigateToNotifications = {
                        navController.navigate("notifications")
                    }
                )
            }



            composable("dosen_reports") { DosenProgressReportScreen(navController = navController,                    onBackClick = { navController.navigate(AppRoute.HOME)},
            ) }



            composable("report_detail/{reportId}") { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId")?.toIntOrNull()
                reportId?.let {
                    ReportDetailScreen(reportId = it, navController = navController, onBackClick = { navController.popBackStack() }) // ✅ kirim navController
                }
            }


            composable("notifications") {
                NotificationScreen(
                    onNavigateToReportDetail = { reportId ->
                        navController.navigate("report_detail/$reportId")
                    }
                    , navController = navController
                )
            }

            composable(route = "pdf_viewer/{fileUrl}",
                arguments = listOf(navArgument("fileUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val fileUrl = backStackEntry.arguments?.getString("fileUrl") ?: ""
                PdfViewerScreen(
                    fileUrl = Uri.decode(fileUrl),
                    navController = navController
                )
            }


            composable("edit_project/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toIntOrNull()
                if (projectId != null) {
                    EditProjectScreen(
                        navController = navController,
                        projectId = projectId
                    )
                }
            }



        }
    }
}

