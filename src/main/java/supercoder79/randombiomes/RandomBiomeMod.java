package supercoder79.randombiomes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RandomBiomeMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_oak_log"), RandomBiomeFeatures.OAK_FALLEN_LOG);
		Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_birch_log"), RandomBiomeFeatures.BIRCH_FALLEN_LOG);
		Registry.register(Registry.FEATURE, new Identifier("randombiomes", "fallen_spruce_log"), RandomBiomeFeatures.SPRUCE_FALLEN_LOG);
		System.out.println("Hello Fabric world!");
	}
}
