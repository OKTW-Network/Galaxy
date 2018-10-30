package one.oktw.galaxy.mixin;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderHell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WorldProviderHell.class)
public abstract class MixinWorldProviderHell extends WorldProvider {
    /**
     * @author james58899
     * @reason allow player respawn in nether
     */
    @Override
    @Overwrite
    public boolean canRespawnHere() {
        return true;
    }
}
