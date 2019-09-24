package supercoder79.randombiomes.biome;

import com.terraformersmc.terraform.feature.FallenLogFeature;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;

public class RandomBiomeFeatures {
    public static FallenLogFeature OAK_FALLEN_LOG = new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.OAK_LOG.getDefaultState(), 3, 3);
    public static FallenLogFeature BIRCH_FALLEN_LOG = new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.BIRCH_LOG.getDefaultState(), 4, 3);
    public static FallenLogFeature SPRUCE_FALLEN_LOG = new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.SPRUCE_LOG.getDefaultState(), 5, 3);
}
