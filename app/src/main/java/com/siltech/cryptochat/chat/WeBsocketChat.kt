package com.siltech.cryptochat.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.siltech.cryptochat.extensions.WebServicesProvider
import com.siltech.cryptochat.model.GetMessagesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class WeBsocketChat constructor(
    private val interactor: MainInteractor):
    ViewModel() {

    @ExperimentalCoroutinesApi
    fun subscribeToSocketEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                interactor.startSocket().consumeEach {
                    if (it[0].userLogin == null) {
                        println("Collecting : ${it[0].message}")
                    } else {
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        interactor.stopSocket()
        super.onCleared()
    }

}

class MainInteractor constructor(private val repository: MainRepository) {

    @ExperimentalCoroutinesApi
    fun stopSocket() {
        repository.closeSocket()
    }

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<GetMessagesResponse> = repository.startSocket()

}

class MainRepository constructor(private val webServicesProvider: WebServicesProvider) {

    @ExperimentalCoroutinesApi
    fun startSocket(): Channel<GetMessagesResponse> =
        webServicesProvider.startSocket()

    @ExperimentalCoroutinesApi
    fun closeSocket() {
        webServicesProvider.stopSocket()
    }
}