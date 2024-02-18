package com.example.proyectovideosrecuperacion.PantallaVideo

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proyectovideosrecuperacion.BBDD.BBDD.ListaVideos
import com.example.proyectovideosrecuperacion.BBDD.Video
import com.example.proyectovideosrecuperacion.ExoPlayer.PrincipalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pantallaVideos(navController: NavHostController, exoPlayer: PrincipalViewModel = viewModel(), nombre:String?) {
    //Variables de estado
    var isPlaying by remember { mutableStateOf(false) }
    var isTextVisible by remember { mutableStateOf(true) }
    val obtenerIndice = obtenerVideo(nombre)
    var isRandomMode by remember { mutableStateOf(false) }
    var isLoopMode by remember { mutableStateOf(false) }
    val currentSongIndex by exoPlayer.currentVideoIndex.collectAsState(obtenerIndice)

    //El DisposableEffect se utiliza para realizar acciones cuando currentSongIndex cambia.
    //Si currentSongIndex no es nulo, se establece isPlaying en true, indicando que la reproducción está activa.
    DisposableEffect(currentSongIndex) {
        if (currentSongIndex != null) {
            isPlaying = true
        }
        //El bloque onDispose está vacío, no se realizan acciones específicas al deshacer el efecto.
        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isTextVisible) {
                        Text(text = "Video")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("pantallaInicio") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {}
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                // Mostrar información de la canción arriba de la imagen
                Text(
                    text = "Ahora reproduciendo: ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(6.dp)
                )

                Text(
                    text = "Videos de Kingdom Hearts 1, 2, 3, 365 Days y Birth of Sleep",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                //Modifica la llamada a obtenerRuta en pantallaVideos
                VideoView(
                    videoUri = obtenerRuta(obtenerVideoPorIndice(obtenerIndice)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .padding(vertical = 16.dp),
                )

                // Botones de acción: pausa, bucle y random (NO FUNCIONALES, SIMPLE DECORACIÓN)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            isRandomMode = !isRandomMode
                        }
                    ) {
                        val icon = if (isRandomMode) {
                            Icons.Default.Replay
                        } else {
                            Icons.Default.Shuffle
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            isPlaying = !isPlaying
                        }
                    ) {
                        val icon = if (isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            isLoopMode = !isLoopMode
                        }
                    ) {
                        val icon = if (isLoopMode) {
                            Icons.Default.RepeatOne
                        } else {
                            Icons.Default.Repeat
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }
                }
            }
        }
    )
}
//Funcion para obtener la ruta de Video y luego mostrarlo arriba en videoView
fun obtenerRuta(videoResource: Video): Uri {
    val uri = Uri.parse("android.resource://com.example.proyectovideosrecuperacion/raw/${videoResource.video}")
    Log.d("VideoDebug", "Video URI: $uri")
    return uri
}
//Funcion de VideoView que luego se usa arriba
@Composable
fun VideoView(videoUri: Uri, modifier: Modifier) {
    AndroidView(factory = { context ->
        android.widget.VideoView(context).apply {
            setVideoURI(videoUri)
            start()
        }
    }, modifier = modifier)
}
//Funcion para obtener el video segun el nombre del video, los numeros representan el orden
fun obtenerVideo(nombre: String?): Int {
    val index = when (nombre) {
        "Kingdom Hearts 1 - Talaska Black x Leg Day" -> 0
        "Kingdom Hearts 3 - In the End" -> 1
        "Kingdom Hearts 365 - Reconnect" -> 2
        "Kingdom Hearts 2 - Save Me Skrillex" -> 3
        "Kingdom Hearts BS - The Darkness is coming" -> 4
        else -> throw IllegalArgumentException("Nombre de juego no válido: $nombre")
    }
    Log.d("VideoDebug", "Índice obtenido para $nombre: $index")
    return index
}
//Funcion para obtener el video por el indice
fun obtenerVideoPorIndice(indice: Int): Video {
    return ListaVideos.getOrElse(indice) {
        // Si el índice está fuera del rango de la lista, devuelve un valor predeterminado o lanza una excepción según tus necesidades
        throw IndexOutOfBoundsException("Índice fuera de rango: $indice")
    }
}