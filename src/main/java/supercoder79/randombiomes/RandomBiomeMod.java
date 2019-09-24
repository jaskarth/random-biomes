package supercoder79.randombiomes;

import net.fabricmc.api.ModInitializer;
import supercoder79.randombiomes.biome.RandomBiomeFeatures;
import supercoder79.randombiomes.biome.RandomSurfaceBuilders;

public class RandomBiomeMod implements ModInitializer {
	@Override
	public void onInitialize() {
		RandomBiomeFeatures.init();
		RandomSurfaceBuilders.init();
	}
}
