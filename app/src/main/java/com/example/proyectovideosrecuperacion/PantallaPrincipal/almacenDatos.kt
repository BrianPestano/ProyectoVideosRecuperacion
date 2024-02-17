package com.example.proyectovideosrecuperacion.PantallaPrincipal

import androidx.compose.runtime.mutableStateListOf
import com.example.proyectovideosrecuperacion.R
import com.example.proyectovideosrecuperacion.Videojuego.infoArray

var lista = mutableStateListOf(
    infoArray("Kingdom Hearts 1 - Talaska Black x Leg Day", R.drawable.kingdomhearts, "KH1", R.raw.kingdomheartstaskablackxlegday),
    infoArray("Kingdom Hearts 2 - Save Me Skrillex", R.drawable.kingdomhearts2, "KH2", R.raw.kingdomheartsintheend),
    infoArray("Kingdom Hearts 3 - In the End", R.drawable.kingdom_hearts_3, "KH3", R.raw.kingdomheartsreconnect),
    infoArray("Kingdom Hearts BS - The Darkness is coming", R.drawable.kingdomheartssleep, "KHBS", R.raw.kingdomheartssavemeskrillex),
    infoArray("Kingdom Hearts 365 - Reconnect", R.drawable.kingdom_hearts_365, "KH365", R.raw.kingdomheartsthedarknessiscoming)
)