package commanderpepper.labophase.di

import commanderpepper.labophase.data.AppDatabase
import commanderpepper.labophase.data.EntryRepository
import commanderpepper.labophase.data.EntryRepositoryImpl
import commanderpepper.labophase.data.SettingsRepository
import commanderpepper.labophase.data.SettingsRepositoryImpl
import commanderpepper.labophase.data.settingsDataStore
import commanderpepper.labophase.logic.LeaderOrderDecider
import commanderpepper.labophase.logic.LeaderOrderDeciderImpl
import commanderpepper.labophase.logic.TournamentResultInterpreter
import commanderpepper.labophase.logic.converter.EntryToEntrySelectionUIConverter
import commanderpepper.labophase.screens.entries.EntrySelectionViewModelImpl
import commanderpepper.labophase.screens.roundentry.RoundEntryViewModelImpl
import commanderpepper.labophase.screens.settings.SettingsViewModelImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    single { AppDatabase.create(androidContext()) }
    single { get<AppDatabase>().entryDao() }
    single<EntryRepository> { EntryRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext().settingsDataStore, get()) }
    single { TournamentResultInterpreter }
    single<LeaderOrderDecider> { LeaderOrderDeciderImpl(get(), get()) }
    single { EntryToEntrySelectionUIConverter() }
    viewModel { params -> RoundEntryViewModelImpl(get(), get(), params.getOrNull<Int>()) }
    viewModel { EntrySelectionViewModelImpl(get(), get()) }
    viewModel { SettingsViewModelImpl(get()) }
}
