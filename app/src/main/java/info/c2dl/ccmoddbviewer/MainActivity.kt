package info.c2dl.ccmoddbviewer

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.beust.klaxon.Klaxon

import info.c2dl.ccmoddbviewer.ui.theme.CCModDBViewerTheme
import info.c2dl.ccmoddbviewer.ui.theme.Purple200
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
                    color = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.medium
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
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.CenterHorizontally)
                            )
                            Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = "Loading...")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            result?.let { result ->
                                result.mods?.let { mods ->
                                    items(mods.map { it.value }) { mod ->
                                        ModListItem(mod)
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

    @Composable
    fun ExternalButton(dest: String, text: String) {
        val context = LocalContext.current
        val intent = remember { Intent(Intent.ACTION_VIEW, Uri.parse(dest)) }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { context.startActivity(intent) }
        ) {
            Text(text)
        }
    }

    /**
     * @author X1nto
     */
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ModListItem(
        mod: Mod
    ) {
        var isDialogVisible by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { isDialogVisible = true },
            elevation = 10.dp
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(mod.name!!)
                Text(mod.description!!)
            }
        }

        if (isDialogVisible) {
            Dialog(
                onDismissRequest = { isDialogVisible = false },
                properties = DialogProperties(usePlatformDefaultWidth = false),
                content = {
                    Card(
                        modifier = Modifier.padding(30.dp).background(Color(0x2d2d2d))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                fontSize = 30.sp,
                                text = mod.name!!
                            )
                            Text(mod.description!!)

                            if (mod.page.isNotEmpty()) {
                                mod.page.let { pages ->
                                    pages.map {
                                        ExternalButton(it.url!!, it.name!!)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
