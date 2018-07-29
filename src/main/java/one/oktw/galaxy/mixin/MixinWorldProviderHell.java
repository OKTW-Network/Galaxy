package one.oktw.galaxy.mixin;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderHell;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldProviderHell.class)
public abstract class MixinWorldProviderHell extends WorldProvider {
    @Override
    public boolean canRespawnHere() {
        return true;
    }
}
