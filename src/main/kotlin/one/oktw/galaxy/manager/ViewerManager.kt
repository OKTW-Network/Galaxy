package one.oktw.galaxy.manager

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class ViewerManager {
    private val viewer = ConcurrentHashMap.newKeySet<UUID>()

    fun setViewer(uuid: UUID) {
        if (viewer.contains(uuid)) return

        viewer += uuid
    }

    fun isViewer(uuid: UUID): Boolean {
        return viewer.contains(uuid)
    }

    fun removeViewer(uuid: UUID) {
        viewer -= uuid
    }
}
