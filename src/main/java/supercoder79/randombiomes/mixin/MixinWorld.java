package supercoder79.randombiomes.mixin;

import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import supercoder79.randombiomes.data.BiomeData;
import supercoder79.randombiomes.data.BiomeUtil;

import java.util.function.BiFunction;

@Mixin(World.class)
public class MixinWorld {
    @Shadow @Final protected LevelProperties properties;

    @Inject(method = "<init>", at = @At("RETURN"))
    protected void World(LevelProperties levelProperties_1, DimensionType dimensionType_1, BiFunction<World, Dimension, ChunkManager> biFunction_1, Profiler profiler_1, boolean boolean_1, CallbackInfo info) {

        if (BiomeUtil.registered) {
            BiomeUtil.registered = false;
        }
    }
}
