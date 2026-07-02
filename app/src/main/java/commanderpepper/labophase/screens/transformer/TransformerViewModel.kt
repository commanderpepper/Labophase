package commanderpepper.labophase.screens.transformer

import commanderpepper.labophase.models.Leader
import kotlinx.coroutines.flow.StateFlow

interface TransformerViewModel {

    val entryText: StateFlow<String>

    val resultText: StateFlow<String>

    fun resetEntry()

    fun transformEntry()

    fun copyToClipboard()
}