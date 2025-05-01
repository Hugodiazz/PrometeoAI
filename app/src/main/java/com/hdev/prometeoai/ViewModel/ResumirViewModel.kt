package com.hdev.prometeoai.ViewModel

import android.util.Log
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
import com.hdev.prometeoai.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResumirViewModel: ViewModel(){
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash-lite",
        apiKey = BuildConfig.apiKey
    )

    //private var chat = generativeModel.startChat(history = mutableListOf())

    val opciones: Map<String, List<String>> = mapOf(
        "Tono" to listOf("Amigable", "Motivador", "Neutro", "Reflexivo", "Persuasivo"),
        "Complejidad" to listOf("Sencillo", "Intermedio", "Avanzado", "Apto para niños"),
        "Estilo" to listOf("Académico", "Creativo", "Informal", "Jurídico", "Literario", "Narrativo","Periodístico", "Profesional","Técnico"),
    )

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
                _messages.update { it + Mensaje(texto = prompt, rol = Rol.USER) }
                _uiState.value = UiState.Loading
                //chat.history.add(content(role = "user") { text(prompt) })
                /*
                generativeModel.countTokens(prompt).let { tokenCount ->
                    println("Token count: ${tokenCount.totalTokens}")
                }
                 */

                //val iaResponse = chat.sendMessage(content(role = "user") { text(prompt) })
                val iaResponse = generativeModel.generateContent(prompt)
                _messages.update { it + Mensaje(texto = iaResponse.text.toString(), rol = Rol.IA) }
                // solicitudes de hoy
                _uiState.value = UiState.Success
            }catch (e: Exception) {
                _messages.update { it + Mensaje(texto = "Error al generar respuesta, vuelve a intentarlo: ${e}", rol = Rol.IA) }
            }
        }
    }

    fun CrearPrompt(texto: String): String{
        //Tono, Complejidad, estilo, longitud
        val opciones = obtenerSeleccionados()

        if (opciones.all { it.isEmpty() }) {
            return "Reescribe el siguiente texto: $texto"
        }

        var prompt = "Reescribe el siguiente texto: ${texto} \n" + "Ten en cuenta los siguientes aspectos:"

        if (opciones[0].isNotEmpty()){
            prompt += "\n* Debe ser redactado con un tono ${opciones[0]} "
        }
        if (opciones[1].isNotEmpty()){
            prompt += "\n* La complejidad del texto de ser ${opciones[1]} "
        }
        if (opciones[2].isNotEmpty()){
            prompt += "\n* El estilo del texto debe ser ${opciones[2]} "
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
        var opcionesnuevas :MutableList<String> = mutableListOf()

        for(i in opciones){
            val grupo = i.key
            val opcion = _seleccionados.value[grupo]
            if (opcion != null) {
                opcionesnuevas.add(opcion)
            }else{
                opcionesnuevas.add("")
            }
        }

        return opcionesnuevas

    }

    fun seleccionarOpcion(grupo: String, opcion: String?) {
        _seleccionados.value = _seleccionados.value.toMutableMap().apply {
            this[grupo] = opcion
        }
    }
}