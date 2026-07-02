package commanderpepper.labophase.di

import commanderpepper.labophase.logic.TournamentResultInterpreter
import commanderpepper.labophase.screens.roundentry.RoundEntryViewModelImpl
import commanderpepper.labophase.screens.transformer.TransformerViewModelImpl
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { TournamentResultInterpreter }
    viewModel { TransformerViewModelImpl(get()) }
    viewModel { RoundEntryViewModelImpl() }
}
