package com.example.proyectovideosrecuperacion.PantallaPrincipal

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.proyectovideosrecuperacion.BBDD.BBDD
import com.example.proyectovideosrecuperacion.BBDD.Video
import com.example.proyectovideosrecuperacion.R
import com.example.proyectovideosrecuperacion.Videojuego.infoArray


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun pantallaInicio(navController: NavHostController) {

    // Variables de estado
    var buscador by remember { mutableStateOf("") }
    var seleccionJuego by remember { mutableStateOf<String?>(null) }
    var menuDesplegado by remember { mutableStateOf(false) }
    var VJSeleccionado by remember { mutableStateOf(mutableSetOf<String>()) }

    val juegos = listOf("KH1", "KH2", "KH365", "KHBS", "KH3")

    // Columna principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Barra de búsqueda y menú desplegable de juego
        SearchBar(
            query = buscador,
            onQueryChange = {
                buscador = it
                if (it.isEmpty()) {
                    menuDesplegado = false
                    seleccionJuego = null
                }
            },
            onSearch = {
                if (seleccionJuego == null) {
                    menuDesplegado = false
                }
            },
            active = menuDesplegado,
            onActiveChange = { menuDesplegado = !menuDesplegado }
        ) {
            // Crear elementos de menú desplegable
            juegos.forEach { juego ->
                if (juego.startsWith(buscador, ignoreCase = true)) {
                    DropdownMenuItem(
                        onClick = {
                            seleccionJuego = juego
                            buscador = juego
                            menuDesplegado = false
                        },
                        text = { Text(text = juego) }
                    )
                }
            }
        }

        // Mostrar lista de videojuegos según la plataforma seleccionada
        if (seleccionJuego != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(lista.filter { it.juegos.contains(seleccionJuego!!, ignoreCase = true) }) { videojuego ->
                    // Llamar a la función PlataformaItem para cada elemento de la lista
                    PlataformaItem(
                        plataforma = videojuego,
                        isChecked = videojuego.nombre in VJSeleccionado,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                VJSeleccionado.add(videojuego.nombre)
                            } else {
                                VJSeleccionado.remove(videojuego.nombre)
                            }
                        },
                        onClick = {
                            navController.navigate("pantallaVideos/${videojuego.nombre}")
                        }
                    )
                }
            }
        } else if (seleccionJuego == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(lista) { videojuego ->
                    // Llamar a la función PlataformaItem para cada elemento de la lista
                    PlataformaItem(
                        plataforma = videojuego,
                        isChecked = videojuego.nombre in VJSeleccionado,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                VJSeleccionado.add(videojuego.nombre)
                            } else {
                                VJSeleccionado.remove(videojuego.nombre)
                            }
                        },
                        onClick = {
                            navController.navigate("pantallaVideos/${videojuego.nombre}")
                            /*/${videojuego.nombre}*/
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlataformaItem(
    plataforma: infoArray,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (Int) -> Unit
) {

    // Card que contiene la información de un videojuego
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onClick(plataforma.video)
            },
        shape = RoundedCornerShape(8.dp)
    ) {
        // Row que contiene la información y la checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sección de la imagen y el nombre del videojuego
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
            ) {
                // Imagen del videojuego
                Image(
                    painter = painterResource(id = plataforma.imagenes),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )

                // Separador
                Spacer(modifier = Modifier.width(16.dp))

                // Nombre del videojuego
                Text(text = plataforma.nombre, style = MaterialTheme.typography.titleMedium)
            }

            // Separador
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}