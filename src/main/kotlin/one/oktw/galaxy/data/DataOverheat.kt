/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.data

import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataQuery
import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableBooleanData
import org.spongepowered.api.data.manipulator.mutable.common.AbstractBooleanData
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.util.TypeTokens
import java.util.*

class DataOverheat(overheat: Boolean = false) :
    AbstractBooleanData<DataOverheat, DataOverheat.Immutable>(overheat, key, false) {
    companion object {
        val key: Key<Value<Boolean>> = Key.builder()
            .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
            .id("overheat")
            .name("Overheat")
            .query(DataQuery.of("overheat"))
            .build()
    }

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(value)
    override fun copy() = DataOverheat(value)

    override fun from(container: DataContainer): Optional<DataOverheat> {
        return if (container[key.query].isPresent) {
            value = container.getBoolean(key.query).get()
            Optional.of(this)
        } else {
            Optional.empty()
        }
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataOverheat> {
        value = overlap.merge(this, dataHolder[DataOverheat::class.java].orElse(null)).value
        return Optional.of(this)
    }

    class Immutable(overheat: Boolean = false) :
        AbstractImmutableBooleanData<Immutable, DataOverheat>(overheat, key, false) {
        override fun getContentVersion() = 1
        override fun asMutable() = DataOverheat(value)
    }

    class Builder : AbstractDataBuilder<DataOverheat>(DataOverheat::class.java, 1),
        DataManipulatorBuilder<DataOverheat, Immutable> {
        override fun create() = DataOverheat()
        override fun createFrom(dataHolder: DataHolder): Optional<DataOverheat> = create().fill(dataHolder)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}
