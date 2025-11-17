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

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.HolderGetter
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.ItemLike
import java.util.*

class ShapedRecipeJsonBuilder(private val registry: HolderGetter<Item>, private val category: RecipeCategory, private val output: ItemStack) :
    RecipeBuilder {
    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
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

    fun addInput(char: Char, tag: TagKey<Item>) = addInput(char, Ingredient.of(registry.getOrThrow(tag)))

    fun addInput(char: Char, item: ItemLike) = addInput(char, Ingredient.of(item))

    fun setPattern(pattern: List<String>): ShapedRecipeJsonBuilder {
        this.pattern.clear()
        this.pattern.addAll(pattern)
        return this
    }

    fun showNotification(show: Boolean): ShapedRecipeJsonBuilder {
        this.showNotification = show
        return this
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): ShapedRecipeJsonBuilder {
        this.criteria[name] = criterion
        return this
    }

    override fun group(group: String?): ShapedRecipeJsonBuilder {
        this.group = group
        return this
    }

    override fun getResult(): Item = output.item

    override fun save(exporter: RecipeOutput, recipeKey: ResourceKey<Recipe<*>>) {
        val recipeKey = ResourceKey.create(recipeKey.registryKey(), exporter.getRecipeIdentifier(recipeKey.location()))
        val rawRecipe = ShapedRecipePattern.of(this.inputs, this.pattern)
        val builder = exporter.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeKey))
            .rewards(AdvancementRewards.Builder.recipe(recipeKey))
            .requirements(AdvancementRequirements.Strategy.OR)
        this.criteria.forEach(builder::addCriterion)
        val recipe = ShapedRecipe(
            Objects.requireNonNullElse<String?>(this.group, "") as String,
            RecipeBuilder.determineBookCategory(this.category),
            rawRecipe,
            output,
            this.showNotification
        )

        exporter.accept(recipeKey, recipe, builder.build(recipeKey.location().withPrefix("recipes/" + this.category.folderName + "/")))
    }
}
