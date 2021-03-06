package com.kyleriedemann.drinkingbuddy.data.source.local

import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.ui.log.FilterRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Local data source for [Log]s. Logs are filtered with a global [FilterRepository] that returns
 * device local settings for logs to be included in the standard log presentation.
 *
 * @param logDao [Log] DAO class that handles writing and reading from the local db
 * @param filterRepository repository of local user settings for filtering log output
 * @param dispatcher dispatcher on which to run all local coroutines
 */
class LogLocalDataSource @Inject constructor(
    private val logDao: LogDao,
    private val filterRepository: FilterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun levels() = logDao.getLogLevels().flowOn(dispatcher)

    fun tags() = logDao.getLogTags().flowOn(dispatcher)

    suspend fun save(log: Log) = withContext(dispatcher) {
        val record = logDao.getLogById(log.id)
        if (record != null) {
            logDao.updateLog(log)
        } else {
            logDao.insertLog(log)
        }
    }
}
