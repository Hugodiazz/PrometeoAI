package com.hdev.prometeoai.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.hdev.prometeoai.BuildConfig
import com.hdev.prometeoai.Model.Mensaje
import com.hdev.prometeoai.Model.Rol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    init {
        viewModelScope.launch {
            _messages.update { it + Mensaje(texto = "Bienvenido al modo de reescritura \nIngresa un texto y presiona enviar o configura los parametros en el icono ⚙\uFE0F", rol = Rol.IA) }
        }

    }
    fun resumirTexto(texto: String){
        viewModelScope.launch {
            try {
                val prompt = CrearPrompt(texto)
                chat.history.add(content(role = "user") { text(prompt) })
                /*
                generativeModel.countTokens(prompt).let { tokenCount ->
                    println("Token count: ${tokenCount.totalTokens}")
                }
                 */

                _messages.update { it + Mensaje(texto = prompt, rol = Rol.USER) }

                val iaResponse = chat.sendMessage(content(role = "user") { text(prompt) })
                _messages.update { it + Mensaje(texto = iaResponse.text.toString(), rol = Rol.IA) }
                // solicitudes de hoy
            }catch (e: Exception) {
                _messages.update { it + Mensaje(texto = "Error al generar respuesta, vuelve a intentarlo", rol = Rol.IA) }
            }
        }
    }

    fun CrearPrompt(texto: String): String{
        //Tono, Complejidad, estilo, longitud
        val opciones = obtenerSeleccionados()

        if (opciones.isEmpty()) {
            return "Reescribe el siguiente texto: $texto"
        }

        var prompt = "Reescribe el siguiente texto: ${texto} \n" + "Ten en cuenta los siguientes aspectos:"

        if (!opciones[0].isNullOrEmpty()){
            prompt += "\n* Debe ser redactado con un tono ${opciones[0]} "
        }
        if (!opciones[1].isNullOrEmpty()){
            prompt += "\n* La complejidad del texto de ser ${opciones[1]} "
        }
        if (!opciones[2].isNullOrEmpty()){
            prompt += "\n* El estilo del texto de ser ${opciones[2]} "
        }
        return prompt
    }
    /*
    Funcion de prueba
    fun resumirTexto(texto: String){
        viewModelScope.launch {
            try{
                //Imprime las opciones
                chat.history.add(content(role = "user") { text(imprimirSeleccionados()) })
                _messages.update { it + Mensaje(texto = imprimirSeleccionados(), rol = Rol.USER) }
                
            }catch (e: Exception){
                _messages.update { it + Mensaje(texto = "Error al generar respuesta, vuelve a intentarlo", rol = Rol.IA) }
            }
        }
    }

     */

    fun obtenerSeleccionados(): List<String>  {
        var opciones :MutableList<String> = mutableListOf()
        for ((grupo, opcion) in _seleccionados.value) {
            opciones.add(opcion ?: "")
        }
        return opciones

    }

    fun seleccionarOpcion(grupo: String, opcion: String?) {
        _seleccionados.value = _seleccionados.value.toMutableMap().apply {
            this[grupo] = opcion
        }
    }
}