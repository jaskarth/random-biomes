package supercoder79.randombiomes.features;

import com.terraformersmc.terraform.feature.FallenLogFeatureConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.stateprovider.SimpleStateProvider;

public class RandomBiomeFeatureConfigs {
	public static FallenLogFeatureConfig OAK_FALLENLOG = new FallenLogFeatureConfig.Builder(new SimpleStateProvider(Blocks.OAK_LOG.getDefaultState()), new SimpleStateProvider(Blocks.OAK_LEAVES.getDefaultState())).baseLength(3).lengthRandom(2).build();
	public static FallenLogFeatureConfig BIRCH_FALLENLOG = new FallenLogFeatureConfig.Builder(new SimpleStateProvider(Blocks.BIRCH_LOG.getDefaultState()), new SimpleStateProvider(Blocks.BIRCH_LEAVES.getDefaultState())).baseLength(3).lengthRandom(3).build();
	public static FallenLogFeatureConfig SPRUCE_FALLENLOG = new FallenLogFeatureConfig.Builder(new SimpleStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleStateProvider(Blocks.SPRUCE_LEAVES.getDefaultState())).baseLength(5).lengthRandom(2).build();

	public static BranchedTreeFeatureConfig OAK_SHRUB = new BranchedTreeFeatureConfig.Builder(new SimpleStateProvider(Blocks.OAK_LOG.getDefaultState()), new SimpleStateProvider(Blocks.OAK_LEAVES.getDefaultState()), null).build();
}
