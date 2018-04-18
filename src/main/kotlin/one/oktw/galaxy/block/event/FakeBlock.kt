package one.oktw.galaxy.block.event

import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockTypes.COMMAND_BLOCK
import org.spongepowered.api.block.tileentity.CommandBlock
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.data.type.HandTypes.OFF_HAND
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class FakeBlock {
    @Listener
    fun onClickBlock(event: InteractBlockEvent.Secondary, @First player: Player) {
        val location = event.targetBlock.location.orElse(null)?.getRelative(event.targetSide) ?: return
        val block = player.getItemInHand(MAIN_HAND).orElse(null)
                ?: player.getItemInHand(OFF_HAND).orElse(null)
                ?: return

        if (!isBlock(block)) return

        placeBlock(location, block)
        // TODO place sound
    }

    private fun isBlock(item: ItemStack): Boolean {
        return item.type == WOODEN_SWORD && item[Keys.UNBREAKABLE].orElse(false) == true
    }

    private fun placeBlock(location: Location<World>, block: ItemStack) {
        val item = 59 - block[Keys.ITEM_DURABILITY].get()

        BlockState.builder()
            .blockType(COMMAND_BLOCK)
            .build()
            .let(location::setBlock)

        location.offer(
            Keys.COMMAND,
            "setBlock ~ ~ ~ minecraft:mob_spawner 0 replace {SpawnData:{id:\"minecraft:armor_stand\",ArmorItems:[{},{},{},{id:\"minecraft:wooden_sword\",Count:1,Damage:$item,tag:{Unbreakable:1}}]},RequiredPlayerRange:0,MaxNearbyEntities:0}"
        )
        (location.tileEntity.get() as CommandBlock).execute()
    }
}
