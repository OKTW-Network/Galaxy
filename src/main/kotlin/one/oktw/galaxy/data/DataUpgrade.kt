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
import one.oktw.galaxy.item.enums.UpgradeType
import one.oktw.galaxy.item.enums.UpgradeType.BASE
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataQuery
import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.util.TypeTokens
import java.util.*

class DataUpgrade(type: UpgradeType = BASE, level: Int = 0) : AbstractData<DataUpgrade, DataUpgrade.Immutable>() {
    companion object {
        val TYPE: Key<Value<UpgradeType>> = Key.builder()
            .type(object : TypeToken<Value<UpgradeType>>() {})
            .id("upgrade_type")
            .name("Upgrade Type")
            .query(DataQuery.of("upgrade", "type"))
            .build()
        val LEVEL: Key<Value<Int>> = Key.builder()
            .type(TypeTokens.INTEGER_VALUE_TOKEN)
            .id("upgrade_level")
            .name("Upgrade Level")
            .query(DataQuery.of("upgrade", "level"))
            .build()
    }

    var type: UpgradeType = type
        private set

    var level: Int = level
        private set

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(type, level)
    override fun copy() = DataUpgrade(type, level)
    override fun toContainer(): DataContainer = super.toContainer().set(TYPE.query, type.name).set(LEVEL, level)

    override fun from(container: DataContainer): Optional<DataUpgrade> {
        if (!container.contains(TYPE, LEVEL)) return Optional.empty()

        type = UpgradeType.valueOf(container.getString(TYPE.query).get())
        level = container.getInt(LEVEL.query).get()

        return Optional.of(this)
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataUpgrade> {
        val merge = overlap.merge(this, dataHolder[DataUpgrade::class.java].orElse(null))

        type = merge.type
        level = merge.level

        return Optional.of(this)
    }

    override fun registerGettersAndSetters() {
        registerKeyValue(TYPE) { Sponge.getRegistry().valueFactory.createValue(TYPE, type) }
        registerKeyValue(LEVEL) { Sponge.getRegistry().valueFactory.createValue(LEVEL, level) }

        registerFieldGetter(TYPE) { type }
        registerFieldGetter(LEVEL) { level }

        registerFieldSetter(TYPE) { type = it }
        registerFieldSetter(LEVEL) { level = it }
    }

    class Immutable(val type: UpgradeType = BASE, val level: Int = 0) :
        AbstractImmutableData<Immutable, DataUpgrade>() {
        override fun getContentVersion() = 1
        override fun asMutable() = DataUpgrade(type, level)
        override fun toContainer(): DataContainer = super.toContainer().set(TYPE.query, type.name).set(LEVEL, level)

        override fun registerGetters() {
            registerKeyValue(TYPE) { Sponge.getRegistry().valueFactory.createValue(TYPE, type).asImmutable() }
            registerKeyValue(LEVEL) { Sponge.getRegistry().valueFactory.createValue(LEVEL, level).asImmutable() }

            registerFieldGetter(TYPE) { type }
            registerFieldGetter(LEVEL) { level }
        }
    }

    class Builder : AbstractDataBuilder<DataUpgrade>(DataUpgrade::class.java, 1),
        DataManipulatorBuilder<DataUpgrade, Immutable> {
        override fun create() = DataUpgrade()
        override fun createFrom(dataHolder: DataHolder): Optional<DataUpgrade> = create().fill(dataHolder)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}
