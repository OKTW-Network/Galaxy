package one.oktw.galaxy.internal

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.ConfigManager.Companion.config
import org.bson.BsonReader
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.Conventions.*
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.json.JsonReader
import org.bson.json.JsonWriter
import org.bson.json.JsonWriterSettings
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.DataSerializable
import org.spongepowered.api.data.persistence.DataFormats
import java.io.StringWriter
import java.util.Arrays.asList

class DatabaseManager {
    companion object {
        lateinit var database: MongoDatabase
            private set
    }

    init {
        val config = config.getNode("mongodb")

        main.logger.info("Loading Database...")

        // Init Config
        if (config.isVirtual) {
            config.setComment(
                "MongoDB connect string. format:\n" +
                        "mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database.collection][?options]]"
            )
            config.value = "mongodb://localhost/oktw-galaxy"
        }

        database = PojoCodecProvider.builder() // POJO settings
            .automatic(true)
            .conventions(asList(SET_PRIVATE_FIELDS_CONVENTION, ANNOTATION_CONVENTION, CLASS_AND_PROPERTY_CONVENTION))
            .build()
            .let {
                // register codec
                fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(SpongeDataCodecProvider(), it)
                )
            }
            .let {
                // connect settings
                MongoClientSettings.builder()
                    .codecRegistry(it)
                    .applyConnectionString(ConnectionString(config.string))
                    .build()
            }
            .let(MongoClients::create) // connect
            .getDatabase(ConnectionString(config.string).database!!) // get database
    }

    class SpongeDataCodecProvider : CodecProvider {
        override fun <T : Any> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
            if (!DataSerializable::class.java.isAssignableFrom(clazz)) return null

            return object : Codec<T> {
                override fun getEncoderClass() = clazz

                override fun encode(writer: BsonWriter, value: T, encoderContext: EncoderContext) {
                    writer.pipe(JsonReader(DataFormats.JSON.write((value as DataSerializable).toContainer())))
                }

                override fun decode(reader: BsonReader, decoderContext: DecoderContext): T {
                    val json = StringWriter()
                    JsonWriter(json, JsonWriterSettings.builder()
                        // workaround https://github.com/SpongePowered/SpongeCommon/issues/1821
                        .doubleConverter { value, writer -> writer.writeString(value.toString()) }
                        .int64Converter { value, writer -> writer.writeString(value.toString()) }
                        .build()).pipe(reader)

                    @Suppress("UNCHECKED_CAST")
                    return Sponge.getDataManager().deserialize(
                        clazz as Class<out DataSerializable>,
                        DataFormats.JSON.read(json.toString())
                    ).get() as T
                }
            }
        }
    }
}
