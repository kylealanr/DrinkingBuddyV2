package com.kyleriedemann.drinkingbuddy.ui.log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.d
import com.kyleriedemann.drinkingbuddy.data.interactors.FilteredLogPolicy
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.data.models.LogTag
import com.kyleriedemann.drinkingbuddy.data.source.local.LogLocalDataSource
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LogListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val filterRepository: FilterRepository,
    private val filteredLogPolicy: FilteredLogPolicy,
    private val logLocalDataSource: LogLocalDataSource
): ViewModel() {

    suspend fun filteredItems() = filteredLogPolicy.invoke()

    val levelSettings = filterRepository.levelFilter

    val tags = logLocalDataSource.tags()

    val tagSettings = combine(filterRepository.tagFilter, logLocalDataSource.tags()) { filters, tags ->
        tags.map { it.copy(selected = filters.logTags.contains(it)) }
    }

    // this is still cleaner than the binding to have the handler pass the level
    fun toggleVerbose() = toggleLevelFilter(LogLevel.Verbose)
    fun toggleDebug() = toggleLevelFilter(LogLevel.Debug)
    fun toggleInfo() = toggleLevelFilter(LogLevel.Info)
    fun toggleWarn() = toggleLevelFilter(LogLevel.Warn)
    fun toggleError() = toggleLevelFilter(LogLevel.Error)
    fun toggleAssert() = toggleLevelFilter(LogLevel.Assert)
    fun toggleUnknown() = toggleLevelFilter(LogLevel.Unknown)

    fun toggleLevelFilter(logLevel: LogLevel) = viewModelScope.launch {
        d { "Toggling log level filter $logLevel" }
        filterRepository.toggleLogLevel(logLevel)
    }

    fun toggleTagFilter(logTag: LogTag) = viewModelScope.launch {
        d { "toggleTagFilter($logTag)" }
        filterRepository.toggleLogTag(logTag)
    }

    fun clearFilters() = viewModelScope.launch {
        filterRepository.clearFilter()
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<LogListViewModel>
}
