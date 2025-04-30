package com.hdev.prometeoai.View

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hdev.prometeoai.ViewModel.ResumirViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResumirScreen(
    viewModel: ResumirViewModel = viewModel()
){
    val opciones: Map<String, List<String>> = mapOf(
        "Tono" to listOf("Ninguno", "Amigable", "Motivador", "Neutro", "Reflexivo", "Persuasivo"),
        "Complejidad" to listOf("Ninguno", "Sencillo", "Intermedio", "Avanzado", "Apto para niños"),
        "Estilo" to listOf("Ninguno", "Académico", "Creativo", "Informal", "Jurídico", "Literario", "Narrativo","Periodístico", "Profesional","Técnico"),
    )
    val mensajes by viewModel.messages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.inicializarSeleccion(opciones)
    }

    val seleccionados by viewModel.seleccionados.collectAsState()

    ChatScreen(
        text = "Estas en el modo resumir, por favor ingresa el texto que deseas resumir y ajusta los parametros.",
        mensajes = mensajes,
        onSendMessage = { texto ->
            viewModel.resumirTexto(texto)
        },
        opciones = opciones,
        seleccionados = seleccionados,
        onSeleccion = { grupo, opcion ->
            viewModel.seleccionarOpcion(grupo, opcion)
        },
    )
}