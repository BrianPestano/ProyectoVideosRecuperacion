package com.example.proyectovideosrecuperacion.ExoPlayer

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/*
  Clase ViewModel, antes estaba lo necesario para los botones, pero nunca funcionaban con el video, si no con una 2 cancion/video fantasma..
* Asique se opto por quitar la mayoria y dejar lo esencial para que funcione sin los botones
*/
class PrincipalViewModel : ViewModel() {

    // Estado del reproductor ExoPlayer
    private val _exoPlayer: MutableStateFlow<ExoPlayer?> = MutableStateFlow(null)

    // Índice del video actual
    private val _currentVideoIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentVideoIndex = _currentVideoIndex.asStateFlow()

    // Liberar recursos cuando se destruye el ViewModel
    override fun onCleared() {
        _exoPlayer.value?.release()
        super.onCleared()
    }
}