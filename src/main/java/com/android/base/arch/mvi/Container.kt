package com.android.base.arch.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface Container<STATE : UIState, EVENT : UIEvent> {

    /** UI 状态 */
    val uiState: StateFlow<STATE>

    /** 单次事件 */
    val uiEvent: Flow<EVENT>

}

interface MutableContainer<STATE : UIState, EVENT : UIEvent> : Container<STATE, EVENT> {

    fun updateState(action: STATE.() -> STATE)

    fun sendEvent(event: EVENT)

}

internal class RealContainer<STATE : UIState, EVENT : UIEvent>(
    initialState: STATE,
    private val coroutineScope: CoroutineScope,
) : MutableContainer<STATE, EVENT> {

    private val _uiState = MutableStateFlow(initialState)

    // TODO: config channel capacity.
    private val _uiEvent = Channel<EVENT>()

    override val uiState: StateFlow<STATE> = _uiState

    override val uiEvent: Flow<EVENT> = _uiEvent.receiveAsFlow()

    override fun updateState(action: STATE.() -> STATE) {
        _uiState.update { action(_uiState.value) }
    }

    override fun sendEvent(event: EVENT) {
        coroutineScope.launch {
            _uiEvent.send(event)
        }
    }

}

class ContainerLazy<STATE : UIState, EVENT : UIEvent>(initialState: STATE, coroutineScope: CoroutineScope) :
    Lazy<MutableContainer<STATE, EVENT>> {

    private var cached: MutableContainer<STATE, EVENT>? = null

    override val value: MutableContainer<STATE, EVENT> =
        cached ?: RealContainer<STATE, EVENT>(initialState, coroutineScope).also { cached = it }

    override fun isInitialized() = cached != null

}


fun <STATE : UIState, Event : UIEvent> stateEventContainer(
    initialState: STATE,
    coroutineScope: CoroutineScope,
): Lazy<MutableContainer<STATE, Event>> {
    return ContainerLazy(initialState, coroutineScope)
}

@Suppress("")
fun <STATE : UIState, Event : UIEvent> ViewModel.stateEventContainer(
    initialState: STATE,
): Lazy<MutableContainer<STATE, Event>> {
    return ContainerLazy(initialState, viewModelScope)
}