/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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

package one.oktw.galaxy.mixin.interfaces;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public interface CustomRecipeManager {
    HashMap<RecipeType<?>, HashMap<Identifier, RecipeEntry<?>>> customRecipes = new HashMap<>();

    static void addRecipe(RecipeType<?> type, Identifier id, Recipe<?> recipe) {
        customRecipes.computeIfAbsent(type, k -> new HashMap<>()).put(id, new RecipeEntry<Recipe<?>>(id, recipe));
    }
}
