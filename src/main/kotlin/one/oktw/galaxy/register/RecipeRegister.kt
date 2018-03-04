package one.oktw.galaxy.register

import one.oktw.galaxy.Main
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.recipe.crafting.Ingredient
import org.spongepowered.api.item.recipe.crafting.Ingredient.NONE
import org.spongepowered.api.item.recipe.crafting.Ingredient.of
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe
import org.spongepowered.api.text.Text

class RecipeRegister {
    init {
        val ironIngot: Ingredient = of(ItemTypes.IRON_INGOT)

        val wrench = ShapedCraftingRecipe.builder().rows()
            .row(ironIngot, NONE, ironIngot)
            .row(NONE, of(ItemStack.of(ItemTypes.STICK, 1)), NONE)
            .row(NONE, ironIngot, NONE)
            .result(ItemStack.of(ItemTypes.STICK, 1).apply { offer(Keys.DISPLAY_NAME, Text.of("Hello World")) }) // TODO
            .build("stick", Main.main)

        Sponge.getRegistry().craftingRecipeRegistry.apply {
            register(wrench)
        }
    }
}