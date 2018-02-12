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

class DataOverheat(overheat: Boolean) : AbstractBooleanData<DataOverheat, DataOverheat.Immutable>(overheat, key, false) {
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

    class Immutable(overheat: Boolean) : AbstractImmutableBooleanData<Immutable, DataOverheat>(overheat, key, false) {
        override fun getContentVersion() = 1
        override fun asMutable() = DataOverheat(value)
    }

    class Builder : AbstractDataBuilder<DataOverheat>(DataOverheat::class.java, 1), DataManipulatorBuilder<DataOverheat, Immutable> {
        override fun createFrom(dataHolder: DataHolder): Optional<DataOverheat> = create().fill(dataHolder)
        override fun create() = DataOverheat(false)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}