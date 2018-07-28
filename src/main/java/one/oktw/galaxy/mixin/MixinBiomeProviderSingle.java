package one.oktw.galaxy.mixin;

import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BiomeProviderSingle.class)
public class MixinBiomeProviderSingle extends BiomeProvider {
    @Shadow
    @Final
    private Biome biome;

    @NotNull
    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return Lists.newArrayList(biome);
    }
}
