/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.event

import kotlinx.coroutines.*
import net.minecraft.util.ThreadExecutor
import one.oktw.galaxy.event.type.Event
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Consumer
import kotlin.reflect.KClass

class EventManager(private val serverThread: ThreadExecutor<*>) : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val asyncEventListeners = ConcurrentHashMap<KClass<out Event>, ConcurrentHashMap.KeySetView<Consumer<Event>, Boolean>>()
    private val syncEventListeners = ConcurrentHashMap<KClass<out Event>, ConcurrentHashMap.KeySetView<Consumer<Event>, Boolean>>()

    fun <T : Event> emit(event: T) {
        launch { asyncEventListeners[event::class]?.forEach { it.accept(event) } }

        if (!serverThread.isOnThread) {
            runBlocking(serverThread.asCoroutineDispatcher()) { syncEventListeners[event::class]?.forEach { it.accept(event) } }
        } else {
            syncEventListeners[event::class]?.forEach { it.accept(event) }
        }
    }

    fun <T : Event> register(event: KClass<T>, sync: Boolean = false, listener: Consumer<T>) = launch {
        (if (sync) syncEventListeners else asyncEventListeners).getOrPut(event, { ConcurrentHashMap.newKeySet() }).add(listener as Consumer<Event>)
    }

    fun <T : Event> register(event: KClass<T>, sync: Boolean = false, listener: (T) -> Unit) = register(event, sync, Consumer(listener::invoke))

    fun <T : Event> unregister(listener: (T) -> Unit) = launch {
        asyncEventListeners.forEachValue(4) { set -> set.removeIf { it == listener } }
    }

    fun <T : Event> unregister(event: KClass<T>, listener: (T) -> Unit) = launch {
        asyncEventListeners[event]?.removeIf { it == listener }
    }
}
