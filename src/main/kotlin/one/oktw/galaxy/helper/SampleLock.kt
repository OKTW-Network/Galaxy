package one.oktw.galaxy.helper

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SampleLock {
    companion object {
        private val lock = ConcurrentHashMap.newKeySet<UUID>()

        fun checkLocked(uuid: UUID): Boolean {
            return lock.contains(uuid)
        }

        fun lock(uuid: UUID): Boolean {
            return lock.add(uuid)
        }

        fun unlock(uuid: UUID): Boolean {
            return lock.remove(uuid)
        }
    }
}