/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2026
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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.util.filefix.FileFixerUpper;
import net.minecraft.util.filefix.FileSystemCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(FileFixerUpper.class)
public class MixinDisableAtomicAndHardLink_FileFixerUpper {
    @Inject(method = "detectFileSystemCapabilities", at = @At("RETURN"), cancellable = true)
    private static void disableAtomicAndHardLink(Path dir, CallbackInfoReturnable<FileSystemCapabilities> cir) {
        cir.cancel();
        cir.setReturnValue(new FileSystemCapabilities(false, false));
    }
}
