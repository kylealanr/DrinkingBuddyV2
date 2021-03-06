@file:Suppress("unused")

package com.kyleriedemann.drinkingbuddy.sdk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.Exceptions.BluetoothLENotSupportedException
import BACtrackAPI.Exceptions.BluetoothNotEnabledException
import BACtrackAPI.Exceptions.LocationServicesNotEnabledException
import android.app.Application
import com.github.ajalt.timberkt.Timber.e
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Created by kyle
 *
 * 1/25/20
 */
class BacTrackSdk(
    private val application: Application,
    private val apiKey: String,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private var _bacTrackSdk: BACtrackAPI? = null

    private val callbacks = ChannelCallbacks(ioDispatcher)

    val errorEvents = callbacks.errorEvents
    val apiKeyEvents = callbacks.apiKeyEvents
    val connectedEvents = callbacks.connectedEvents
    val readingEvents = callbacks.readingEvents
    val deviceInformationEvents = callbacks.deviceInformationEvents
    val stateEvents = callbacks.stateEvents

    fun start() {
        try {
            _bacTrackSdk = BACtrackAPI(application, callbacks, apiKey)
            _bacTrackSdk?.breathalyzerBatteryVoltage
        } catch (t: BluetoothLENotSupportedException) {
            e { "BLE not supported" }
        } catch (t: LocationServicesNotEnabledException) {
            e { "Location services not enabled" }
        } catch (t: BluetoothNotEnabledException) {
            e { "Bluetooth not enabled" }
        }
    }

    val isConnected = _bacTrackSdk?.isConnected ?: false

    fun connectToClosestDevice() = throwIfNotInitialized {
        _bacTrackSdk?.connectToNearestBreathalyzer()
    }

    fun disconnect() = throwIfNotInitialized {
        _bacTrackSdk?.disconnect()
    }

    fun takeReading() = throwIfNotInitialized {
        _bacTrackSdk?.startCountdown()
    }

    fun getFirmwareVersion() = throwIfNotInitialized {
        _bacTrackSdk?.firmwareVersion
        _bacTrackSdk?.useCount
    }

    fun getSerialNumber() = throwIfNotInitialized {
        _bacTrackSdk?.serialNumber
    }

    private fun BacTrackSdk.throwIfNotInitialized(block: () -> Unit) {
        if (this._bacTrackSdk == null) throw SdkNotInitialized()
        block.invoke()
    }
}
