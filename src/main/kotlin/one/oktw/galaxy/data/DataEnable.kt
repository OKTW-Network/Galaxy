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

class DataEnable(enable: Boolean = false) :
    AbstractBooleanData<DataEnable, DataEnable.Immutable>(enable, key, false) {
    companion object {
        val key: Key<Value<Boolean>> = Key.builder()
            .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
            .id("enable")
            .name("Enable")
            .query(DataQuery.of("enable"))
            .build()
    }

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(value)
    override fun copy() = DataEnable(value)

    override fun from(container: DataContainer): Optional<DataEnable> {
        return if (container[key.query].isPresent) {
            value = container.getBoolean(key.query).get()
            Optional.of(this)
        } else {
            Optional.empty()
        }
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataEnable> {
        value = overlap.merge(this, dataHolder[DataEnable::class.java].orElse(null)).value
        return Optional.of(this)
    }

    class Immutable(enable: Boolean = false) :
        AbstractImmutableBooleanData<Immutable, DataEnable>(enable, key, false) {
        override fun getContentVersion() = 1
        override fun asMutable() = DataEnable(value)
    }

    class Builder : AbstractDataBuilder<DataEnable>(DataEnable::class.java, 1),
        DataManipulatorBuilder<DataEnable, Immutable> {
        override fun create() = DataEnable()
        override fun createFrom(dataHolder: DataHolder): Optional<DataEnable> = create().fill(dataHolder)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}
