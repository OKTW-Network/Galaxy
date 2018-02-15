package one.oktw.galaxy.data

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataQuery
import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.value.immutable.ImmutableValue
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.util.TypeTokens
import java.util.*

class DataUUID(uuid: UUID) : AbstractSingleData<UUID, DataUUID, DataUUID.Immutable>(uuid, key) {
    companion object {
        val key: Key<Value<UUID>> = Key.builder()
                .type(TypeTokens.UUID_VALUE_TOKEN)
                .id("uuid")
                .name("UUID")
                .query(DataQuery.of("uuid"))
                .build()
    }

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(value)
    override fun copy() = DataUUID(value)
    override fun getValueGetter(): Value<UUID> = Sponge.getRegistry().valueFactory.createValue(key, value)
    override fun toContainer(): DataContainer = super.toContainer().set(key, value)

    override fun from(container: DataContainer): Optional<DataUUID> {
        return if (container[key.query].isPresent) {
            value = container.getObject(key.query, UUID::class.java).get()
            Optional.of(this)
        } else {
            Optional.empty()
        }
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataUUID> {
        value = overlap.merge(this, dataHolder[DataUUID::class.java].orElse(null)).value
        return Optional.of(this)
    }

    class Immutable(uuid: UUID) : AbstractImmutableSingleData<UUID, Immutable, DataUUID>(uuid, key) {
        private val immutableValue: ImmutableValue<UUID> = Sponge.getRegistry().valueFactory.createValue(key, value).asImmutable()

        override fun getContentVersion() = 1
        override fun asMutable() = DataUUID(value)
        override fun getValueGetter() = immutableValue
        override fun toContainer(): DataContainer = super.toContainer().set(key, value)
    }

    class Builder : AbstractDataBuilder<DataUUID>(DataUUID::class.java, 1), DataManipulatorBuilder<DataUUID, Immutable> {
        override fun createFrom(dataHolder: DataHolder): Optional<DataUUID> = create().fill(dataHolder)
        override fun create() = DataUUID(UUID.randomUUID())
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}
