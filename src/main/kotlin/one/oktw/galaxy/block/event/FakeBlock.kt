package one.oktw.galaxy.block.event

import org.spongepowered.api.block.BlockState
import org.spongepowered.api.block.BlockTypes.MOB_SPAWNER
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.data.type.HandTypes.OFF_HAND
import org.spongepowered.api.entity.EntityArchetype
import org.spongepowered.api.entity.EntityTypes.ARMOR_STAND
import org.spongepowered.api.entity.living.ArmorStand
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.weighted.WeightedSerializableObject
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class FakeBlock {
    @Listener
    fun onClickBlock(event: InteractBlockEvent.Secondary, @First player: Player) {
        val block = player.getItemInHand(MAIN_HAND).orElse(null)
                ?: player.getItemInHand(OFF_HAND).orElse(null)
                ?: return

        if (!isBlock(block)) return

        placeBlock(event.targetBlock.location.get().getRelative(event.targetSide), block)
        // TODO place sound
    }

    private fun isBlock(item: ItemStack): Boolean {
        return item.type == WOODEN_SWORD && item[Keys.UNBREAKABLE].orElse(false) == true
    }

    private fun placeBlock(location: Location<World>, block: ItemStack) {
        (location.createEntity(ARMOR_STAND) as ArmorStand)
            .apply { setHelmet(block) }
            .let { WeightedSerializableObject(EntityArchetype.builder().from(it).build(), 1) }
            .let {
                location.apply {
                    setBlock(BlockState.builder().blockType(MOB_SPAWNER).build())
                    offer(Keys.SPAWNER_REQUIRED_PLAYER_RANGE, 0)
                    offer(Keys.SPAWNER_NEXT_ENTITY_TO_SPAWN, it)
                    // TODO fix rotation
                }
            }
    }
}