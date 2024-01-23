package com.example.ble_wiegewertanzeigen.data

import com.example.ble_wiegewertanzeigen.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow

interface WeightReceiveManager {

    val data: MutableSharedFlow<Resource<WeightResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()
}