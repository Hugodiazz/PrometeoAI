package com.hdev.prometeoai.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdev.prometeoai.ViewModel.AppViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppScreen(
    viewModel: AppViewModel = viewModel()
){
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val selectedItem by viewModel.selectedItem.collectAsState()
    val selectedMod by viewModel.selectedMod.collectAsState()

    val items = remember {
        listOf(
            Icons.Default.Summarize,
            Icons.Default.Edit,
            Icons.Default.Book
        )
    }
    val modos = remember {
        listOf(
            "Resumir",
            "Reescribir",
            "Ruta de estudio"
        )
    }

    MyDismissibleNavigationDrawer(
        drawerState = drawerState,
        items = items,
        names = modos,
        selectedItem = selectedItem,
        onItemSelected = viewModel::onItemSelected,
        selectedMod = selectedMod,
        onModoSelected = viewModel::onModoSelected,
        drawerContent = {},
        content = {
            when(selectedItem) {
                Icons.Default.Summarize -> ResumirScreen()
                Icons.Default.Edit -> ChatScreen( "Reescribir" ,{},{})
                Icons.Default.Book -> ChatScreen("Ruta de estudio",{},{})
            }
        }
    )
}
@Composable
fun MyDismissibleNavigationDrawer(
    drawerState: DrawerState,
    items: List<ImageVector>,
    names: List<String>,
    selectedItem: ImageVector,
    onItemSelected: (ImageVector) -> Unit,
    selectedMod: Int,
    onModoSelected: (Int) -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet() {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(12.dp))
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(names[index].substringAfterLast(".")) },
                            selected = item == selectedItem,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onItemSelected(item)
                                onModoSelected(index)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ){
                MyTopAppBar(
                    titulo = names[selectedMod],
                    onClick = { scope.launch { drawerState.open() } }
                )
                content()
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MyTopAppBar(
    titulo: String = "Prometeo",
    onClick: () -> Unit = {}
){
    CenterAlignedTopAppBar(title = { Text(text = titulo) },
        navigationIcon = {
            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Localized description"
                )
            }
        })
}