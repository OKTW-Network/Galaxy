package one.oktw.galaxy.internal

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.scheduler.Task;
import java.util.concurrent.TimeUnit

class TPManager {

    companion object {

        fun Teleport( player : Player , location : Location<World> , safty : Boolean = false, delay : Long = 3){

            if(safty){
                val teleportTask = Task.builder().execute(Runnable { player.setLocationSafely(location) }).delay(delay,TimeUnit.SECONDS)
            }else{
                val teleportTask = Task.builder().execute(Runnable { player.setLocation(location) }).delay(delay,TimeUnit.SECONDS)
            }

        }
    }

}