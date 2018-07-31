package one.oktw.galaxy.mixin;

import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ExecutorService;

@Mixin(WorldServer.class)
public class MixinWorldServer {
    @Dynamic(value = "Added by Sponge Async Lighting")
    private ExecutorService lightExecutorService;

    @Inject(method = "flush()V", at = @At("RETURN"))
    private void flush(CallbackInfo ci) {
        if (lightExecutorService != null && !lightExecutorService.isShutdown()) lightExecutorService.shutdown();
    }
}
