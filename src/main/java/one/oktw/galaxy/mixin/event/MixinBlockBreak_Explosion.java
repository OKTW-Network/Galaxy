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

package one.oktw.galaxy.mixin.event;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.type.BlockExplodeEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mixin(Explosion.class)
public class MixinBlockBreak_Explosion {
    @Final
    @Shadow
    private World world;

    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(
        value = "INVOKE",
        target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z",
        ordinal = 0
    ))
    private boolean onDestroyByExplosion(List<BlockPos> list, Collection<BlockPos> affectedPos) {
        Main main = Main.Companion.getMain();
        if (main == null) return list.addAll(affectedPos);
        return list.addAll(main.getEventManager().emit(new BlockExplodeEvent(world, (Set<BlockPos>) affectedPos)).getAffectedPos());
    }
}
