package com.example.proyectovideosrecuperacion.BBDD

import com.example.proyectovideosrecuperacion.R


data class Video(
    val name: String,
    val coverResourceId: Int,
    val video: Int,
    val duracion: String,
)

object BBDD {
    val ListaVideos: List<Video> = listOf(
        Video( "Kingdom Hearts 1 - Talaska Black x Leg Day", R.drawable.kingdomhearts, R.raw.kingdomheartstaskablackxlegday, "03:39"),
        Video( "Kingdom Hearts 3 - In the End", R.drawable.kingdom_hearts_3,R.raw.kingdomheartsintheend,  "03:02"),
        Video( "Kingdom Hearts 365 - Reconnect", R.drawable.kingdom_hearts_365,R.raw.kingdomheartsreconnect,   "05:18"),
        Video( "Kingdom Hearts 2 - Save Me Skrillex", R.drawable.kingdomhearts2,R.raw.kingdomheartssavemeskrillex,   "3:43"),
        Video( "Kingdom Hearts BS - The Darkness is coming", R.drawable.kingdomheartssleep,R.raw.kingdomheartsthedarknessiscoming,   "03:28")
    )
}