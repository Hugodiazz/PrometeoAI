package com.hdev.prometeoai.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.hdev.prometeoai.BuildConfig
import com.hdev.prometeoai.Model.Mensaje
import com.hdev.prometeoai.Model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResumirViewModel: ViewModel(){
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-lite",
        apiKey = BuildConfig.apiKey
    )
    private var chat = generativeModel.startChat(history = mutableListOf())

    private val _messages = MutableStateFlow<List<Mensaje>>(emptyList())
    val messages: StateFlow<List<Mensaje>> get() = _messages

    private val _seleccionados = MutableStateFlow<Map<String, String?>>(
        emptyMap()
    )
    val seleccionados: StateFlow<Map<String, String?>> = _seleccionados

    fun resumirTexto(texto: String){
        viewModelScope.launch {
            try {
                chat.history.add(content(role = "user") { text(texto) })
                _messages.update { it + Mensaje(texto = texto, rol = Rol.USER) }

                val iaResponse = chat.sendMessage(content(role = "user") { text(texto) })
                _messages.update { it + Mensaje(texto = iaResponse.text.toString(), rol = Rol.IA) }
            }catch (e: Exception) {
                _messages.update { it + Mensaje(texto = "Error al generar respuesta, vuelve a intentarlo", rol = Rol.IA) }
            }
        }
    }

    fun inicializarSeleccion(opciones: Map<String, List<String>>) {
        // Solo inicializa si aún no hay selección
        if (_seleccionados.value.isEmpty()) {
            _seleccionados.value = opciones.mapValues { it.value.firstOrNull() }
        }
    }

    fun seleccionarOpcion(grupo: String, opcion: String?) {
        _seleccionados.value = _seleccionados.value.toMutableMap().apply {
            this[grupo] = opcion
        }
    }
}