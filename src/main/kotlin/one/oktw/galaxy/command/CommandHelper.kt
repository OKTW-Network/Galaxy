package one.oktw.galaxy.command

import one.oktw.galaxy.Main
import one.oktw.galaxy.command.data.GalaxyAndPlanet
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.planet.data.Planet
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.user.UserStorageService
import java.util.*

class CommandHelper {
    companion object {
        fun getPlayer(player: Player?, offlinePlayer: String?): Player {
            if (player == null) {
                if (offlinePlayer == null) {
                    throw IllegalArgumentException("Not enough arguments!")
                }
                val user: User? = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(offlinePlayer).orElse(null)
                if (user == null) {
                    throw IllegalArgumentException("Player not found")
                } else {
                    return user.player.get()
                }
            }
            return player
        }

        suspend fun getGalaxy(uuid: UUID?, src: CommandSource): Galaxy {
            var galaxy = Main.galaxyManager.get(uuid)
            //If galaxy(uuid) is null then get player galaxy
            if (galaxy == null && src is Player) galaxy = Main.galaxyManager.get(src.world)
            //If it is still null then return
            if (galaxy == null) {
                throw IllegalArgumentException("Not enough arguments!")
            }
            return galaxy
        }

        suspend fun getGalaxyAndPlanet(uuid: UUID?, src: CommandSource): GalaxyAndPlanet {
            val galaxy: Galaxy?
            val planet: Planet?
            var uuid1 = uuid
            //If uuid is null then get player planet uuid
            if (uuid1 == null && src is Player) {
                galaxy = Main.galaxyManager.get(src.world)
                planet = galaxy?.getPlanet(src.world)
                uuid1 = planet?.uuid
            } else {
                galaxy = Main.galaxyManager.get(planet = uuid1)
                planet = galaxy?.getPlanet(uuid1!!)
            }
            if (uuid1 == null) {
                throw IllegalArgumentException("Not enough arguments!")
            } else if (planet == null || galaxy == null) {
                throw IllegalArgumentException("Planet not found")
            }
            return GalaxyAndPlanet(galaxy, planet, uuid1)
        }

    }
}
