/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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

package one.oktw.galaxy.datagen.util

import net.minecraft.advancement.AdvancementCriterion
import net.minecraft.advancement.AdvancementRequirements
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion
import net.minecraft.data.recipe.CraftingRecipeJsonBuilder
import net.minecraft.data.recipe.RecipeExporter
import net.minecraft.item.Item
import net.minecraft.item.ItemConvertible
import net.minecraft.item.ItemStack
import net.minecraft.recipe.Ingredient
import net.minecraft.recipe.RawShapedRecipe
import net.minecraft.recipe.Recipe
import net.minecraft.recipe.ShapedRecipe
import net.minecraft.recipe.book.RecipeCategory
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.tag.TagKey
import java.util.*

class ShapedRecipeJsonBuilder(private val registry: RegistryEntryLookup<Item>, private val category: RecipeCategory, private val output: ItemStack) :
    CraftingRecipeJsonBuilder {
    private val criteria: MutableMap<String, AdvancementCriterion<*>> = LinkedHashMap()
    private var group: String? = null
    private val inputs: MutableMap<Char, Ingredient> = LinkedHashMap()
    private val pattern: MutableList<String> = ArrayList()
    private var showNotification: Boolean = true

    fun addInput(char: Char, ingredient: Ingredient): ShapedRecipeJsonBuilder {
        if (this.inputs.contains(char)) {
            throw IllegalArgumentException("Symbol '$char' is already defined!")
        }
        this.inputs[char] = ingredient
        return this
    }

    fun addInput(char: Char, tag: TagKey<Item>) = addInput(char, Ingredient.ofTag(registry.getOrThrow(tag)))

    fun addInput(char: Char, item: ItemConvertible) = addInput(char, Ingredient.ofItem(item))

    fun setPattern(pattern: List<String>): ShapedRecipeJsonBuilder {
        this.pattern.clear()
        this.pattern.addAll(pattern)
        return this
    }

    fun showNotification(show: Boolean): ShapedRecipeJsonBuilder {
        this.showNotification = show
        return this
    }

    override fun criterion(name: String, criterion: AdvancementCriterion<*>): ShapedRecipeJsonBuilder {
        this.criteria[name] = criterion
        return this
    }

    override fun group(group: String?): ShapedRecipeJsonBuilder {
        this.group = group
        return this
    }

    override fun getOutputItem(): Item = output.item

    override fun offerTo(exporter: RecipeExporter, recipeKey: RegistryKey<Recipe<*>>) {
        val recipeKey = RegistryKey.of(recipeKey.getRegistryRef(), exporter.getRecipeIdentifier(recipeKey.value))
        val rawRecipe = RawShapedRecipe.create(this.inputs, this.pattern)
        val builder = exporter.advancementBuilder
            .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeKey))
            .rewards(AdvancementRewards.Builder.recipe(recipeKey))
            .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
        this.criteria.forEach(builder::criterion)
        val recipe = ShapedRecipe(
            Objects.requireNonNullElse<String?>(this.group, "") as String,
            CraftingRecipeJsonBuilder.toCraftingCategory(this.category),
            rawRecipe,
            output,
            this.showNotification
        )

        exporter.accept(recipeKey, recipe, builder.build(recipeKey.value.withPrefixedPath("recipes/" + this.category.getName() + "/")))
    }
}
