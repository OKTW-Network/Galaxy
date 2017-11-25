package one.oktw.galaxy.internal.galaxy

import com.mongodb.client.model.Filters.eq
import one.oktw.galaxy.Main
import one.oktw.galaxy.internal.galaxy.Groups.ADMIN
import one.oktw.galaxy.internal.galaxy.Groups.MEMBER
import org.bson.Document
import java.util.*
import java.util.stream.Collectors.toList


class GalaxyManager {
    private val database = Main.databaseManager.database.getCollection("Galaxy")

    fun createGalaxy(name: String, creator: UUID, vararg members: UUID): Galaxy {
        val uuid = UUID.randomUUID()
        val memberList = Arrays.asList(*members).parallelStream()
                .map { member -> Document("UUID", member).append("Group", MEMBER) }
                .collect(toList())
        memberList += Document("UUID", creator).append("Group", ADMIN)
        val document = Document("UUID", uuid)
                .append("Name", name)
                .append("Members", memberList)
        database.insertOne(document)
        return getGalaxy(uuid)
    }

    fun getGalaxy(uuid: UUID): Galaxy {
        return Galaxy(uuid)
    }

    fun searchGalaxy(name: String): ArrayList<Galaxy> {
        val galaxyList = ArrayList<Galaxy>()
        database.find(eq("Name", name)).forEach(
                { document: Document -> galaxyList += Galaxy(document["UUID"] as UUID) }
        )
        return galaxyList
    }
}
