package one.oktw.galaxy.internal

import com.mongodb.client.model.Filters.eq
import one.oktw.galaxy.Main.Companion.databaseManager
import one.oktw.galaxy.internal.Groups.ADMIN
import one.oktw.galaxy.internal.Groups.MEMBER
import org.bson.Document
import java.util.*
import java.util.stream.Collectors.toList


class GalaxyManager {
    companion object {
        private val database = databaseManager.database.getCollection("Galaxy")

        fun createGalaxy(galaxyManager: GalaxyManager, name: String, creator: UUID, vararg members: UUID): Galaxy {
            val uuid = UUID.randomUUID()
            val memberList = Arrays.asList(*members).parallelStream()
                    .map { member -> Document("UUID", member).append("Group", MEMBER) }
                    .collect(toList())
            memberList += Document("UUID", creator).append("Group", ADMIN)
            val document = Document("UUID", uuid)
                    .append("Name", name)
                    .append("Members", memberList)
            database.insertOne(document)
            return Companion.getGalaxy(uuid)
        }

        fun getGalaxy(uuid: UUID): Galaxy {
            return Galaxy(uuid)
        }

        fun searchGalaxy(galaxyManager: GalaxyManager, name: String): ArrayList<Galaxy> {
            val galaxyList = ArrayList<Galaxy>()
            database.find(eq("Name", name)).forEach(
                    { document: Document -> galaxyList += Galaxy(document["UUID"] as UUID) }
            )
            return galaxyList
        }
    }
}
