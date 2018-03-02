package one.oktw.galaxy.manager

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import one.oktw.galaxy.Main.Companion.configManager
import one.oktw.galaxy.Main.Companion.main
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
    val database: MongoDatabase

    init {
        val config = configManager.configNode.getNode("database")

        main.logger.info("Loading Database...")

        // Init Config
        if (config.isVirtual) {
            config.setComment("Mongodb connect setting")
            config.getNode("host").value = "localhost"
            config.getNode("port").value = 27017
            config.getNode("name").value = "oktw"
            config.getNode("name").setComment("Database name")
            config.getNode("Username").value = ""
            config.getNode("Password").value = ""
            configManager.save()
        }

        // Init Database connect
        val serverAddress = ServerAddress(
            config.getNode("host").string,
            config.getNode("port").int
        )

        val pojoCodecRegistry = fromRegistries(
            MongoClient.getDefaultCodecRegistry(),
            fromProviders(
                SpongeDataCodecProvider(),
                PojoCodecProvider.builder()
                    .register("one.oktw.galaxy.types", "one.oktw.galaxy.types.item")
//                                .automatic(true)
                    .conventions(
                        asList(
                            SET_PRIVATE_FIELDS_CONVENTION,
                            ANNOTATION_CONVENTION,
                            CLASS_AND_PROPERTY_CONVENTION
                        )
                    )
                    .build()
            )
        )

        database = if (config.getNode("Username").string.isEmpty()) {
            MongoClient(serverAddress)
                .getDatabase(config.getNode("name").string)
                .withCodecRegistry(pojoCodecRegistry)
        } else {
            val credential = MongoCredential.createCredential(
                config.getNode("Username").string,
                config.getNode("name").string,
                config.getNode("Password").string.toCharArray()
            )

            MongoClient(
                serverAddress,
                credential,
                MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build()
            ).getDatabase(config.getNode("name").string)
        }
    }

    class SpongeDataCodecProvider : CodecProvider {
        override fun <T : Any> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
            if (DataSerializable::class.java.isAssignableFrom(clazz)) {
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

                        main.logger.info(json.toString())

                        @Suppress("UNCHECKED_CAST")
                        return Sponge.getDataManager().deserialize(
                            clazz as Class<out DataSerializable>,
                            DataFormats.JSON.read(json.toString())
                        ).get() as T
                    }
                }
            }
            return null
        }
    }
}
