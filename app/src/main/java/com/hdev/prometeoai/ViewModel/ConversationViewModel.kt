package com.hdev.prometeoai.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.hdev.prometeoai.BuildConfig
import com.hdev.prometeoai.Model.Mensaje
import com.hdev.prometeoai.Model.Rol
import com.hdev.prometeoai.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationViewModel: ViewModel(){
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-lite",
        apiKey = BuildConfig.apiKey
    )

    private var chat = generativeModel.startChat(history = mutableListOf())
    private val _messages = MutableStateFlow<List<Mensaje>>(emptyList())
    val messages: StateFlow<List<Mensaje>> get() = _messages

    init {
        viewModelScope.launch {
            _messages.update { it + Mensaje(texto = "Bienvenido al modo de Chat \uD83E\uDD16", rol = Rol.IA) }
        }
    }
    fun conversar(texto: String){
        viewModelScope.launch {
            try {
                _messages.update { it + Mensaje(texto = texto, rol = Rol.USER) }
                _uiState.value = UiState.Loading
                chat.history.add(content(role = "user") { text(texto) })

//                generativeModel.countTokens(texto).let { tokenCount ->
//                    println("Token count: ${tokenCount.totalTokens}")
//                }
                val iaResponse = chat.sendMessage(content(role = "user") { text(texto) })
                //val iaResponse = generativeModel.generateContent(prompt)
                _messages.update { it + Mensaje(texto = iaResponse.text.toString(), rol = Rol.IA) }
                // solicitudes de hoy
                _uiState.value = UiState.Success
            }catch (e: Exception) {
                _uiState.value = UiState.Error
                _messages.update { it + Mensaje(texto = "Error al generar respuesta, vuelve a intentarlo: ${e}", rol = Rol.IA) }
            }
        }
    }
}