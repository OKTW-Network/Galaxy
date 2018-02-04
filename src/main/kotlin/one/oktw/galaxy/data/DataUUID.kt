package one.oktw.galaxy.data

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataView
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData
import org.spongepowered.api.data.merge.MergeFunction
import org.spongepowered.api.data.persistence.AbstractDataBuilder
import org.spongepowered.api.data.value.immutable.ImmutableValue
import org.spongepowered.api.data.value.mutable.Value
import java.util.*
import java.util.UUID.randomUUID

class DataUUID(uuid: UUID) : AbstractSingleData<UUID, DataUUID, DataUUID.Immutable>(uuid, DataKeys.UUID) {
    override fun from(container: DataContainer?): Optional<DataUUID> {
        return Optional.of(this)
    }

    override fun copy(): DataUUID {
        return DataUUID(value)
    }

    override fun getContentVersion(): Int {
        return 1
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataUUID> {
        val data = dataHolder.get(DataUUID::class.java)

        data.ifPresent {
            val finalData = overlap.merge(this, it)
            value = finalData.value
        }

        return Optional.of(this)
    }

    override fun asImmutable(): Immutable {
        return Immutable(value)
    }

    override fun getValueGetter(): Value<UUID> {
        return Sponge.getRegistry().valueFactory.createValue(DataKeys.UUID, value)
    }

    class Immutable(uuid: UUID) : AbstractImmutableSingleData<UUID, Immutable, DataUUID>(uuid, DataKeys.UUID) {
        override fun getContentVersion(): Int {
            return 1
        }

        override fun asMutable(): DataUUID {
            return DataUUID(value)
        }

        override fun getValueGetter(): ImmutableValue<UUID> {
            return Sponge.getRegistry().valueFactory.createValue(DataKeys.UUID, value).asImmutable()
        }
    }

    class Builder : AbstractDataBuilder<DataUUID>(DataUUID::class.java, 1), DataManipulatorBuilder<DataUUID, Immutable> {
        override fun createFrom(dataHolder: DataHolder): Optional<DataUUID> {
            return create().fill(dataHolder)
        }

        override fun create(): DataUUID {
            return DataUUID(randomUUID())
        }

        override fun buildContent(container: DataView?): Optional<DataUUID> {
            return Optional.of(create())
        }
    }
}