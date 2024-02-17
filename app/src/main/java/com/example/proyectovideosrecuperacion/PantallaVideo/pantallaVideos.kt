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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Slider
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
import androidx.compose.ui.platform.LocalContext
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
fun pantallaVideos(navController: NavHostController, ExoPlayer: PrincipalViewModel = viewModel(), nombre:String?) {
    var isPlaying by remember { mutableStateOf(false) }
    var isTextVisible by remember { mutableStateOf(true) }
    val contexto = LocalContext.current
    val obtenerIndice = obtenerVideo(nombre)
    val currentSongIndex by ExoPlayer.currentVideoIndex.collectAsState(obtenerIndice)
    
    DisposableEffect(currentSongIndex) {
        if (currentSongIndex != null) {
            // Lógica para reproducir el video seleccionado
            ExoPlayer.playVideo(contexto, currentSongIndex!!)
            isPlaying = true
        }
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
                    text = ExoPlayer.getCurrentVideo().name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // Modifica la llamada a obtenerRuta en pantallaVideos
                VideoView(
                    videoUri = obtenerRuta(obtenerVideoPorIndice(obtenerIndice)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .padding(vertical = 16.dp),
                )

                // Slider
                val progreso = ExoPlayer.progreso.collectAsState().value.toFloat()
                val duracionTotal = ExoPlayer.duracion.collectAsState().value.toFloat()

                Slider(
                    value = ExoPlayer.progreso.collectAsState().value.toFloat(),
                    onValueChange = {
                        ExoPlayer.seekTo(it.toLong() * 1000)
                    },
                    valueRange = 0f..ExoPlayer.duracion.collectAsState().value.toFloat(),
                    steps = 100,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .fillMaxWidth()
                        .widthIn(0.dp, 300.dp)
                )

                // Textos de los tiempos para mostrar el progreso actual y la duración total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatTime(progreso.toLong()))
                    Text(text = formatTime(duracionTotal.toLong()))
                }

                // Botones de acción: pausa, avance, retroceder, bucle y random
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            ExoPlayer.toggleRandomMode()
                        }
                    ) {
                        val icon = if (ExoPlayer.isRandomMode.collectAsState().value) {
                            Icons.Default.Replay
                        } else {
                            Icons.Default.Shuffle
                        }
                        Icon(imageVector = icon, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            ExoPlayer.playPrevious(contexto)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.SkipPrevious, contentDescription = null)
                    }

                    // Botón de pausa y reproducción
                    IconButton(
                        onClick = {
                            isPlaying = ExoPlayer.pausarOSeguirVideo()
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
                            ExoPlayer.playNext(contexto)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.SkipNext, contentDescription = null)
                    }

                    IconButton(
                        onClick = {
                            ExoPlayer.toggleLoopMode()
                        }
                    ) {
                        val icon = if (ExoPlayer.isLoopMode.collectAsState().value) {
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

fun obtenerRuta(videoResource: Video): Uri {
    val uri = Uri.parse("android.resource://com.example.proyectovideosrecuperacion/raw/${videoResource.video}")
    Log.d("VideoDebug", "Video URI: $uri")
    return uri
}

// Función auxiliar para formatear el tiempo en formato MM:SS
@Composable
fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

@Composable
fun VideoView(videoUri: Uri, modifier: Modifier) {
    AndroidView(factory = { context ->
        android.widget.VideoView(context).apply {
            setVideoURI(videoUri)
            start() // Inicia la reproducción automáticamente
        }
    }, modifier = modifier)
}


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

fun obtenerVideoPorIndice(indice: Int): Video {
    return ListaVideos.getOrElse(indice) {
        // Si el índice está fuera del rango de la lista, devuelve un valor predeterminado o lanza una excepción según tus necesidades
        throw IndexOutOfBoundsException("Índice fuera de rango: $indice")
    }
}

/*
* Column(
            modifier = Modifier
                .width(380.dp)
                .height(70.dp), Arrangement.SpaceAround, Alignment.CenterHorizontally
        ) {
            if (exoPlayer != null) {
                if (exoPlayer.duration > -1) {
                    Slider(
                        value = progress.toFloat()/1000,
                        onValueChange = {
                            exoPlayerViewModel.setNewProgress(it.toLong())
                        },
                        valueRange = 0f..(duration/ 1000).toFloat(),
                        steps = (duration / 1000).toInt(),
                        colors = SliderDefaults.colors(
                            thumbColor = colorResource(id = R.color.dark_orange),
                            activeTrackColor = colorResource(id = R.color.light_orange),
                            inactiveTrackColor = Color.Gray
                        )
                    )
                }
            }
            Row(
                modifier = Modifier.width(380.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                Text(text = songViewModel.formatDuration(progress))
                if (exoPlayer != null) {
                    Text(text = songViewModel.formatDuration(duration))
                }
            }
        }
        Row(
            Modifier
                .width(380.dp)
                .height(150.dp), Arrangement.SpaceBetween, Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                songViewModel.setNewShuffleState(!shuffleState)
                if (shuffleState) {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Shuffle mode activated",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                } else {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Shuffle mode deactivated",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }

            }) {
                Icon(
                    painter = painterResource(
                        if (shuffleState) {
                            R.drawable.baseline_shuffle_on_24
                        } else {
                            R.drawable.baseline_shuffle_24
                        }
                    ),
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier.width(200.dp),
                Arrangement.SpaceBetween,
                Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    exoPlayerViewModel.changeSong(applicationContext, "normal", "prev")
                    println("Current: " + currentSong.value.toString())
                    if (currentSong.value != null) {
                        song = currentSong.value
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_skip_previous_24),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = {
                        if (exoPlayer != null) {
                            exoPlayerViewModel.playOrPause()
                            songViewModel.setNewPlayState(exoPlayer.isPlaying)
                        }
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            if (playState) {
                                R.drawable.baseline_pause_circle_outline_24
                            } else {
                                R.drawable.baseline_play_circle_outline_24
                            }
                        ),
                        contentDescription = null
                    )
                }
                IconButton(onClick = {
                    exoPlayerViewModel.changeSong(applicationContext, "normal", "next")
                    println("Current: " + currentSong.value.toString())
                    if (currentSong.value != null) {
                        println("Current dentro del if: " + currentSong.value.toString())
                        song = currentSong.value
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_skip_next_24),
                        contentDescription = null
                    )
                }
            }
            IconButton(onClick = {
                songViewModel.setNewLoopState(!loopState)
                if (loopState) {
                    exoPlayerViewModel.setLoopMode(true)
                    val toast = Toast.makeText(
                        applicationContext,
                        "Loop mode activated",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                } else {
                    exoPlayerViewModel.setLoopMode(false)
                    val toast = Toast.makeText(
                        applicationContext,
                        "Loop mode deactivated",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_loop_24),
                    contentDescription = null
                )
            }
        }
* */