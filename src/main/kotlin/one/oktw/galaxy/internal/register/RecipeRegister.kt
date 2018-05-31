package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.item.enums.ToolType.WRENCH
import one.oktw.galaxy.item.enums.UpgradeType
import one.oktw.galaxy.item.type.Tool
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.DyeColors
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.recipe.crafting.Ingredient.NONE
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe

class RecipeRegister {
    init {
        val stick = of(STICK)
        val ironIngot = of(IRON_INGOT)
        val gold = of(GOLD_INGOT)
        val diamond = of(DIAMOND)
        val redstone = of(REDSTONE)
        val obsidian = of(OBSIDIAN)
        val quartz = of(QUARTZ)
        val craftingTable = of(CRAFTING_TABLE)
        val glassPane = of(GLASS_PANE)
        val lapis = of(ItemStack.of(DYE, 1).apply { offer(Keys.DYE_COLOR, DyeColors.BLUE) })

        Sponge.getRegistry().craftingRecipeRegistry.apply {
            // Wrench
            register(
                ShapedCraftingRecipe.builder().rows()
                    .row(ironIngot, NONE, ironIngot)
                    .row(NONE, stick, NONE)
                    .row(NONE, ironIngot, NONE)
                    .result(Tool(WRENCH).createItemStack())
                    .build("wrench", main)
            )

            // Advance Crafting System
            register(
                ShapedCraftingRecipe.builder().rows()
                    .row(redstone, diamond, lapis)
                    .row(ironIngot, craftingTable, ironIngot)
                    .row(lapis, obsidian, redstone)
                    .result(ItemStack.of(CRAFTING_TABLE, 1)) // TODO custom block
                    .build("advance_crafting_system", main)
            )

            // Upgrade base
            register(
                ShapedCraftingRecipe.builder().rows()
                    .row(NONE, gold, NONE)
                    .row(redstone, glassPane, redstone)
                    .row(NONE, quartz, NONE)
                    .result(Upgrade(UpgradeType.BASE).createItemStack())
                    .build("upgrade_base", main)
            )

            // TODO upgrade
        }
    }
}
