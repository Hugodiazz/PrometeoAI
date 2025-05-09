package com.hdev.prometeoai.View

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hdev.prometeoai.ViewModel.ConversationViewModel

@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = viewModel()
){

    val mensajes by viewModel.messages.collectAsState()
    //opciones vacias
    val opciones : Map<String, List<String>> = emptyMap()
    val uiState by viewModel.uiState.collectAsState()
    val seleccionados : Map<String, String?> = emptyMap()

    ChatScreen(
        text = "Bienvenido",
        mensajes = mensajes,
        onSendMessage = { texto ->
            viewModel.conversar(texto)
        },
        opciones = opciones,
        seleccionados = seleccionados,
        onSeleccion = { grupo, opcion -> {}},
        uiState = uiState
    )
}