package one.oktw.galaxy.data

import com.google.common.reflect.TypeToken
import one.oktw.galaxy.enums.ItemType
import one.oktw.galaxy.enums.ItemType.EMPTY
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

class DataType(type: ItemType = EMPTY) :
    AbstractSingleEnumData<ItemType, DataType, DataType.Immutable>(type, key, EMPTY) {
    companion object {
        val key: Key<Value<ItemType>> = Key.builder()
            .type(object : TypeToken<Value<ItemType>>() {})
            .id("type")
            .name("Type")
            .query(DataQuery.of("type"))
            .build()
    }

    override fun getContentVersion() = 1
    override fun asImmutable() = Immutable(value)
    override fun copy() = DataType(value)

    override fun from(container: DataContainer): Optional<DataType> {
        return if (container[key.query].isPresent) {
            value = container.getObject(key.query, ItemType::class.java).get()
            Optional.of(this)
        } else {
            Optional.empty()
        }
    }

    override fun fill(dataHolder: DataHolder, overlap: MergeFunction): Optional<DataType> {
        value = overlap.merge(this, dataHolder[DataType::class.java].orElse(null)).value
        return Optional.of(this)
    }


    class Immutable(type: ItemType = EMPTY) :
        AbstractImmutableSingleEnumData<ItemType, Immutable, DataType>(type, EMPTY, key) {
        override fun getContentVersion() = 1
        override fun asMutable() = DataType((value))
    }

    class Builder : AbstractDataBuilder<DataType>(DataType::class.java, 1),
        DataManipulatorBuilder<DataType, Immutable> {
        override fun create() = DataType()
        override fun createFrom(dataHolder: DataHolder): Optional<DataType> = create().fill(dataHolder)
        override fun buildContent(container: DataView) = create().from(container.copy())
    }
}