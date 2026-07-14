package commanderpepper.labophase.di

import commanderpepper.labophase.data.AppDatabase
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryRepositoryImpl
import commanderpepper.labophase.logic.LeaderOrderDecider
import commanderpepper.labophase.logic.LeaderOrderDeciderImpl
import commanderpepper.labophase.logic.TournamentResultInterpreter
import commanderpepper.labophase.screens.roundentry.RoundEntryViewModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().entryDao() }
    single<EntryRepository> { EntryRepositoryImpl(get()) }
    single { TournamentResultInterpreter }
    single<LeaderOrderDecider> { LeaderOrderDeciderImpl(get()) }
    viewModel { RoundEntryViewModelImpl(get(), get()) }
}
