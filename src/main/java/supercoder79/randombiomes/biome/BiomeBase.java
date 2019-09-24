package supercoder79.randombiomes.biome;

import com.terraformersmc.terraform.biome.builder.TerraformBiome;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import static com.terraformersmc.terraform.biome.builder.DefaultFeature.*;

/**
 * Base Biome class for all other biomes to extend
 **/
public class BiomeBase {
    public static TerraformBiome.Template template = new TerraformBiome.Template(TerraformBiome.builder()
            .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
            .precipitation(Biome.Precipitation.RAIN)
            .category(Biome.Category.FOREST)
            .addDefaultFeatures(LAND_CARVERS, STRUCTURES, LAKES, DUNGEONS, MINEABLES, ORES, DISKS,
                    DEFAULT_MUSHROOMS, DEFAULT_VEGETATION, SPRINGS,
                    FROZEN_TOP_LAYER, DEFAULT_FLOWERS)
            .addStructureFeature(Feature.STRONGHOLD)
            .addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004D, MineshaftFeature.Type.NORMAL))
            .addDefaultSpawnEntries()
    );

}
