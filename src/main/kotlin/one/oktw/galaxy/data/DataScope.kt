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

class DataScope(scoping: Boolean) : AbstractBooleanData<DataScope, DataScope.Immutable>(scoping, key, false) {
    companion object {
        val key: Key<Value<Boolean>> = Key.builder()
                .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
                .id("scoping")
                .name("Scoping")
                .query(DataQuery.of("scoping"))
                .build()
    }

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(value)
    override fun copy() = DataScope(value)

    override fun from(container: DataContainer): Optional<DataScope> {
        return if (container[key.query].isPresent) {
            value = container.getBoolean(key.query).get()
            Optional.of(this)
        } else {
            Optional.empty()
        }
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataScope> {
        value = overlap.merge(this, dataHolder[DataScope::class.java].orElse(null)).value
        return Optional.of(this)
    }

    class Immutable(scoping: Boolean) : AbstractImmutableBooleanData<Immutable, DataScope>(scoping, key, false) {
        override fun getContentVersion() = 1
        override fun asMutable() = DataScope(value)
    }

    class Builder : AbstractDataBuilder<DataScope>(DataScope::class.java, 1), DataManipulatorBuilder<DataScope, Immutable> {
        override fun createFrom(dataHolder: DataHolder): Optional<DataScope> = create().fill(dataHolder)
        override fun create() = DataScope(false)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}
