package info.c2dl.ccmoddbviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.beust.klaxon.Klaxon

import info.c2dl.ccmoddbviewer.ui.theme.CCModDBViewerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CCModDBViewerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // coroutine is basically the same as Thread in Java
                    val coroutineScope = rememberCoroutineScope()
                    // making the result a mutable so the screen updates with mod list after fetching is done
                    var result by remember { mutableStateOf<ModDB?>(null) }
                    // coroutine scope because doing networking on main thread is bad
                    coroutineScope.launch(Dispatchers.IO) {
                        val request = req("https://raw.githubusercontent.com/CCDirectLink/CCModDB/master/mods.json")?.body()?.string()
                        println(request)
                        if (request != null) {
                            result = Klaxon().parse<ModDB>(request)
                        }
                    }

                    if (result == null) {
                        Column(
                            modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.wrapContentSize().align(Alignment.CenterHorizontally)
                            )
                            Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Loading...")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            result?.let { result ->
                                result.mods?.let { mods ->
                                    items(mods.map { it.value }) { meta ->
                                        ModListItem(
                                            modName = "${meta.name}",
                                            modDescription = "${meta.description}"
                                        ) {
                                            Text("${meta.description}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun req(url: String): Response? {
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute()
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
