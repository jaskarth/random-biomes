package supercoder79.randombiomes.surface;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

import java.util.Random;
import java.util.function.Function;

public class MixedSandRedSandSurfaceBuilder extends SurfaceBuilder<TernarySurfaceConfig> {
    double threshold = 0;
    public MixedSandRedSandSurfaceBuilder(Function<Dynamic<?>, ? extends TernarySurfaceConfig> function_1, double threshold) {
        super(function_1);
        this.threshold = threshold;
    }

    @Override
    public void generate(Random random, Chunk chunk, Biome biome, int int_1, int int_2, int int_3, double noise, BlockState blockState_1, BlockState blockState_2, int int_4, long long_1, TernarySurfaceConfig config) {
        if (noise > threshold) {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, int_1, int_2, int_3, noise, blockState_1, blockState_2, int_4, long_1, SurfaceBuilder.BADLANDS_CONFIG);
        } else {
            SurfaceBuilder.DEFAULT.generate(random, chunk, biome, int_1, int_2, int_3, noise, blockState_1, blockState_2, int_4, long_1, SurfaceBuilder.SAND_CONFIG);
        }

    }
}