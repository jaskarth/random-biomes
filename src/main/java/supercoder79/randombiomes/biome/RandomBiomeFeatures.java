package supercoder79.randombiomes.biome;

import com.terraformersmc.terraform.feature.FallenLogFeature;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import supercoder79.randombiomes.features.PalmTreeFeature;
import supercoder79.randombiomes.features.ShrubFeature;
import supercoder79.randombiomes.features.TreeDefinition;

public class RandomBiomeFeatures {
    public static FallenLogFeature OAK_FALLEN_LOG;
    public static FallenLogFeature BIRCH_FALLEN_LOG;
    public static FallenLogFeature SPRUCE_FALLEN_LOG;
    public static PalmTreeFeature JUNGLE_PALM_TREE;
    public static ShrubFeature OAK_SHRUB;

    public static void init() {
        OAK_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_oak_log"), new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.OAK_LOG.getDefaultState(), 3, 3));
        BIRCH_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_birch_log"), new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.BIRCH_LOG.getDefaultState(), 3, 3));
        SPRUCE_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_spruce_log"), new FallenLogFeature(DefaultFeatureConfig::deserialize, false, Blocks.SPRUCE_LOG.getDefaultState(), 5, 3));

        TreeDefinition.Basic junglePalm = new TreeDefinition.Basic(
                Blocks.JUNGLE_LOG.getDefaultState(),
                Blocks.JUNGLE_LEAVES.getDefaultState()
        );
        JUNGLE_PALM_TREE = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "palm_tree"),
                new PalmTreeFeature(DefaultFeatureConfig::deserialize, false, junglePalm.withBark(Blocks.JUNGLE_WOOD.getDefaultState()))
        );

        TreeDefinition.Basic oakShrub = new TreeDefinition.Basic(
                Blocks.OAK_LOG.getDefaultState(),
                Blocks.OAK_LEAVES.getDefaultState()
        );

        OAK_SHRUB = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "oak_shrub"),
                new ShrubFeature(DefaultFeatureConfig::deserialize, false, oakShrub.withBark(Blocks.OAK_WOOD.getDefaultState()))
        );
    }
}
