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

    val mensajes by viewModel.messages.collectAsState()
    val opciones = viewModel.opciones
    val uiState by viewModel.uiState.collectAsState()
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
        uiState = uiState
    )
}