package one.oktw.galaxy.mixin

import net.minecraft.world.WorldProvider
import net.minecraft.world.WorldProviderHell
import org.spongepowered.asm.mixin.Mixin

@Mixin(WorldProviderHell::class)
abstract class MixinWorldProviderHell : WorldProvider() {
    override fun canRespawnHere() = true
}
