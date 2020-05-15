package supercoder79.randombiomes.biome;

import com.terraformersmc.terraform.feature.FallenLogFeature;
import com.terraformersmc.terraform.feature.FallenLogFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.JungleGroundBushFeature;
import supercoder79.randombiomes.features.PalmTreeFeature;
import supercoder79.randombiomes.features.TreeDefinition;

public class RandomBiomeFeatures {
    public static FallenLogFeature OAK_FALLEN_LOG;
    public static PalmTreeFeature JUNGLE_PALM_TREE;

    public static void init() {
        OAK_FALLEN_LOG = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_oak_log"), new FallenLogFeature(FallenLogFeatureConfig::deserialize));

        TreeDefinition.Basic junglePalm = new TreeDefinition.Basic(
                Blocks.JUNGLE_LOG.getDefaultState(),
                Blocks.JUNGLE_LEAVES.getDefaultState()
        );
        JUNGLE_PALM_TREE = Registry.register(Registry.FEATURE, new Identifier("randombiomes", "palm_tree"),
                new PalmTreeFeature(BranchedTreeFeatureConfig::deserialize2, Blocks.JUNGLE_WOOD.getDefaultState())
        );

        TreeDefinition.Basic oakShrub = new TreeDefinition.Basic(
                Blocks.OAK_LOG.getDefaultState(),
                Blocks.OAK_LEAVES.getDefaultState()
        );
    }
}
