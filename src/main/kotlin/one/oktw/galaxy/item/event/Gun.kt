/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.item.event

import com.flowpowered.math.imaginary.Quaterniond
import com.flowpowered.math.vector.Vector3d
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EntityDamageSource
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.data.DataEnable
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.event.CustomItemCraftEvent
import one.oktw.galaxy.event.PostHiTectCraftEvent
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.getTraveler
import one.oktw.galaxy.item.enums.ItemType
import one.oktw.galaxy.item.enums.ItemType.PISTOL
import one.oktw.galaxy.item.enums.ItemType.SNIPER
import one.oktw.galaxy.item.enums.UpgradeType.*
import one.oktw.galaxy.item.service.CoolDown
import one.oktw.galaxy.item.type.Gun
import one.oktw.galaxy.translation.extensions.toLegacyText
import org.spongepowered.api.block.BlockTypes.*
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.data.manipulator.mutable.entity.SneakingData
import org.spongepowered.api.data.property.entity.EyeLocationProperty
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.data.type.HandTypes.OFF_HAND
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.api.effect.sound.SoundCategories
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.entity.living.ArmorStand
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.monster.Boss
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.data.ChangeDataHolderEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.filter.data.Has
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractItemEvent
import org.spongepowered.api.item.ItemTypes.DIAMOND_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult
import org.spongepowered.api.util.blockray.BlockRay
import org.spongepowered.api.world.World
import org.spongepowered.api.world.extent.EntityUniverse.EntityHit
import java.lang.Math.random
import java.util.Arrays.asList
import kotlin.math.roundToInt

class Gun {
    val lang = Main.translationService

    @Listener
    @Suppress("unused")
    fun onInteractItem(event: InteractItemEvent.Secondary.MainHand, @Getter("getSource") player: Player) {
        val itemStack = event.itemStack
        if (itemStack.type != DIAMOND_SWORD || !itemStack[DataUUID.key].isPresent) return

        val world = player.world
        var direction =
            Quaterniond.fromAxesAnglesDeg(player.rotation.x, -player.rotation.y, player.rotation.z).direction
        val source = player.getProperty(EyeLocationProperty::class.java)
            .map(EyeLocationProperty::getValue).orElse(null)?.add(direction) ?: return

        GlobalScope.launch {
            val gun = (getTraveler(player)!!.item
                .filter { it is Gun }
                .find { (it as Gun).uuid == itemStack[DataUUID.key].get() } as? Gun ?: return@launch)
                .copy()
                .run(::doUpgrade)

            if (checkOverheat(world, source, gun).await()) return@launch

            showActionBar(player)

            if (!player[IS_SNEAKING].get()) {
                direction = drift(direction)
            }

            val target = getTarget(world, source, direction, gun.range).await()
            val wall = getWall(
                world,
                source.sub(direction),
                direction,
                if (!target.isEmpty()) target.first().intersection.distance(source) else gun.range
            ).await()

            if (!target.isEmpty() && !wall.hasNext()) damageEntity(player, source, gun, target)

            showTrajectory(
                world, source, when {
                    wall.hasNext() -> wall.next().position.sub(source)
                    !target.isEmpty() -> target.first().intersection.sub(source)
                    else -> direction.mul(gun.range)
                }
            )

            playShotSound(world, source, gun.itemType)
        }
    }

    @Listener
    @Suppress("unused", "UNUSED_PARAMETER")
    fun onChangeDataHolder(event: ChangeDataHolderEvent.ValueChange, @Getter("getTargetHolder") @Has(SneakingData::class) player: Player) {
        player.getItemInHand(MAIN_HAND).filter { it[DataEnable.key].isPresent }.ifPresent {
            val sneak: Boolean = event.endResult.successfulData.firstOrNull { it.key == IS_SNEAKING }?.get() as Boolean?
                    ?: player[IS_SNEAKING].get()
            if (it[DataEnable.key].get() != sneak) {
                player.setItemInHand(MAIN_HAND, toggleScope(it))
            }

            if (sneak) player.offer(WALKING_SPEED, -10.0) else player.offer(WALKING_SPEED, 0.1)
        }
    }

    @Listener
    fun onSwapHand(event: ChangeInventoryEvent.SwapHand, @Root player: Player) = onChangeInventory(event, player)

    @Listener
    fun onHeld(event: ChangeInventoryEvent.Held, @Root player: Player) = onChangeInventory(event, player)

    @Suppress("unused", "UNUSED_PARAMETER")
    private fun onChangeInventory(event: ChangeInventoryEvent, @Getter("getSource") player: Player) {
        val mainHand = player.getItemInHand(MAIN_HAND).filter { it[DataEnable.key].isPresent }.orElse(null)
        mainHand?.let {
            val sneak = player[IS_SNEAKING].get()
            if (it[DataEnable.key].get() != sneak) {
                player.setItemInHand(MAIN_HAND, toggleScope(it))
            }

            if (sneak) player.offer(WALKING_SPEED, -10.0) else player.offer(WALKING_SPEED, 0.1)
        }

        if (mainHand == null) player.offer(WALKING_SPEED, 0.1)

        player.getItemInHand(OFF_HAND).filter { it[DataEnable.key].isPresent }.ifPresent {
            if (it[DataEnable.key].get()) player.setItemInHand(OFF_HAND, toggleScope(it))
        }

        GlobalScope.launch { showActionBar(player) }
    }

    private fun drift(direction: Vector3d): Vector3d {
        return direction.mul(10.0).add(Math.random(), Math.random(), Math.random())
            .sub(Math.random(), Math.random(), Math.random()).div(10.0)
    }

    private fun doUpgrade(gun: Gun) = gun.apply {
        upgrade.forEach {
            when (it.type) {
                DAMAGE -> gun.damage += it.level * 3
                RANGE -> gun.range += it.level * 5
                COOLING -> gun.cooling += it.level * 2
                THROUGH -> gun.through += it.level
                HEAT -> gun.maxTemp += it.level * 5
                else -> Unit
            }
        }
    }

    private fun checkOverheat(world: World, source: Vector3d, gun: Gun) = GlobalScope.async {
        if (CoolDown.getHeat(gun).overheated) return@async true

        if (CoolDown.heating(gun).overheated) {
            world.playSound(SoundType.of("gun.overheat"), SoundCategories.PLAYER, source, 1.0)
        }

        return@async false
    }

    private fun getTarget(world: World, source: Vector3d, direction: Vector3d, range: Double) = GlobalScope.async {
        world.getIntersectingEntities(source, direction, range) {
            it.entity is Living && it.entity !is Player && it.entity !is ArmorStand && (it.entity as EntityLivingBase).isEntityAlive
        }
    }

    private fun getWall(world: World, source: Vector3d, direction: Vector3d, range: Double) = GlobalScope.async(serverThread) {
        BlockRay.from(world, source)
            .direction(direction)
            .distanceLimit(range)
            .skipFilter {
                when (it.location.blockType) {
                    AIR,
                    GLASS, STAINED_GLASS,
                    GLASS_PANE, STAINED_GLASS_PANE,
                    IRON_BARS,
                    TALLGRASS, DOUBLE_PLANT,
                    TORCH, REDSTONE_TORCH, UNLIT_REDSTONE_TORCH,
                    REEDS,
                    LEAVES, LEAVES2 -> false

                    else -> true
                }
            }.build()
    }

    private fun damageEntity(player: Player, source: Vector3d, gun: Gun, target: Set<EntityHit>) {
        target.stream()
            .filter { it.entity !is Boss }
            .sorted { hit1, hit2 -> ((hit1.intersection.distance(source) - hit2.intersection.distance(source)) * 10).roundToInt() }
            .limit(gun.through.toLong())
            .forEach {
                val entity = it.entity as Living

                GlobalScope.launch(serverThread) {
                    (entity as EntityLivingBase).hurtResistantTime = 0

                    entity.damage(
                        gun.damage,
                        EntityDamageSource("player", player as EntityPlayer).setProjectile() as DamageSource
                    )

                    gun.damage *= 0.9
                }

                if ((entity as EntityLivingBase).isEntityAlive) {
                    player.playSound(
                        SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategories.PLAYER,
                        source,
                        1.0
                    )
                } else {
                    player.playSound(
                        SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategories.PLAYER,
                        source,
                        1.0,
                        0.5
                    )
                }
            }
    }

    private fun showTrajectory(world: World, source: Vector3d, line: Vector3d) = GlobalScope.async {
        val interval = when (line.abs().maxAxis) {
            0 -> line.abs().x.div(0.3)
            1 -> line.abs().y.div(0.3)
            2 -> line.abs().z.div(0.3)
            else -> 10.0
        }
        var pos = source

        for (i in 0..interval.roundToInt()) {
            world.spawnParticles(
                ParticleEffect.builder()
                    .type(ParticleTypes.MAGIC_CRITICAL_HIT)
                    .build(),
                pos
            )
            pos = pos.add(line.div(interval))
        }
    }

    private fun playShotSound(world: World, position: Vector3d, type: ItemType) = GlobalScope.async {
        when (type) {
            PISTOL -> world.playSound(
                SoundType.of("gun.shot"),
                SoundCategories.PLAYER,
                position,
                1.0,
                1 + random() / 10 - random() / 10
            )
            SNIPER -> {
                // TODO new sound
                world.playSound(
                    SoundType.of("entity.blaze.hurt"),
                    SoundCategories.PLAYER,
                    position,
                    1.0,
                    2.0
                )
                world.playSound(
                    SoundType.of("entity.firework.blast"),
                    SoundCategories.PLAYER,
                    position,
                    1.0,
                    0.0
                )
                world.playSound(
                    SoundType.of("block.piston.extend"),
                    SoundCategories.PLAYER,
                    position,
                    1.0,
                    2.0
                )
            }
            else -> Unit
        }
    }

    private fun toggleScope(item: ItemStack) = item.apply {
        transform(ITEM_DURABILITY) { if (item[DataEnable.key].get()) it - 1 else it + 1 }

        transform(DataEnable.key) { !it }
    }

    private suspend fun showActionBar(player: Player) {
        val traveler = getTraveler(player) ?: return
        val gun1 = player.getItemInHand(MAIN_HAND).orElse(null)?.run {
            traveler.item.filterIsInstance(Gun::class.java).firstOrNull { it.uuid == get(DataUUID.key).orElse(null) }
        }?.copy()?.let { doUpgrade(it) }
        val gun2 = player.getItemInHand(OFF_HAND).orElse(null)?.run {
            traveler.item.filterIsInstance(Gun::class.java).firstOrNull { it.uuid == get(DataUUID.key).orElse(null) }
        }?.copy()?.let { doUpgrade(it) }

        gun1?.let { CoolDown.showActionBar(player, gun1, gun2) }
    }

    @Listener
    private fun onGunItemCraft(event: PostHiTectCraftEvent) {
        if (event.item[DataItemType.key].orElse(null) in asList(ItemType.PISTOL, ItemType.SNIPER)) {
            event.isCancelled = true
        }
    }

    @Listener
    private fun onGunCraft(event: CustomItemCraftEvent) {
        (event.item as? Gun)?.let {
            val player = event.player
            val traveler = event.traveler
            traveler.item.add(it)
            val res = player.inventory.offer(it.createItemStack())
            if (res.type != InventoryTransactionResult.Type.SUCCESS) {
                event.player.sendMessage(lang.of("Respond.weaponDelivered").toLegacyText(player))
            }
        }
    }
}
