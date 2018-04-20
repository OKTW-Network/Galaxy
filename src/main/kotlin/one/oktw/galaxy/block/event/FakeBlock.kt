package one.oktw.galaxy.block.event

import org.spongepowered.api.block.BlockTypes.COMMAND_BLOCK
import org.spongepowered.api.block.tileentity.CommandBlock
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.key.Keys.UNBREAKABLE
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.data.type.HandTypes.OFF_HAND
import org.spongepowered.api.effect.sound.SoundCategories.BLOCK
import org.spongepowered.api.effect.sound.SoundTypes.BLOCK_STONE_PLACE
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class FakeBlock {
    @Listener
    fun onPlaceBlock(event: InteractBlockEvent.Secondary, @First player: Player) {
        val location = event.targetBlock.location.orElse(null)?.getRelative(event.targetSide) ?: return
        var hand: HandType = MAIN_HAND
        val block = player.getItemInHand(MAIN_HAND).orElse(null)
                ?: player.getItemInHand(OFF_HAND).orElse(null)?.apply { hand = OFF_HAND }
                ?: return

        if (!isBlock(block) || !checkCanPlace(location)) return

        placeBlock(block, location)
        playPlaceSound(player)
        consumeItem(player, hand)
    }

    private fun isBlock(item: ItemStack): Boolean {
        return item.type == WOODEN_SWORD && item[UNBREAKABLE].orElse(false) == true
    }

    private fun checkCanPlace(location: Location<World>): Boolean {
        val box = location.blockPosition.let { AABB(it, it.add(1, 1, 1)) }

        return location.extent.run { getIntersectingBlockCollisionBoxes(box).isEmpty() && getIntersectingEntities(box).isEmpty() }
    }

    private fun placeBlock(block: ItemStack, location: Location<World>) {
        val item = 59 - block[Keys.ITEM_DURABILITY].get()
        val command =
            "setblock ~ ~ ~ minecraft:mob_spawner 0 replace {SpawnData:{id:\"minecraft:armor_stand\",ArmorItems:[{},{},{},{id:\"minecraft:wooden_sword\",Count:1,Damage:$item,tag:{Unbreakable:1}}]},RequiredPlayerRange:0,MaxNearbyEntities:0}"

        location.apply {
            blockType = COMMAND_BLOCK
            offer(Keys.COMMAND, command)
            (tileEntity.get() as CommandBlock).execute()
        }
    }

    private fun playPlaceSound(player: Player) {
        player.playSound(BLOCK_STONE_PLACE, BLOCK, player.position, 1.0)
    }

    private fun consumeItem(player: Player, hand: HandType) {
        if (player.gameMode().get() == GameModes.CREATIVE) return

        val item = player.getItemInHand(hand).orElse(null) ?: return

        item.quantity--

        if (item.quantity <= 0) player.setItemInHand(hand, ItemStack.empty()) else player.setItemInHand(hand, item)
    }
}
