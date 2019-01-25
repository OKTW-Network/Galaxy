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

import com.google.common.reflect.TypeToken
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.DUMMY
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataQuery
import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleEnumData
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleEnumData
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.value.mutable.Value
import java.util.*

class DataItemType(type: ItemType = DUMMY) :
    AbstractSingleEnumData<ItemType, DataItemType, DataItemType.Immutable>(type, key, DUMMY) {
    companion object {
        val key: Key<Value<ItemType>> = Key.builder()
            .type(object : TypeToken<Value<ItemType>>() {})
            .id("item_type")
            .name("Item Type")
            .query(DataQuery.of("type"))
            .build()
    }

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(value)
    override fun copy() = DataItemType(value)

    override fun from(container: DataContainer): Optional<DataItemType> {
        return if (container[key.query].isPresent) {
            value = ItemType.valueOf(container.getString(key.query).get())
            Optional.of(this)
        } else {
            Optional.empty()
        }
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataItemType> {
        value = overlap.merge(this, dataHolder[DataItemType::class.java].orElse(null)).value
        return Optional.of(this)
    }


    class Immutable(type: ItemType = DUMMY) :
        AbstractImmutableSingleEnumData<ItemType, Immutable, DataItemType>(type, DUMMY, key) {
        override fun getContentVersion() = 1
        override fun asMutable() = DataItemType((value))
    }

    class Builder : AbstractDataBuilder<DataItemType>(DataItemType::class.java, 1),
        DataManipulatorBuilder<DataItemType, Immutable> {
        override fun create() = DataItemType()
        override fun createFrom(dataHolder: DataHolder): Optional<DataItemType> = create().fill(dataHolder)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}
