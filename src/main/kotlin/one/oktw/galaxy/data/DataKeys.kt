package one.oktw.galaxy.data

import org.spongepowered.api.data.DataQuery
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.util.TypeTokens
import java.util.*

class DataKeys {
    companion object {
        val UUID: Key<Value<UUID>> = Key.builder()
                .type(TypeTokens.UUID_VALUE_TOKEN)
                .query(DataQuery.of("UUID"))
                .id("uuid")
                .name("UUID")
                .build()
    }
}
