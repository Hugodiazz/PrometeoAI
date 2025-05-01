package com.hdev.prometeoai.View

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hdev.prometeoai.ViewModel.ResumirViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ReescribirScreen(
    viewModel: ResumirViewModel = viewModel()
){
    val opciones: Map<String, List<String>> = mapOf(
        "Tono" to listOf("Amigable", "Motivador", "Neutro", "Reflexivo", "Persuasivo"),
        "Complejidad" to listOf("Sencillo", "Intermedio", "Avanzado", "Apto para niños"),
        "Estilo" to listOf("Académico", "Creativo", "Informal", "Jurídico", "Literario", "Narrativo","Periodístico", "Profesional","Técnico"),
    )
    val mensajes by viewModel.messages.collectAsState()

    val seleccionados by viewModel.seleccionados.collectAsState()

    ChatScreen(
        text = "Bienvenido",
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