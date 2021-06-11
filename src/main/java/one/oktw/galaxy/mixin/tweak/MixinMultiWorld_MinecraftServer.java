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

package one.oktw.galaxy.mixin.tweak;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Lifecycle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressLogger;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.WanderingTraderManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.CatSpawner;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.PhantomSpawner;
import net.minecraft.world.gen.PillagerSpawner;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import one.oktw.galaxy.mixin.interfaces.MultiWorldMinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MixinMultiWorld_MinecraftServer implements MultiWorldMinecraftServer {
    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow
    @Final
    private Executor workerExecutor;

    @Shadow
    @Final
    protected LevelStorage.Session session;

    @Shadow
    @Final
    protected SaveProperties saveProperties;

    @Shadow
    @Final
    protected DynamicRegistryManager.Impl registryManager;

    @Shadow
    private static void setupSpawn(ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld) {
    }

    @Shadow
    public abstract ServerWorld getOverworld();

    @Override
    public void createWorld(Identifier identifier) { // TODO nether, end
        RegistryKey<World> key = RegistryKey.of(Registry.WORLD_KEY, identifier);
        if (worlds.containsKey(key)) return;

        ServerWorldProperties serverWorldProperties = saveProperties.getMainWorldProperties();
        long seed = (new Random()).nextLong();
        DimensionType type = registryManager.get(Registry.DIMENSION_TYPE_KEY).getOrThrow(DimensionType.OVERWORLD_REGISTRY_KEY); // TODO custom type
        NoiseChunkGenerator generator = GeneratorOptions.createOverworldGenerator(registryManager.get(Registry.BIOME_KEY), registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY), seed);

        ServerWorld world = new ServerWorld(
            (MinecraftServer) (Object) this,
            workerExecutor,
            session,
            new UnmodifiableLevelProperties(saveProperties, serverWorldProperties),
            key,
            type,
            new WorldGenerationProgressLogger(0), // TODO progress log
            generator,
            false,
            seed,
            ImmutableList.of(new PhantomSpawner(), new PillagerSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverWorldProperties)),
            true
        );

        setupSpawn(world, serverWorldProperties, false, false);

        // Add to level.dat
        saveProperties.getGeneratorOptions().getDimensions().add(RegistryKey.of(Registry.DIMENSION_KEY, identifier), new DimensionOptions(() -> type, generator), Lifecycle.experimental());
        getOverworld().getPersistentStateManager().save();

        // Start tick
        worlds.put(key, world);
    }
}
