package com.android.base.arch.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KProperty1

interface UIState

fun <T : UIState> Flow<T>.collectStateWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: StateCollector<T>.() -> Unit,
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            StateCollector(this@collectStateWithLifecycle, this.coroutineContext).action()
        }
    }
}

class StateCollector<T : UIState>(
    private val flow: Flow<T>,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {

    fun <A> collectPartial(
        prop1: KProperty1<T, A>,
        action: suspend (A) -> Unit,
    ) {
        launch {
            flow.map { prop1.get(it) }.distinctUntilChanged().collect { partialState ->
                action(partialState)
            }
        }
    }

    fun <A, B> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        action: suspend (A, B) -> Unit,
    ) {
        launch {
            flow.map { Pair(prop1.get(it), prop2.get(it)) }.distinctUntilChanged().collect { (partialStateA, partialStateB) ->
                action(partialStateA, partialStateB)
            }
        }
    }

    fun <A, B, C> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        prop3: KProperty1<T, C>,
        action: suspend (A, B, C) -> Unit,
    ) {
        launch {
            flow.map { Triple(prop1.get(it), prop2.get(it), prop3.get(it)) }.distinctUntilChanged()
                .collect { (partialStateA, partialStateB, partialStateC) ->
                    action(partialStateA, partialStateB, partialStateC)
                }
        }
    }

    fun <A, B, C, D> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        prop3: KProperty1<T, C>,
        prop4: KProperty1<T, D>,
        action: suspend (A, B, C, D) -> Unit,
    ) {
        launch {
            flow.map { Quadruple(prop1.get(it), prop2.get(it), prop3.get(it), prop4.get(it)) }.distinctUntilChanged()
                .collect { (partialStateA, partialStateB, partialStateC, partialStateD) ->
                    action(partialStateA, partialStateB, partialStateC, partialStateD)
                }
        }
    }

    fun <A, B, C, D, E> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        prop3: KProperty1<T, C>,
        prop4: KProperty1<T, D>,
        prop5: KProperty1<T, E>,
        action: suspend (A, B, C, D, E) -> Unit,
    ) {
        launch {
            flow.map { Quintuple(prop1.get(it), prop2.get(it), prop3.get(it), prop4.get(it), prop5.get(it)) }.distinctUntilChanged()
                .collect { (partialStateA, partialStateB, partialStateC, partialStateD, partialStateE) ->
                    action(partialStateA, partialStateB, partialStateC, partialStateD, partialStateE)
                }
        }
    }

    fun <A, B, C, D, E, F> collectPartial(
        prop1: KProperty1<T, A>,
        prop2: KProperty1<T, B>,
        prop3: KProperty1<T, C>,
        prop4: KProperty1<T, D>,
        prop5: KProperty1<T, E>,
        prop6: KProperty1<T, F>,
        action: suspend (A, B, C, D, E, F) -> Unit,
    ) {
        launch {
            flow.map { Sextuple(prop1.get(it), prop2.get(it), prop3.get(it), prop4.get(it), prop5.get(it), prop6.get(it)) }.distinctUntilChanged()
                .collect { (partialStateA, partialStateB, partialStateC, partialStateD, partialStateE, partialStateF) ->
                    action(partialStateA, partialStateB, partialStateC, partialStateD, partialStateE, partialStateF)
                }
        }
    }

}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
)

data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
)

data class Sextuple<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
)