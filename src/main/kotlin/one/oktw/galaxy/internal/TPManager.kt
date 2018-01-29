package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.concurrent.TimeUnit



class TPManager {

    companion object {

        fun Teleport( player : Player , location : Location<World> , safty : Boolean = false, delay : Long = 3){

            val first_location = player.location

            if(safty){
                player.sendMessage(Text.of(TextColors.YELLOW,"請站在原地不要移動，將在 " + delay.toString() + " 秒後傳送......").toText())
                Task.builder()
                        .execute{->
                            if (first_location.equals(player.location)) {
                                player.setLocationSafely(location)
                                player.sendMessage(Text.of(TextColors.GREEN, "傳送成功").toText())
                            } else {
                                player.sendMessage(Text.of(TextColors.RED, "位置移動，傳送取消").toText())
                            }
                        }
                        .delay(delay,TimeUnit.SECONDS).submit(main);
            }else{
                player.sendMessage(Text.of(TextColors.YELLOW,"請站在原地不要移動，將在 " + delay.toString() + " 秒後傳送......").toText())
                Task.builder()
                        .execute{->
                            if (first_location.equals(player.location)) {
                                player.setLocation(location)
                                player.sendMessage(Text.of(TextColors.GREEN,"傳送成功").toText())
                            }else{
                                player.sendMessage(Text.of(TextColors.RED, "位置移動，傳送取消").toText())
                            }

                    }.delay(delay,TimeUnit.SECONDS).submit(main);
                }
            }
        }
}