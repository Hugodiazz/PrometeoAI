package com.hdev.prometeoai.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.hdev.prometeoai.BuildConfig
import com.hdev.prometeoai.Model.Mensaje
import com.hdev.prometeoai.Model.Rol
import com.hdev.prometeoai.UiState
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

    val opciones: Map<String, List<String>> = mapOf(
        "Nivel de comprensión" to listOf("Extremo", "Moderado", "Detallado"),
        "Enfoque" to listOf("General", "Por sección", "Por párrafo"),
        "Estructura" to listOf("Viñetas", "Párrafo continuo", "Jerárquica"),
        "Longitud" to listOf("5 palabras clave", "Breve","Media", "Extensa")
    )

    private val _messages = MutableStateFlow<List<Mensaje>>(emptyList())
    val messages: StateFlow<List<Mensaje>> get() = _messages

    private val _seleccionados = MutableStateFlow<Map<String, String?>>(
        emptyMap()
    )
    val seleccionados: StateFlow<Map<String, String?>> = _seleccionados

    init {
        viewModelScope.launch {
            _messages.update { it + Mensaje(texto = "Bienvenido al modo de RESUMEN \nIngresa un texto y presiona enviar o configura los parametros en el icono ⚙\uFE0F", rol = Rol.IA) }
        }
    }

    // Función para hacer llamado a la API, solicitando un resumen.
    fun resumirTexto(texto: String){
        viewModelScope.launch {
            try {
                val prompt = CrearPrompt(texto)
                _messages.update { it + Mensaje(texto = prompt, rol = Rol.USER) }
                _uiState.value = UiState.Loading

                val iaResponse = generativeModel.generateContent(prompt)
                _messages.update { it + Mensaje(texto = iaResponse.text.toString(), rol = Rol.IA) }

                _uiState.value = UiState.Success
            }catch (e: Exception) {
                _uiState.value = UiState.Error
                _messages.update { it + Mensaje(texto = "Error al generar respuesta, vuelve a intentarlo: ${e}", rol = Rol.IA) }
            }
        }
    }

    fun CrearPrompt(texto: String): String{
        //Nivel de comprensión, Enfoque, Estructura, longitud
        val opciones = obtenerSeleccionados()

        if (opciones.all { it.isEmpty() }) {
            return "Resume el siguiente tema: $texto"
        }

        var prompt = "Resume el siguiente texto: ${texto} \n" + "Ten en cuenta los siguientes aspectos:"

        if (opciones[0].isNotEmpty()){
            prompt += "\n* Debe ser resumido con un nivel de comprensión ${opciones[0]} del original"
        }
        if (opciones[1].isNotEmpty()){
            prompt += "\n* El enfoque del resumen debe ser ${opciones[1]} "
        }
        if (opciones[2].isNotEmpty()){
            prompt += "\n* La estructura debe ser ${opciones[2]} "
        }
        if (opciones[3].isNotEmpty()){
            prompt += "\n* La longitud debe ser ${opciones[3]} "
        }
        return prompt
    }

    private fun obtenerSeleccionados(): List<String>  {
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