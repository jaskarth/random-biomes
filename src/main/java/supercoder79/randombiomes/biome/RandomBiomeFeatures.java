package supercoder79.randombiomes.biome;

import com.terraformersmc.terraform.feature.FallenLogFeature;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class RandomBiomeFeatures {
    public static FallenLogFeature OAK_FALLEN_LOG;
    public static FallenLogFeature BIRCH_FALLEN_LOG;
    public static FallenLogFeature SPRUCE_FALLEN_LOG;

    public static void init() {
        OAK_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_oak_log"), new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.OAK_LOG.getDefaultState(), 3, 3));
        BIRCH_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_birch_log"), new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.BIRCH_LOG.getDefaultState(), 3, 3));
        SPRUCE_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_spruce_log"), new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.SPRUCE_LOG.getDefaultState(), 5, 3));
    }
}
