package supercoder79.randombiomes.biome;

import com.terraformersmc.terraform.surface.CliffSurfaceBuilder;
import com.terraformersmc.terraform.surface.CliffSurfaceConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import supercoder79.randombiomes.surface.MixedSandGrassSurfaceBuilder;

public class RandomSurfaceBuilders {
    public static CliffSurfaceBuilder CLIFF = Registry.register(Registry.SURFACE_BUILDER,
            new Identifier("randombiomes", "cliff"),
            new CliffSurfaceBuilder(CliffSurfaceConfig::deserialize,
                    62,
                    SurfaceBuilder.DEFAULT));
    public static CliffSurfaceConfig CLIFF_CONFIG = new CliffSurfaceConfig(
            Blocks.GRASS_BLOCK.getDefaultState(),
            Blocks.DIRT.getDefaultState(),
            Blocks.SAND.getDefaultState(),
            Blocks.STONE.getDefaultState()
    );
    public static MixedSandGrassSurfaceBuilder MOSTLY_GRASS = Registry.register(Registry.SURFACE_BUILDER,
            new Identifier("randombiomes", "ms"),
            new MixedSandGrassSurfaceBuilder(TernarySurfaceConfig::deserialize, -2.25));
    public static MixedSandGrassSurfaceBuilder MOSTLY_SAND = Registry.register(Registry.SURFACE_BUILDER,
            new Identifier("randombiomes", "mg"),
            new MixedSandGrassSurfaceBuilder(TernarySurfaceConfig::deserialize, 1.75));
}
