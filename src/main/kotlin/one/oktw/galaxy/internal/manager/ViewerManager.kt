package one.oktw.galaxy.internal.manager

import java.util.*

class ViewerManager {
    private val viewer = ArrayList<UUID>()

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