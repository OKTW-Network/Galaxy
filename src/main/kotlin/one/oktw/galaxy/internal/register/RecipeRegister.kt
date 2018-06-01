package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.item.enums.ToolType.WRENCH
import one.oktw.galaxy.item.enums.UpgradeType.BASE
import one.oktw.galaxy.item.enums.UpgradeType.RANGE
import one.oktw.galaxy.item.type.Tool
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe

class RecipeRegister {
    init {
        val lapis = of(ItemStack.of(DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.BLUE) })

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
            // TODO upgrade
        }
    }
}
