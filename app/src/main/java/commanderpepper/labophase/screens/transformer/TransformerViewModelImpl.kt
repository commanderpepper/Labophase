package commanderpepper.labophase.screens.transformer

import androidx.lifecycle.ViewModel
import commanderpepper.labophase.logic.TournamentResultInterpreter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransformerViewModelImpl(private val interpreter: TournamentResultInterpreter) : TransformerViewModel, ViewModel() {

    private val _resultText: MutableStateFlow<String> = MutableStateFlow("")
    override val resultText: StateFlow<String> = _resultText

    private val _entryText: MutableStateFlow<String> = MutableStateFlow("")
    override val entryText: StateFlow<String> = _entryText

    override fun copyToClipboard() {
        TODO("Not yet implemented")
    }

    override fun resetEntry() {
        _entryText.value = ""
    }

    override fun transformEntry() {
        _resultText.value = interpreter.transformTournamentResult(_entryText.value)
    }
}