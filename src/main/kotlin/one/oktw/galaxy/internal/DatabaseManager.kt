/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.internal

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClients
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.Conventions.*
import org.bson.codecs.pojo.PojoCodecProvider

class DatabaseManager(dbPath: String) {
    companion object {
        lateinit var database: MongoDatabase
            private set
    }

    init {
        database = PojoCodecProvider.builder() // POJO settings
            .automatic(true)
            .conventions(listOf(SET_PRIVATE_FIELDS_CONVENTION, ANNOTATION_CONVENTION, CLASS_AND_PROPERTY_CONVENTION))
            .build()
            .let {
                // register codec
                fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry()
                )
            }
            .let {
                // connect settings
                MongoClientSettings.builder()
                    .codecRegistry(it)
                    .applyConnectionString(ConnectionString(dbPath))
                    .build()
            }
            .let(MongoClients::create) // connect
            .getDatabase(ConnectionString(dbPath).database!!) // get database
    }
}
