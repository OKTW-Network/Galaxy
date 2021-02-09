/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.util

import net.minecraft.util.Identifier
import java.util.*
import kotlin.collections.HashMap

open class CustomRegistry<T : Registrable> {
    private val registry = HashMap<Identifier, T>()

    @Throws(IllegalArgumentException::class)
    open fun register(obj: T): T {
        if (registry.putIfAbsent(obj.identifier, obj) != null) {
            throw IllegalArgumentException("Identifier duplicate: ${obj.identifier}")
        }

        return obj
    }

    open fun get(identifier: Identifier): T? = registry[identifier]

    open fun getAll(): Map<Identifier, T> = Collections.unmodifiableMap(registry)
}
