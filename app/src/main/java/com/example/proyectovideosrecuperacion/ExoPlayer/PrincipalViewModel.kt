package com.example.proyectovideosrecuperacion.ExoPlayer

import android.content.ContentResolver
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.proyectovideosrecuperacion.BBDD.BBDD.ListaVideos
import com.example.proyectovideosrecuperacion.BBDD.Video
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrincipalViewModel : ViewModel() {

    // Estado del reproductor ExoPlayer
    private val _exoPlayer: MutableStateFlow<ExoPlayer?> = MutableStateFlow(null)
    val exoPlayer = _exoPlayer.asStateFlow()

    // Estado del modo de bucle
    private val _isLoopMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoopMode = _isLoopMode.asStateFlow()

    // Estado del modo de reproducción aleatoria
    private val _isRandomMode: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRandomMode = _isRandomMode.asStateFlow()

    // Índice del video actual
    private val _currentVideoIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentVideoIndex = _currentVideoIndex.asStateFlow()

    /*
    * private val _songList = MutableStateFlow(mutableStateListOf<Song>())
    val songList = _songList.asStateFlow()
    * */

    // Información del video actual
    private val _actual = MutableStateFlow(ListaVideos[_currentVideoIndex.value].video)
    val actual = _actual.asStateFlow()

    /*
    * private val _currentSong = MutableStateFlow(mutableStateOf<Song?>(null))
    val currentSong = _currentSong.asStateFlow()
    * */

    // Duración total del video
    private val _duracion = MutableStateFlow(0)
    val duracion = _duracion.asStateFlow()

    // Progreso actual de reproducción
    private val _progreso = MutableStateFlow(0)
    val progreso = _progreso.asStateFlow()


    // Crear el reproductor ExoPlayer
    fun crearExoPlayer(context: Context) {
        _exoPlayer.value = ExoPlayer.Builder(context).build()
        _exoPlayer.value!!.prepare()
        _exoPlayer.value?.playWhenReady = true
    }

    /*
    * fun updateProgress() {
        viewModelScope.launch {
            while(isActive){
                _progress.value = _exoPlayer.value!!.currentPosition.toInt().toLong()
                delay(1000)
            }
        }
    }
    * */

    // Iniciar la reproducción del video
    fun hacerReproducirVideo(context: Context) {
        val mediaItem = MediaItem.fromUri(obtenerRuta(context, _currentVideoIndex.value))

        _exoPlayer.value!!.setMediaItem(mediaItem)
        _exoPlayer.value!!.prepare()
        _exoPlayer.value!!.playWhenReady = true
    }

    // Manejar el cambio de estado de reproducción
    private fun handlePlaybackStateChanged(playbackState: Int, context: Context) {
        if (playbackState == Player.STATE_READY) {
            _duracion.value = (_exoPlayer.value!!.duration / 1000).toInt()
            viewModelScope.launch {
                while (_exoPlayer.value!!.isPlaying) {
                    _progreso.value = (_exoPlayer.value!!.currentPosition / 1000).toInt()
                    delay(1000)
                }
            }
        } else if (playbackState == Player.STATE_ENDED) {
            playNext(context)
        }
    }

    // Obtener la ruta del video
    private fun obtenerRuta(context: Context, videoIndex: Int): String {
        val currentVideo = ListaVideos[videoIndex].video

        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.packageName + '/' + context.resources.getResourceTypeName(currentVideo) + '/' +
                context.resources.getResourceEntryName(currentVideo)
    }

    // Obtener el video actual
    fun getCurrentVideo(): Video {
        return ListaVideos[_currentVideoIndex.value]
    }

    // Obtener el índice del siguiente video
    fun getNextVideoIndex(): Int {
        return if (_isRandomMode.value) {
            (0 until ListaVideos.size).random()
        } else {
            (_currentVideoIndex.value + 1) % ListaVideos.size
        }
    }

    // Alternar el modo de bucle
    fun toggleLoopMode(/*repeat: Boolean*//*esta variable es de el, si no funciona quitarla*/) {
        /*if (repeat) {
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_ONE
        }
        else {
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_OFF
        }*/

        _isLoopMode.value = !_isLoopMode.value

        if (_isLoopMode.value) {
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_ONE
        } else {
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_OFF
        }

        if (_isLoopMode.value) {
            _isRandomMode.value = false
        }
    }

    // Alternar el modo de reproducción aleatoria
    fun toggleRandomMode() {
        _isRandomMode.value = !_isRandomMode.value

        if (_isRandomMode.value) {
            _exoPlayer.value?.shuffleModeEnabled = true
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_OFF
        } else {
            _exoPlayer.value?.shuffleModeEnabled = false
        }

        if (_isRandomMode.value) {
            _isLoopMode.value = false
        }
    }

    // Establecer el índice del video actual
    fun setCurrentVideoIndex(index: Int) {
        _currentVideoIndex.value = index
    }

    // Inicializar el ExoPlayer y añadir un listener
    fun inicializarEP(context: Context) {
        _exoPlayer.value = ExoPlayer.Builder(context).build()
        _exoPlayer.value!!.prepare()
        _exoPlayer.value!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                handlePlaybackStateChanged(playbackState, context)
            }
        })
    }

    // Liberar recursos cuando se destruye el ViewModel
    override fun onCleared() {
        _exoPlayer.value?.release()
        super.onCleared()
    }

    // Pausar o reanudar la reproducción de video
    fun pausarOSeguirVideo(): Boolean {
        /*if (!_exoPlayer.value!!.isPlaying) {
            _exoPlayer.value!!.play()
        } else {
            _exoPlayer.value!!.pause()
        }*/

        val exoPlayer = _exoPlayer.value

        if (exoPlayer != null) {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
                println("Video pausado")
                return true
            } else {
                exoPlayer.play()
                println("Video reanudado")
                return true
            }
        } else {
            println("ExoPlayer no inicializado")
        }

        return false
    }

    // Reproducir el video anterior
    fun playPrevious(context: Context) {
        setCurrentVideoIndex((_currentVideoIndex.value - 1 + ListaVideos.size) % ListaVideos.size)

        _actual.value = ListaVideos[_currentVideoIndex.value].video

        val mediaItem = MediaItem.fromUri(obtenerRuta(context, _currentVideoIndex.value))
        _exoPlayer.value?.setMediaItem(mediaItem)
        _exoPlayer.value?.prepare()

        if (_isLoopMode.value) {
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_ONE
        }

        _exoPlayer.value?.playWhenReady = true
    }

    /*
    * Esto es el playNext y playPrevious junto
    *
    * fun changeSong(context: Context, type: String, direction: String) {
        val random = Random
        _exoPlayer.value!!.stop()
        _exoPlayer.value!!.clearMediaItems()
        //_progress.value = 0
        //_exoPlayer.value!!.prepare()

        if (type == "normal") {
            if (direction == "next") {
                if (_songList.value.indexOf(_currentSong.value) == _songList.value.size - 1) {
                    _currentSong.value = _songList.value[0]
                }
                else {
                    _currentSong.value = _songList.value[_songList.value.indexOf(_currentSong.value) + 1]
                }
            }
            else {
                if (_songList.value.indexOf(_currentSong.value) == 0) {
                    _currentSong.value = _songList.value[_songList.value.size - 1]
                }
                _currentSong.value = _songList.value[_songList.value.indexOf(_currentSong.value) - 1]
            }
            val mediaItem = fromUri((_currentSong.value as Song).getLink())
            _exoPlayer.value!!.setMediaItem(mediaItem)
        }
        else {
            _currentSong.value = _songList.value[random.nextInt(0, _songList.value.size)]
            val mediaItem = fromUri((_currentSong.value as Song).getLink())
            _exoPlayer.value!!.setMediaItem(mediaItem)
        }

        _exoPlayer.value!!.prepare()
        _exoPlayer.value!!.playWhenReady = true

    }
    * */

    // Reproducir el siguiente video
    fun playNext(context: Context) {
        val nextVideoIndex = if (_isRandomMode.value) {
            (0 until ListaVideos.size).random()
        } else {
            (_currentVideoIndex.value + 1) % ListaVideos.size
        }

        setCurrentVideoIndex(nextVideoIndex)

        _actual.value = ListaVideos[_currentVideoIndex.value].video

        val mediaItem = MediaItem.fromUri(obtenerRuta(context, _currentVideoIndex.value))
        _exoPlayer.value?.setMediaItem(mediaItem)
        _exoPlayer.value?.prepare()

        if (_isRandomMode.value) {
            _exoPlayer.value?.shuffleModeEnabled = true
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_OFF
        }

        if (_isLoopMode.value) {
            _exoPlayer.value?.repeatMode = Player.REPEAT_MODE_ONE
        }

        _exoPlayer.value?.playWhenReady = true
    }

    // Saltar a una posición específica del video
    fun seekTo(positionMillis: Long) {
        _exoPlayer.value?.seekTo(positionMillis)

        /* Esto seria el sekkTo que tenemos?

    * fun setNewProgress(newProgress: Long) {
        _progress.value = newProgress
        //_progress.value = _exoPlayer.value!!.currentPosition.toInt().toLong()
        _exoPlayer.value?.seekTo(_progress.value * 1000)

    }
    * */
    }

    // Reproducir un video en específico (para el search bar)
    fun playVideo(context: Context, videoIndex: Int) {
        setCurrentVideoIndex(videoIndex)

        val mediaItem = MediaItem.fromUri(obtenerRuta(context, videoIndex))
        _exoPlayer.value?.setMediaItem(mediaItem)
        _exoPlayer.value?.prepare()
        _exoPlayer.value?.playWhenReady = true
        _actual.value = ListaVideos[_currentVideoIndex.value].video

        /*
    * fun playMusic(context: Context, songs: List<Song>, song: Song) {
            _exoPlayer.value!!.stop()
            _exoPlayer.value!!.clearMediaItems()
            _exoPlayer.value?.prepare()
            _songList.value.clear()
            _songList.value.addAll(songs)
            _currentSong.value = song
            println("Link: " + (_currentSong.value as Song).getLink())
            val mediaItem = fromUri(((_currentSong.value as Song).getLink()))
            _exoPlayer.value?.setMediaItem(mediaItem)
            _exoPlayer.value!!.playWhenReady = true
            _exoPlayer.value!!.addListener(object : Player.Listener{
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            // El Player está preparado para empezar la reproducción.
                            // Si playWhenReady es true, empezará a sonar la música.
                            updateProgress()
                            _duration.value = _exoPlayer.value!!.duration
                            println("Duracion: " + _duration.value)
                        }
                        Player.STATE_BUFFERING -> {
                            // El Player está cargando el archivo, preparando la reproducción.
                            // No está listo, pero está en ello.
                        }
                        Player.STATE_ENDED -> {
                            // El Player ha terminado de reproducir el archivo.
                            changeSong(context, "normal", "next")

                        }
                        Player.STATE_IDLE -> {
                            // El player se ha creado, pero no se ha lanzado la operación prepared.
                        }
                    }

                }
            })
    }*/
    }


}