package one.oktw.galaxy.event

import one.oktw.galaxy.Main
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.block.CollideBlockEvent
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.entity.CollideEntityEvent
import org.spongepowered.api.event.entity.DamageEntityEvent
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.entity.ai.SetAITargetEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.event.item.inventory.InteractItemEvent
import org.spongepowered.api.item.ItemTypes

class Viewer {

    @Listener
    fun onTarget(event: SetAITargetEvent, @Getter("getTarget") player: Player) {
        if (Main.viewerManager.isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener
    fun onDropItem(event: DropItemEvent.Pre, @Getter("getSource") player: Player) {
        if (Main.viewerManager.isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener
    fun onSpawnEntity(event: SpawnEntityEvent) {
        val source = event.cause.allOf(Player::class.java).any { Main.viewerManager.isViewer(it.uniqueId) }

        if (source) event.isCancelled = true
    }

    @Listener
    fun onChangeBlock(event: ChangeBlockEvent, @Getter("getSource") player: Player) {
        if (Main.viewerManager.isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener
    fun onInteractBlock(event: InteractBlockEvent, @Getter("getSource") player: Player) {
        if (Main.viewerManager.isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener
    fun onInteractEntity(event: InteractEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        val target = entity is Player && Main.viewerManager.isViewer(entity.uniqueId)
        val source = event.cause.allOf(Player::class.java).any { Main.viewerManager.isViewer(it.uniqueId) }

        if (target || source) event.isCancelled = true
    }

    @Listener
    fun onInteractItem(event: InteractItemEvent.Secondary, @Getter("getSource") player: Player) {
        if (Main.viewerManager.isViewer(player.uniqueId)) when (event.itemStack.type) {
            ItemTypes.BOW -> event.isCancelled = true
            ItemTypes.ENDER_PEARL -> event.isCancelled = true
            ItemTypes.ENDER_EYE -> event.isCancelled = true
            ItemTypes.SNOWBALL -> event.isCancelled = true
            ItemTypes.WATER_BUCKET -> event.isCancelled = true
            ItemTypes.LAVA_BUCKET -> event.isCancelled = true
            ItemTypes.BUCKET -> event.isCancelled = true
            ItemTypes.SPLASH_POTION -> event.isCancelled = true
            ItemTypes.LINGERING_POTION -> event.isCancelled = true
            ItemTypes.FISHING_ROD -> event.isCancelled = true
            ItemTypes.BOAT -> event.isCancelled = true
            ItemTypes.ACACIA_BOAT -> event.isCancelled = true
            ItemTypes.BIRCH_BOAT -> event.isCancelled = true
            ItemTypes.DARK_OAK_BOAT -> event.isCancelled = true
            ItemTypes.JUNGLE_BOAT -> event.isCancelled = true
            ItemTypes.SPRUCE_BOAT -> event.isCancelled = true
            ItemTypes.MAP -> event.isCancelled = true
            ItemTypes.EXPERIENCE_BOTTLE -> event.isCancelled = true
            ItemTypes.CHORUS_FRUIT -> event.isCancelled = true
            ItemTypes.DRAGON_BREATH -> event.isCancelled = true
            ItemTypes.WOODEN_SWORD -> event.isCancelled = true //Gun
        }
    }

    @Listener
    fun onDamageEntity(event: DamageEntityEvent) {
        val target = event.targetEntity is Player && Main.viewerManager.isViewer(event.targetEntity.uniqueId)
        val source = event.cause.allOf(Player::class.java).any { Main.viewerManager.isViewer(it.uniqueId) }

        if (target || source) event.isCancelled = true
    }

    @Listener
    fun onCollideEntity(event: CollideEntityEvent) {
        val source = event.cause.filterIsInstance<Player>().any { Main.viewerManager.isViewer(it.uniqueId) }

        if (source) {
            event.isCancelled = true
        } else {
            event.filterEntities { !Main.viewerManager.isViewer(it.uniqueId) }
        }
    }

    @Listener
    fun onCollideBlock(event: CollideBlockEvent, @Getter("getSource") player: Player) {
        if (Main.viewerManager.isViewer(player.uniqueId)) event.isCancelled = true
    }

    // TODO add more Listener
}