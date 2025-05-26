package com.hdev.prometeoai.View

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.hdev.prometeoai.Model.Mensaje
import com.hdev.prometeoai.Model.Rol
import com.hdev.prometeoai.UiState
import com.hdev.prometeoai.ui.theme.PrometeoAITheme

@Composable
fun ChatScreen(
    text: String,
    onSendMessage: (String) -> Unit,
    mensajes: List<Mensaje> = emptyList(),
    opciones: Map<String, List<String>> = emptyMap(),
    seleccionados: Map<String, String?>,
    onSeleccion: (grupo: String, opcion: String?) -> Unit,
    uiState: UiState
) {
    val openBottomSheet = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mensajes.isNotEmpty()) {
                ChatHistory(mensajes = mensajes, modifier = Modifier.fillMaxSize())
            } else {
                Text(text = text)
            }
        }
        if (uiState == UiState.Loading){
            MessageBubbleAIText(
                texto = obtenerFraseAleatoria()
            )
        }
        Text(text = "Mensajes generados por Gemini 2.0 Flash-Lite", fontSize = 10.sp)

        if(opciones.isEmpty()){
            MessageInputBar(
                onSendMessage = onSendMessage
            )
        }else{

            MessageInputBar(onSendMessage = onSendMessage, settings = { openBottomSheet.value = true })
        }

        MyModalBottomSheet(
            openBottomSheet = openBottomSheet.value,
            onDismissRequest = { openBottomSheet.value = false },
            opciones = opciones,
            seleccionados = seleccionados,
            onSeleccion = onSeleccion
        )
    }
}
fun obtenerFraseAleatoria(): String {
    val frases = listOf(
        "Generando respuesta...",
        "Un momento...",
        "Cargando...",
        "Esperando respuesta...",
        "Dame un segundo...",
        "Estoy en eso...",
        "Esto tomará solo un momento...",
        "Ya casi está...",
        "Buscando la mejor respuesta...",
        "Haciendo magia...",
        "Algunos mensajes pueden contener errores"
    )
    return frases.random()
}
@Composable
fun MessageInputBar(
    modifier: Modifier = Modifier,
    onSendMessage: (String) -> Unit,
    settings: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0 // Detecta si el teclado está activo
    val focusManager = LocalFocusManager.current

    var text by remember { mutableStateOf("") }
    val bottomPadding = if (imeVisible) 15.dp else 10.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Red,
                unfocusedIndicatorColor = Color.Red,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            value = text,
            maxLines = 5,
            onValueChange = { text = it },
            placeholder = { Text("Mensaje") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            trailingIcon = {
                if(!imeVisible && text.isEmpty() || text.isBlank() ){
                    IconButton(onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Ocultar teclado"
                        )
                    }
                }else{
                    IconButton(onClick = {
                        onSendMessage(text)
                        text = ""
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },enabled = (text.isNotEmpty() && text.isNotBlank()) ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar"
                        )
                    }
                }
            },
            leadingIcon = {
                IconButton(
                    onClick = {settings()},
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Configuración"
                    )
                }
            }
        )
    }
}

@Composable
fun MessageInputBar(
    modifier: Modifier = Modifier,
    onSendMessage: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0 // Detecta si el teclado está activo
    val focusManager = LocalFocusManager.current

    var text by remember { mutableStateOf("") }
    val bottomPadding = if (imeVisible) 15.dp else 10.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = bottomPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Red,
                unfocusedIndicatorColor = Color.Red,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            value = text,
            maxLines = 5,
            onValueChange = { text = it },
            label = { Text("Mensaje") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            trailingIcon = {
                if(!imeVisible && text.isEmpty() || text.isBlank() ){
                    IconButton(onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Ocultar teclado"
                        )
                    }
                }else{
                    IconButton(onClick = {
                        onSendMessage(text)
                        text = ""
                    },enabled = (text.isNotEmpty() && text.isNotBlank()) ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar"
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ChatHistory(
    mensajes: List<Mensaje>,
    modifier: Modifier = Modifier
){
    val lazyListState = rememberLazyListState()
    LaunchedEffect(mensajes.size){
        lazyListState.scrollToItem(mensajes.size - 1)
    }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        state = lazyListState
    ){
        mensajes.forEach(){
            item {
                if (it.rol == Rol.USER) {
                    MessageBubbleUser(it)
                } else {
                    MessageBubbleAI(it)
                }
            }
        }
    }
}

@Composable
fun MessageBubbleUser(
    mensaje: Mensaje
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
            SelectionContainer(
                modifier = Modifier
                    .padding(start = 60.dp)
                    .background(
                        color = Color.Red,
                        shape = RoundedCornerShape(
                            topStart = 25.dp,
                            topEnd = 25.dp,
                            bottomStart = 25.dp,
                            bottomEnd = 25.dp
                        )
                    )
                    .padding(4.dp)
            ){
                Text(
                    text = mensaje.texto,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 20.dp)
                )
            }
    }
}

@Composable
fun MessageBubbleAI(
    mensaje: Mensaje
){
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        SelectionContainer(
            modifier = Modifier
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 25.dp,
                        bottomStart = 25.dp,
                        bottomEnd = 25.dp
                    )
                )
                .padding(4.dp)
        ){
            RichText(modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)){
                Markdown(
                    mensaje.texto.trimIndent()
                )
//                Text(
//                    text = mensaje.texto,
//                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp)
//                )
            }
        }
        IconButton(onClick = {
            val clip = ClipData.newPlainText("Texto guardado en el portapapeles", mensaje.texto)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
        }) {
            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy" )
        }

    }
}

@Composable
fun MessageBubbleAIText(
    texto: String
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 40.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Button(
            onClick = { /*TODO*/ },
            shape =
            RoundedCornerShape(
                topStart = 25.dp,
                topEnd = 25.dp,
                bottomStart = 25.dp,
                bottomEnd = 25.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Black
            )
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(end = 10.dp).size(25.dp),
                color = Color.Red,
                strokeWidth = 2.dp
            )
            texto.let { Text(text = it,
                color = MaterialTheme.colorScheme.onBackground) }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MessageAi(){
    PrometeoAITheme {
        MessageBubbleAI(
            mensaje = Mensaje(
                id = "1",
                texto = "Hola, soy Prometeo, ¿en qué puedo ayudarte hoy?",
                rol = Rol.IA,
            )
        )
    }
}

//Preview de noche

@Preview()
@Composable
fun MessageUser(){
    PrometeoAITheme {
        MessageBubbleUser(
            mensaje = Mensaje(
                id = "1",
                texto = "Hola, este es un texto que debo de hacer mas y mas grnade solo para ver como es que se muestra en el componente final, vaya vaya tacubaya",
                rol = Rol.USER,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyModalBottomSheet(
    openBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    opciones: Map<String, List<String>>,
    seleccionados: Map<String, String?>,
    onSeleccion: (grupo: String, opcion: String?) -> Unit
) {
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    // Sheet content
    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismissRequest() },
            sheetState = bottomSheetState,
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
               MenuParameters(
                   opciones = opciones,
                   seleccionados = seleccionados,
                   onSeleccion = onSeleccion
               )
            }

            Button(onClick = { onDismissRequest() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )) {
                Text("Guardar", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MenuParameters(
    opciones: Map<String, List<String>>,
    seleccionados: Map<String, String?>,
    onSeleccion: (grupo: String, opcion: String?) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        LazyColumn {
            opciones.forEach { (key, values) ->
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Text(text = key)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                        ){
                            values.forEach { option ->
                                val selected = seleccionados[key] == option
                                FilterChip(
                                    selected = selected,
                                    onClick = {
                                        onSeleccion(key, if (selected) null else option)
                                    },
                                    label = { Text(option) }
                                )
                                Spacer(modifier = Modifier.width(6.dp)) // Espacio entre los chips
                            }
                        }
                    }
                }
            }
        }
    }
}