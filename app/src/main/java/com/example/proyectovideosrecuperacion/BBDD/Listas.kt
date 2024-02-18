package com.example.proyectovideosrecuperacion.BBDD

import com.example.proyectovideosrecuperacion.R

//Clase Video donde se le pasan los parametros nombre,video y duracion
data class Video(
    val nombre: String,
    val video: Int,
    val duracion: String,
)
//Objeto donde le pasamos una lista de los videos con el nombre, el video y la duracion
object BBDD {
    val ListaVideos: List<Video> = listOf(
        Video( "Kingdom Hearts 1 - Talaska Black x Leg Day", R.raw.kingdomheartstaskablackxlegday, "03:39"),
        Video( "Kingdom Hearts 3 - In the End",R.raw.kingdomheartsintheend,  "03:02"),
        Video( "Kingdom Hearts 365 - Reconnect",R.raw.kingdomheartsreconnect,   "05:18"),
        Video( "Kingdom Hearts 2 - Save Me Skrillex",R.raw.kingdomheartssavemeskrillex,   "3:43"),
        Video( "Kingdom Hearts BS - The Darkness is coming",R.raw.kingdomheartsthedarknessiscoming,   "03:28")
    )
}