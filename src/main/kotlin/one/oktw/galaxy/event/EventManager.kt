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
import net.minecraft.util.thread.ThreadExecutor
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.Event
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.*

class EventManager(private val serverThread: ThreadExecutor<*>) : CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    private val asyncEventListeners = ConcurrentHashMap<KClass<*>, ConcurrentHashMap<Any, CopyOnWriteArrayList<KFunction<*>>>>()
    private val asyncEventCallback = ConcurrentHashMap<KClass<*>, CopyOnWriteArrayList<Function1<Event, Unit>>>()
    private val syncEventListeners = HashMap<KClass<*>, HashMap<Any, ArrayList<KFunction<*>>>>()
    private val syncEventCallback = HashMap<KClass<*>, ArrayList<Function1<Event, Unit>>>()

    fun <T : Event> emit(event: T): T {
        launch {
            asyncEventListeners[event::class]?.forEach { obj, listeners -> listeners.forEach { it.javaMethod?.invoke(obj, event) } }
            asyncEventCallback[event::class]?.forEach { it(event) }
        }

        if (!serverThread.isOnThread) {
            runBlocking(serverThread.asCoroutineDispatcher()) {
                syncEventListeners[event::class]?.forEach { obj, listeners -> listeners.forEach { it.javaMethod?.invoke(obj, event) } }
                syncEventCallback[Event::class]?.forEach { it(event) }
            }
        } else {
            syncEventListeners[event::class]?.forEach { obj, listeners -> listeners.forEach { it.javaMethod?.invoke(obj, event) } }
            syncEventCallback[Event::class]?.forEach { it(event) }
        }
        return event
    }

    fun register(obj: Any) {
        obj::class.memberFunctions.forEach {
            val annotation = it.findAnnotation<EventListener>() ?: return@forEach
            val event = it.valueParameters.first().type.jvmErasure

            require(event.isSubclassOf(Event::class)) { "Event register failed, type mismatch: ${event.jvmName}" }

            it.isAccessible = true

            if (annotation.sync) {
                syncEventListeners.getOrPut(event, { hashMapOf() }).getOrPut(obj) { arrayListOf() }.add(it)
            } else {
                asyncEventListeners.getOrPut(event, { ConcurrentHashMap() }).getOrPut(obj) { CopyOnWriteArrayList() }.add(it)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> register(event: KClass<T>, listener: (T) -> Unit) {
        if (listener.reflect()?.findAnnotation<EventListener>()?.sync == true) {
            syncEventCallback.getOrPut(event, { arrayListOf() }).add(listener as (Event) -> Unit)
        } else {
            asyncEventCallback.getOrPut(event, { CopyOnWriteArrayList() }).add(listener as (Event) -> Unit)
        }
    }

    fun <T : Event> unregister(event: KClass<T>, listener: (T) -> Unit) {
        if (listener.reflect()?.findAnnotation<EventListener>()?.sync == true) {
            syncEventCallback[event]?.remove(listener)
        } else {
            asyncEventCallback[event]?.remove(listener)
        }

        removeEmpty()
    }

    fun unregister(obj: Any) {
        obj::class.memberFunctions.forEach {
            val annotation = it.findAnnotation<EventListener>() ?: return
            val event = it.parameters.first().type.jvmErasure

            if (!event.isSubclassOf(Event::class)) return@forEach

            if (annotation.sync) {
                syncEventListeners[event]?.get(obj)?.remove(it)
            } else {
                asyncEventListeners[event]?.get(obj)?.remove(it)
            }
        }

        removeEmpty()
    }

    private fun removeEmpty() {
        syncEventListeners.entries.removeIf { event ->
            event.value.entries.removeIf { it.value.isEmpty() }
            event.value.isEmpty()
        }
    }
}
