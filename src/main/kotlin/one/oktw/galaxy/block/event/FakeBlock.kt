package one.oktw.galaxy.block.event

import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import one.oktw.galaxy.block.data.FakeBlockItem
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.event.PlaceCustomBlockEvent
import one.oktw.galaxy.event.RemoveCustomBlockEvent
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ToolType.WRENCH
import one.oktw.galaxy.item.type.Tool
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockTypes.COMMAND_BLOCK
import org.spongepowered.api.block.tileentity.CommandBlock
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.data.type.HandTypes.OFF_HAND
import org.spongepowered.api.effect.sound.SoundCategories.BLOCK
import org.spongepowered.api.effect.sound.SoundTypes.BLOCK_STONE_PLACE
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.world.ExplosionEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.AABB
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class FakeBlock {
    @Listener
    fun onPlaceBlock(event: InteractBlockEvent.Secondary, @First player: Player) {
        val location = event.targetBlock.location.orElse(null)?.getRelative(event.targetSide) ?: return
        var hand: HandType = MAIN_HAND
        val blockItem = player.getItemInHand(MAIN_HAND).orElse(null)
                ?: player.getItemInHand(OFF_HAND).orElse(null)?.apply { hand = OFF_HAND }
                ?: return

        if (!isBlock(blockItem) || !checkCanPlace(location)) return

        if (placeBlock(blockItem, location)) {
            PlaceCustomBlockEvent(location, blockItem, event.cause).let {
                Sponge.getEventManager().post(it)
            }

            playPlaceSound(player)
            consumeItem(player, hand)
        }
    }

    @Listener
    fun onBreakBlock(event: InteractBlockEvent.Primary) {
        if (event.targetBlock[DataBlockType.key].isPresent) {
            event.isCancelled = true

            event.targetBlock.location.ifPresent {
                (it.extent as WorldServer).playerChunkMap.markBlockForUpdate(BlockPos(it.blockX, it.blockY, it.blockZ))
            }
        }
    }

    @Listener
    fun onExplosion(event: ExplosionEvent.Detonate) {
        event.affectedLocations.removeIf { it[DataBlockType::class.java].isPresent }
    }

    @Listener
    fun onUseWrench(event: InteractBlockEvent.Secondary, @First player: Player) {
        if (player[IS_SNEAKING].orElse(false) == false) return
        if (player.getItemInHand(event.handType).orElse(null)?.run(Tool(WRENCH)::test) != true) return

        val location = event.targetBlock.location.orElse(null) ?: return
        val entity = location.createEntity(EntityTypes.ITEM)
        val block = location[DataBlockType.key].orElse(null)?.apply { if (id == null) return } ?: return
        val item = FakeBlockItem(block).createItemStack().createSnapshot()

        entity.offer(REPRESENTED_ITEM, item)
        location.removeBlock()
        location.spawnEntity(entity)

        RemoveCustomBlockEvent(location, event.cause).let {
            Sponge.getEventManager().post(it)
        }
    }

    private fun isBlock(item: ItemStack): Boolean {
        return item[DataItemType.key].orElse(null) == ItemType.BLOCK && item[DataBlockType.key].isPresent
    }

    private fun checkCanPlace(location: Location<World>): Boolean {
        val box = location.blockPosition.let { AABB(it, it.add(1, 1, 1)) }

        return location.extent.run { getIntersectingBlockCollisionBoxes(box).isEmpty() && getIntersectingEntities(box).isEmpty() }
    }

    private fun placeBlock(blockItem: ItemStack, location: Location<World>): Boolean {
        val item = 59 - blockItem[ITEM_DURABILITY].get()

        location.run {
            blockType = COMMAND_BLOCK

            // test place block success
            val commandBlock = tileEntity.orElse(null) as? CommandBlock ?: return false

            // place spawner
            offer(
                COMMAND,
                "setblock ~ ~ ~ minecraft:mob_spawner 0 replace {SpawnData:{id:\"minecraft:armor_stand\",ArmorItems:[{},{},{},{id:\"minecraft:wooden_sword\",Count:1,Damage:$item,tag:{Unbreakable:1}}]},RequiredPlayerRange:0,MaxNearbyEntities:0}"
            )
            commandBlock.execute()

            // offer block type data
            offer(DataBlockType(blockItem[DataBlockType.key].get()))

            return true
        }
    }

    private fun playPlaceSound(player: Player) {
        player.world.playSound(BLOCK_STONE_PLACE, BLOCK, player.position, 1.0)
    }

    private fun consumeItem(player: Player, hand: HandType) {
        if (player.gameMode().get() == GameModes.CREATIVE) return

        val item = player.getItemInHand(hand).orElse(null) ?: return

        item.quantity--

        if (item.quantity <= 0) player.setItemInHand(hand, ItemStack.empty()) else player.setItemInHand(hand, item)
    }
}
