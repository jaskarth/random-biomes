package supercoder79.randombiomes;

import com.terraformersmc.terraform.biome.builder.TerraformBiome;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

import java.util.Random;

import static com.terraformersmc.terraform.biome.builder.DefaultFeature.*;

public class BiomeTest {
    public static TerraformBiome.Template template = new TerraformBiome.Template(TerraformBiome.builder()
            .configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
            .precipitation(Biome.Precipitation.RAIN).category(Biome.Category.TAIGA)
            .depth((new Random().nextFloat()*1.25F))
            .scale(new Random().nextFloat())
            .temperature(new Random().nextFloat())
            .downfall(new Random().nextFloat())
            .waterColor(4159204)
            .waterFogColor(329011)
            .addDefaultFeatures(LAND_CARVERS, STRUCTURES, LAKES, DUNGEONS, MINEABLES, ORES, DISKS,
                    TAIGA_GRASS, DEFAULT_MUSHROOMS, DEFAULT_VEGETATION, SPRINGS,
                    FROZEN_TOP_LAYER, DEFAULT_FLOWERS)
            .addStructureFeature(Feature.STRONGHOLD)
            .addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004D, MineshaftFeature.Type.NORMAL))
            .addDefaultSpawnEntries()
    );

}
