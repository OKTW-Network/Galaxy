/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.mixin.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.server.PlayerManager;
import one.oktw.galaxy.mixin.interfaces.CustomRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.stream.Collectors;

@Mixin(PlayerManager.class)
public class MixinCustomRecipe_PlayerManager {
    // TODO Client mod

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;values()Ljava/util/Collection;"))
    private Collection<Recipe<?>> skipSendRecipe(RecipeManager recipeManager) {
        Collection<Recipe<?>> recipes = recipeManager.values();
        recipes.removeAll(CustomRecipeManager.customRecipes.values().stream().flatMap(i -> i.values().stream()).collect(Collectors.toList()));
        return recipes;
    }

    @Redirect(method = "onDataPacksReloaded", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/RecipeManager;values()Ljava/util/Collection;"))
    private Collection<Recipe<?>> skipSyncRecipe(RecipeManager recipeManager) {
        Collection<Recipe<?>> recipes = recipeManager.values();
        recipes.removeAll(CustomRecipeManager.customRecipes.values().stream().flatMap(i -> i.values().stream()).collect(Collectors.toList()));
        return recipes;
    }
}
