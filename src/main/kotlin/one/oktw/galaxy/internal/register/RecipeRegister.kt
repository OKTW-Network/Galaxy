package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.item.enums.ToolType.WRENCH
import one.oktw.galaxy.item.enums.UpgradeType.*
import one.oktw.galaxy.item.type.Tool
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.key.Keys.POTION_EFFECTS
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.recipe.crafting.Ingredient.builder
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe

class RecipeRegister {
    init {
        val lapis = of(ItemStack.of(DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.BLUE) })
        val boneMeal = of(ItemStack.of(DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.WHITE) })

        Sponge.getRegistry().craftingRecipeRegistry.apply {
            // Wrench
            register(
                ShapedCraftingRecipe.builder().aisle("i i", " s ", " i ")
                    .where('i', of(IRON_INGOT))
                    .where('s', of(STICK))
                    .result(Tool(WRENCH).createItemStack())
                    .build("wrench", main)
            )

            // Advance Crafting System
            register(
                ShapedCraftingRecipe.builder().aisle("rdl", "ici", "lor")
                    .where('r', of(REDSTONE))
                    .where('d', of(DIAMOND))
                    .where('l', lapis)
                    .where('i', of(IRON_INGOT))
                    .where('c', of(CRAFTING_TABLE))
                    .where('o', of(OBSIDIAN))
                    .result(ItemStack.of(CRAFTING_TABLE, 1)) // TODO custom block
                    .build("advance_crafting_system", main)
            )

            // Upgrade base
            register(
                ShapedCraftingRecipe.builder().aisle(" i ", "rgr", " q ")
                    .where('i', of(GOLD_INGOT))
                    .where('r', of(REDSTONE))
                    .where('g', of(GLASS_PANE))
                    .where('q', of(QUARTZ))
                    .result(Upgrade(BASE).createItemStack())
                    .group("upgrade")
                    .build("upgrade_base", main)
            )

            // Range Upgrade
            register(
                ShapedCraftingRecipe.builder().aisle("aba", "cuc", "ada")
                    .where('a', of(IRON_INGOT))
                    .where('b', of(DIAMOND))
                    .where('c', of(GOLD_INGOT))
                    .where('d', lapis)
                    .where('u', Upgrade())
                    .result(Upgrade(RANGE, 1).createItemStack())
                    .group("upgrade")
                    .build("upgrade_range_1", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "cuc", "ada")
                    .where('a', of(GOLD_INGOT))
                    .where('b', of(EMERALD))
                    .where('c', of(QUARTZ))
                    .where('d', lapis)
                    .where('u', Upgrade(RANGE, 1))
                    .result(Upgrade(RANGE, 2).createItemStack())
                    .group("upgrade")
                    .build("upgrade_range_2", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "cuc", "ada")
                    .where('a', of(QUARTZ))
                    .where('b', of(ENDER_PEARL))
                    .where('c', of(BLAZE_ROD))
                    .where('d', lapis)
                    .where('u', Upgrade(RANGE, 2))
                    .result(Upgrade(RANGE, 3).createItemStack())
                    .group("upgrade")
                    .build("upgrade_range_3", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "cuc", "ada")
                    .where('a', of(BLAZE_ROD))
                    .where('b', of(ENDER_EYE))
                    .where('c', of(DRAGON_BREATH))
                    .where('d', lapis)
                    .where('u', Upgrade(RANGE, 3))
                    .result(Upgrade(RANGE, 4).createItemStack())
                    .group("upgrade")
                    .build("upgrade_range_4", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "cuc", "ada")
                    .where('a', of(DRAGON_BREATH))
                    .where('b', of(NETHER_STAR))
                    .where('c', of(DRAGON_EGG))
                    .where('d', lapis)
                    .where('u', Upgrade(RANGE, 4))
                    .result(Upgrade(RANGE, 5).createItemStack())
                    .group("upgrade")
                    .build("upgrade_range_5", main)
            )

            // Flexible upgrade
            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(FEATHER))
                    .where('b', of(SUGAR))
                    .where('u', Upgrade())
                    .result(Upgrade(FLEXIBLE, 1).createItemStack())
                    .group("upgrade")
                    .build("upgrade_flexible_1", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(FIREWORKS))
                    .where('b', boneMeal)
                    .where('u', Upgrade(FLEXIBLE, 1))
                    .result(Upgrade(FLEXIBLE, 2).createItemStack())
                    .group("upgrade")
                    .build("upgrade_flexible_2", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', builder().with {
                        it.type == POTION && it[POTION_EFFECTS].orElse(null)?.first()?.type == PotionEffectTypes.SPEED
                    }.build())
                    .where('b', of(GLOWSTONE_DUST))
                    .where('u', Upgrade(FLEXIBLE, 2))
                    .result(Upgrade(FLEXIBLE, 3).createItemStack())
                    .group("upgrade")
                    .build("upgrade_flexible_3", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', builder().with {
                        it.type == SPLASH_POTION && it[POTION_EFFECTS].orElse(null)?.first()?.type == PotionEffectTypes.SPEED
                    }.build())
                    .where('b', of(NETHER_WART))
                    .where('u', Upgrade(FLEXIBLE, 3))
                    .result(Upgrade(FLEXIBLE, 4).createItemStack())
                    .group("upgrade")
                    .build("upgrade_flexible_4", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', builder().with {
                        it.type == LINGERING_POTION && it[POTION_EFFECTS].orElse(null)?.first()?.type == PotionEffectTypes.SPEED
                    }.build())
                    .where('b', of(CHORUS_FRUIT_POPPED))
                    .where('u', Upgrade(FLEXIBLE, 4))
                    .result(Upgrade(FLEXIBLE, 5).createItemStack())
                    .group("upgrade")
                    .build("upgrade_flexible_5", main)
            )

            // Cooling upgrade
            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', builder().with { it.type == POTION && !it[POTION_EFFECTS].isPresent }.build())
                    .where('b', of(LEAVES, LEAVES2))
                    .where('u', Upgrade())
                    .result(Upgrade(COOLING, 1).createItemStack())
                    .group("upgrade")
                    .build("upgrade_cooling_1", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(WATER_BUCKET))
                    .where('b', of(REDSTONE_BLOCK))
                    .where('u', Upgrade(COOLING, 1))
                    .result(Upgrade(COOLING, 2).createItemStack())
                    .group("upgrade")
                    .build("upgrade_cooling_2", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(SNOW))
                    .where('b', of(GLOWSTONE))
                    .where('u', Upgrade(COOLING, 2))
                    .result(Upgrade(COOLING, 3).createItemStack())
                    .group("upgrade")
                    .build("upgrade_cooling_3", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(PACKED_ICE))
                    .where('b', of(DIAMOND_BLOCK))
                    .where('u', Upgrade(COOLING, 3))
                    .result(Upgrade(COOLING, 4).createItemStack())
                    .group("upgrade")
                    .build("upgrade_cooling_4", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(NETHER_STAR))
                    .where('b', of(EMERALD_BLOCK))
                    .where('u', Upgrade(COOLING, 4))
                    .result(Upgrade(COOLING, 5).createItemStack())
                    .group("upgrade")
                    .build("upgrade_cooling_5", main)
            )

            // Damage upgrade
            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(WATER_BUCKET))
                    .where('b', of(REDSTONE_BLOCK))
                    .where('u', Upgrade())
                    .result(Upgrade(DAMAGE, 1).createItemStack())
                    .group("upgrade")
                    .build("upgrade_damage_1", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(OBSIDIAN))
                    .where('b', of(CONCRETE))
                    .where('u', Upgrade(DAMAGE, 1))
                    .result(Upgrade(DAMAGE, 2).createItemStack())
                    .group("upgrade")
                    .build("upgrade_damage_2", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(PRISMARINE))
                    .where('b', of(SEA_LANTERN))
                    .where('u', Upgrade(DAMAGE, 2))
                    .result(Upgrade(DAMAGE, 3).createItemStack())
                    .group("upgrade")
                    .build("upgrade_damage_3", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(RED_NETHER_BRICK))
                    .where('b', of(NETHER_WART_BLOCK))
                    .where('u', Upgrade(DAMAGE, 3))
                    .result(Upgrade(DAMAGE, 4).createItemStack())
                    .group("upgrade")
                    .build("upgrade_damage_4", main)
            )

            register(
                ShapedCraftingRecipe.builder().aisle("aba", "bub", "aba")
                    .where('a', of(PURPUR_BLOCK))
                    .where('b', of(END_BRICKS))
                    .where('u', Upgrade(DAMAGE, 4))
                    .result(Upgrade(DAMAGE, 5).createItemStack())
                    .group("upgrade")
                    .build("upgrade_damage_5", main)
            )
        }
    }
}
