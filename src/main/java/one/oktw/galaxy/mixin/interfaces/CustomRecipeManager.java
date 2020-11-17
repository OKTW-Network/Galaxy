/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public interface CustomRecipeManager {
    HashMap<RecipeType<?>, HashMap<Identifier, Recipe<?>>> customRecipes = new HashMap<>();

    static void addRecipe(RecipeType<?> type, Recipe<?> recipe) {
        customRecipes.computeIfAbsent(type, k -> new HashMap<>()).putIfAbsent(recipe.getId(), recipe);
    }
}
