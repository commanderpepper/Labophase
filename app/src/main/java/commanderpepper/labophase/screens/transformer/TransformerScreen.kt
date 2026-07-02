package commanderpepper.labophase.screens.transformer

import android.widget.EditText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TransformerScreen(viewModel: TransformerViewModel) {
    val entryText = viewModel.entryText.collectAsState()
    val resultText = viewModel.resultText.collectAsState()
    TransformerScreen(
        entryText = entryText.value,
        resultText = resultText.value,
        copyToClipboard = viewModel::copyToClipboard,
        transformText = viewModel::transformEntry,
        resetText = viewModel::resetEntry
    )
}

@Composable
fun TransformerScreen(
    entryText: String,
    resultText: String,
    copyToClipboard: () -> Unit,
    transformText: () -> Unit,
    resetText: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = entryText,
            onValueChange = {},
            label = { Text("Enter something will ya") }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = resultText,
            onValueChange = {},
            label = { Text("This goes to Punk Records") }
        )
        Button(onClick = { transformText() }) { Text("TRANSFORM!") }
        Button(onClick = { resetText() }) { Text("reset...") }
    }
}

@Preview
@Composable
fun TransformerScreenPreview(){
    TransformerScreen(
        entryText = "",
        resultText = "",
        copyToClipboard = {},
        transformText = {},
        resetText = {}
    )
}