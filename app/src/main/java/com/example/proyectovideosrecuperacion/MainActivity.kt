package com.example.proyectovideosrecuperacion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectovideosrecuperacion.PantallaPrincipal.pantallaInicio
import com.example.proyectovideosrecuperacion.PantallaVideo.pantallaVideos
import com.example.proyectovideosrecuperacion.ui.theme.ProyectoVideosRecuperacionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoVideosRecuperacionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // Configuración del sistema de navegación
                    SetupNavigation(navController)
                }
            }
        }
    }
}

//Funcion de navegacion, para poder navegear de pantalla en pantalla
@Composable
fun SetupNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "pantallaInicio") {
        composable("pantallaInicio") {
            pantallaInicio(navController)
        }

        composable(
            "pantallaVideos/{nombre}",
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")
            pantallaVideos(navController, ExoPlayer = viewModel(), nombre)
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoVideosRecuperacionTheme {
        Greeting("Android")
    }
}