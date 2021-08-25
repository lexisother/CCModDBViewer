package info.c2dl.ccmoddbviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import info.c2dl.ccmoddbviewer.ui.theme.CCModDBViewerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCModDBViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ModListItem(modName = "Test", modDescription = "Test2") {
                        Text("Hi")
                    }
                }
            }
        }
    }

    /**
     * @author X1nto
     */
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ModListItem(
        modName: String,
        modDescription: String,
        content: @Composable () ->  Unit
    ) {
        var isDialogVisible by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isDialogVisible = true }
                .background(Color(525252))
        ) {
            Column {
                Text(text = modName)
                Text(text = modDescription)
            }
        }

        if (isDialogVisible) {
            Dialog(
                onDismissRequest = { isDialogVisible = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                content = content
            )
        }
    }
}
