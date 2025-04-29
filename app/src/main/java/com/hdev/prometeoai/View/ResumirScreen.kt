package com.hdev.prometeoai.View

import androidx.compose.runtime.Composable

@Composable
fun ResumirScreen(){
    ChatScreen(
        text = "Estas en el modo resumir, por favor ingresa el texto que deseas resumir y ajusta los parametros o dejale el trabajo a Prometeo y presiona enviar.",
        onSendMessage = { /*TODO*/ },
        settings = { /*TODO*/ }
    )
}