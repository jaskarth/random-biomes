package supercoder79.randombiomes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.terraform.surface.CliffSurfaceBuilder;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.impl.biome.InternalBiomeData;
import net.fabricmc.fabric.impl.biome.WeightedBiomePicker;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import sun.misc.Unsafe;
import supercoder79.randombiomes.biome.BiomeBase;
import supercoder79.randombiomes.biome.RandomBiomeFeatures;
import supercoder79.randombiomes.biome.RandomSurfaceBuilders;
import supercoder79.randombiomes.config.ConfigData;
import supercoder79.randombiomes.data.BiomeData;
import supercoder79.randombiomes.data.BiomeUtil;
import supercoder79.randombiomes.data.SerializableBiomeData;
import supercoder79.randombiomes.features.RandomBiomeFeatureConfigs;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MixinHelper {
	public static void startIntegratedServer(String worldname){
		if (!BiomeUtil.registered) {
			System.out.println("Attempting to load random biomes");
			BiomeUtil.holder = null;
			BiomeUtil.idBiomeMap.clear();
			try {
				//Try loading from json
				Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
				path = Paths.get(path.toString(), "config", "randombiomes", worldname, "biomes.json");
				Gson json = new GsonBuilder().create();
				SerializableBiomeData[] list = json.fromJson(new FileReader(path.toString()), SerializableBiomeData[].class);

				//Debug vars
				boolean toAddNew = false;
				Map<Identifier, Biome> biomeMap = new HashMap<>();

				//Iterate through the found biomes to attempt to add them
				for (SerializableBiomeData data : list) {
					Biome b_raw = BiomeBase.template.builder()
							.configureSurfaceBuilder(BiomeUtil.getSurfaceBuilder(data.surfaceBuilder), BiomeUtil.getSurfaceConfig(data.surfaceBuilderConfig))
							.depth(data.depth)
							.scale(data.scale)
							.temperature(data.temperature)
							.downfall(data.rainfall)
							.waterColor(data.waterColor)
							.waterFogColor(data.waterColor)
							.addTreeFeature(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG), data.features.getOrDefault("oak_trees", 0))
							.addTreeFeature(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.BIRCH_TREE_CONFIG), data.features.getOrDefault("birch_trees", 0))
							.addTreeFeature(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG), data.features.getOrDefault("spruce_trees", 0))
							.addTreeFeature(RandomBiomeFeatures.JUNGLE_PALM_TREE.configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG), data.features.getOrDefault("palm_trees", 0))
							.addTreeFeature(Feature.JUNGLE_GROUND_BUSH.configure(RandomBiomeFeatureConfigs.OAK_SHRUB), data.features.getOrDefault("oak_shrubs", 0))
							.addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG.configure(RandomBiomeFeatureConfigs.OAK_FALLENLOG), data.features.getOrDefault("oak_logs", 0))
							.addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG.configure(RandomBiomeFeatureConfigs.BIRCH_FALLENLOG), data.features.getOrDefault("birch_logs", 0))
							.addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG.configure(RandomBiomeFeatureConfigs.SPRUCE_FALLENLOG), data.features.getOrDefault("spruce_logs", 0))
							.addGrassFeature(Blocks.GRASS.getDefaultState(), data.features.getOrDefault("grass", 0))
							.addGrassFeature(Blocks.FERN.getDefaultState(), data.features.getOrDefault("ferns", 0))
							.addCustomFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.CACTUS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(data.features.getOrDefault("cacti", 0)))))
							.build();
					if (BiomeUtil.holder == null) {
						BiomeUtil.holder = b_raw;
					}
					Identifier id = new Identifier("randombiomes", Integer.toString(data.biomeID));
					if (Registry.BIOME.containsId(id)) {
						//If existing biomes need to be swapped out, add it to the list
						biomeMap.put(id, b_raw);
						if (!toAddNew) toAddNew = true;
					} else {
						//Register if the biomes weren't already added
						Biome b;
						b = Registry.register(Registry.BIOME, data.rawID, id.toString(), b_raw);
						OverworldBiomes.addContinentalBiome(b, OverworldClimate.TEMPERATE, data.weight);
					}
				}
				//If new biomes need to be injected
				if (toAddNew) {
					for (Biome b : biomeMap.values()) {
						BiomeUtil.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
					}

					//Perform a swap of the biomes in the registry (yes this is a major hack)
					ArrayList<Biome> oldBiomes = new ArrayList<>();
					ArrayList<Identifier> oldIDs = new ArrayList<>();
					System.out.println("Replacing existing biomes");
					Field entries = ((SimpleRegistry) Registry.BIOME).getClass().getDeclaredField("entries");
					entries.setAccessible(true);
					BiMap<Identifier, Biome> e = (BiMap<Identifier, Biome>) entries.get(Registry.BIOME);
					BiMap<Identifier, Biome> eNew = HashBiMap.create(e);
					for (Identifier t : e.keySet()) {
						if (t.getNamespace().equals("randombiomes")) {
							oldBiomes.add(e.get(t));
							oldIDs.add(t);
							eNew.forcePut(t, biomeMap.get(t));
						}
					}

					entries.set(Registry.BIOME, eNew);


					//Perform an even bigger hacky-hack to add this into the fabric registry
                    System.out.println("injecting into the fabric registry");
                    Field biomes = VanillaLayeredBiomeSource.class.getDeclaredField("BIOMES");
					biomes.setAccessible(true);
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(biomes, biomes.getModifiers() & ~Modifier.FINAL);
					HashSet<Biome> fabricBiomeList = (HashSet<Biome>) biomes.get(null);
					HashSet<Biome> fabricBiomeNew = new HashSet<>();
                    for (Biome fb : fabricBiomeList) {
                        if (biomeMap.containsKey(Registry.BIOME.getId(fb))) {
                            fabricBiomeNew.add(biomeMap.get(Registry.BIOME.getId(fb)));
                        } else {
                            fabricBiomeNew.add(fb);
                        }
                    }
                    Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    Unsafe unsafe = (Unsafe) f.get(null);
                    final Object base = unsafe.staticFieldBase(biomes);
                    unsafe.putObject(base, unsafe.staticFieldOffset(biomes), fabricBiomeNew);

					//Perform *another* hacky-hack to inject into the fabric registry
					System.out.println("injecting into the biome picker");
					Field biomePickers = InternalBiomeData.class.getDeclaredField("OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS");
					biomePickers.setAccessible(true);
					Field biomePickersField = Field.class.getDeclaredField("modifiers");
					biomePickersField.setAccessible(true);
					biomePickersField.setInt(biomePickers, biomePickers.getModifiers() & ~Modifier.FINAL);
					EnumMap<OverworldClimate, WeightedBiomePicker> biomePickersList = (EnumMap<OverworldClimate, WeightedBiomePicker>) biomePickers.get(null);
					for (OverworldClimate c : biomePickersList.keySet()) { //Find all of the climates
						WeightedBiomePicker picker = biomePickersList.get(c);
						Field weightField = picker.getClass().getDeclaredField("entries");
						weightField.setAccessible(true);
						List<?> weightList = (List<?>)weightField.get(picker); //Get the list of all of the biomes
						for (Object testClass : weightList) {
							Field biome = testClass.getClass().getDeclaredField("biome");
							biome.setAccessible(true);
							Field weight = testClass.getClass().getDeclaredField("weight");
							weight.setAccessible(true); //Make the biome and weight fields visible
							if (oldBiomes.contains(biome.get(testClass))) {
								Identifier id = oldIDs.get(oldBiomes.indexOf(biome.get(testClass)));
								for (SerializableBiomeData d : list) {
									if (d.biomeID == Integer.parseInt(id.getPath())) {
										biome.set(testClass, biomeMap.get(id)); //Replace the old biome with the new biome
										weight.set(testClass, d.weight); //Replace the old weight with the new weight
										break; //If we did this right, this should eliminate ugly chunk borders and allow worlds to regenerate biomes when reloaded
									}
								}
							}
						}
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("No random biome files found");
			} catch (NoSuchFieldException e) {
				System.out.println("No such field!");
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				System.out.println("Illegal Access!");
			}
		}
	}

	public static void createLevel(String saveDirectoryName) {
		List<SerializableBiomeData> list = new ArrayList<>();
		BiomeUtil.holder = null;
		SerializableBiomeData data;
		BiomeUtil.idBiomeMap.clear();
		Random r = new Random();

		HashMap<Biome, Pair<Identifier, Double>> biomeList = new HashMap<>();
		Map<Identifier, Biome> biomeMap = new HashMap<>();

		boolean doTheHack = false;

		for (int j = 0; j<=9;j++) {
			Map<String, Integer> features = new HashMap<>();
			//Create trees and fallen logs
			int oakTreeAmt = r.nextInt(4);
			int oakLogAmt = 0;
			if (oakTreeAmt > 0) {
				oakLogAmt = ConfigData.data.generateFallenLogs ? r.nextInt(2) : 0;
			}
			features.put("oak_trees", oakTreeAmt);
			features.put("oak_logs", oakLogAmt);
			int birchTreeAmt = r.nextInt(4);
			int birchLogAmt = 0;
			if (birchTreeAmt > 0) {
				birchLogAmt = ConfigData.data.generateFallenLogs ? r.nextInt(2) : 0;
			}
			features.put("birch_trees", birchTreeAmt);
			features.put("birch_logs", birchLogAmt);
			int spruceTreeAmt = r.nextInt(4);
			int spruceLogAmt = 0;
			if (spruceTreeAmt > 0) {
				spruceLogAmt = ConfigData.data.generateFallenLogs ? r.nextInt(2) : 0;
			}
			features.put("spruce_trees", spruceTreeAmt);
			features.put("spruce_logs", spruceLogAmt);
			//Create surface builders and configs
			int surfaceBuilder = r.nextInt(11);
			SurfaceBuilder s = BiomeUtil.getSurfaceBuilder(surfaceBuilder);
			int surfaceConfig = r.nextInt(5);
			if (s instanceof CliffSurfaceBuilder) { //Cliff Surface Builder will crash without Cliff Surface Config
				surfaceConfig = 5;
			}
			TernarySurfaceConfig c = BiomeUtil.getSurfaceConfig(surfaceConfig);

			//Generate cactus for desert-like and mesa-like biomes
			int cactusCount = 0;
			if (c == SurfaceBuilder.SAND_CONFIG || c == SurfaceBuilder.BADLANDS_CONFIG) {
				cactusCount = r.nextInt(25)+5;
			}
			//Generate cactus for lush deserts
			if (s == RandomSurfaceBuilders.MOSTLY_SAND || s == RandomSurfaceBuilders.MOSTLY_GRASS || s == RandomSurfaceBuilders.MIXED_SAND) {
				cactusCount = r.nextInt(60)+60; //Lush deserts have way more cactus
			}
			features.put("cacti", cactusCount);

			//Generate basic biome info
			int waterColor = 4159204 + (r.nextInt((10000 - -10000) + 1) + -10000);
			float depth = -1F + (r.nextFloat()*(3.5F));
			float scale = r.nextFloat()*1.75F;
			float temperature = r.nextFloat();
			float downfall = r.nextFloat();
			int grassAmt = r.nextInt(4)+2;
			features.put("grass", grassAmt);
			int fernAmt = r.nextInt(4);
			features.put("ferns", fernAmt);
			double weight = (r.nextDouble()*3)+1;



			int palmTreeAmt = 0;
			if (cactusCount > 0) {
				if (temperature > 0.6) {
					palmTreeAmt = r.nextInt(3); //Only generate palm trees in hot desert biomes
				}
			}
			features.put("palm_trees", palmTreeAmt);

			int oakShrubAmt = 0;
			if (temperature < 0.3) {
				oakShrubAmt = r.nextInt(3); //Only cold biomes make shrubs
			}
			features.put("oak_shrubs", oakShrubAmt);

			Identifier id = new Identifier("randombiomes", Integer.toString(j));
			//Register the biome
			Biome b = BiomeBase.template.builder()
					.configureSurfaceBuilder(s, c)
					.depth(depth)
					.scale(scale)
					.temperature(temperature)
					.downfall(downfall)
					.waterColor(waterColor)
					.waterFogColor(waterColor)
					.addTreeFeature(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.OAK_TREE_CONFIG), oakTreeAmt)
					.addTreeFeature(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.BIRCH_TREE_CONFIG), birchTreeAmt)
					.addTreeFeature(Feature.NORMAL_TREE.configure(DefaultBiomeFeatures.SPRUCE_TREE_CONFIG), spruceTreeAmt)
					.addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG.configure(RandomBiomeFeatureConfigs.OAK_FALLENLOG), oakLogAmt)
					.addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG.configure(RandomBiomeFeatureConfigs.BIRCH_FALLENLOG), birchLogAmt)
					.addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG.configure(RandomBiomeFeatureConfigs.SPRUCE_FALLENLOG), spruceLogAmt)
					.addTreeFeature(RandomBiomeFeatures.JUNGLE_PALM_TREE.configure(DefaultBiomeFeatures.JUNGLE_SAPLING_TREE_CONFIG), palmTreeAmt)
					.addTreeFeature(Feature.JUNGLE_GROUND_BUSH.configure(RandomBiomeFeatureConfigs.OAK_SHRUB), oakShrubAmt)
					.addGrassFeature(Blocks.GRASS.getDefaultState(), grassAmt)
					.addGrassFeature(Blocks.FERN.getDefaultState(), fernAmt)
					.addCustomFeature(GenerationStep.Feature.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configure(DefaultBiomeFeatures.CACTUS_CONFIG).createDecoratedFeature(Decorator.COUNT_HEIGHTMAP_DOUBLE.configure(new CountDecoratorConfig(cactusCount))))
					.build();

			biomeList.put(b, new Pair<>(id, weight));
			biomeMap.put(id, b);

			if (Registry.BIOME.containsId(id)) doTheHack = true;

			//This is all debug stuff that will get removed eventually (TM)
			if (BiomeUtil.holder == null) {
				BiomeUtil.holder = b;
			}

			BiomeUtil.registered = true;

			BiomeData biomeData = new BiomeData(b, Registry.BIOME.getRawId(b), id);
			BiomeUtil.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
			BiomeUtil.data.add(biomeData);

			data = new SerializableBiomeData(Registry.BIOME.getRawId(b), j, depth, scale, temperature, downfall, waterColor, features, surfaceBuilder, surfaceConfig, weight);
			list.add(data);
		}
		//not loaded yet: let's load the biomes into the registry
		if (!doTheHack) {
			System.out.println("Registering!");
			biomeList.forEach((b, w) -> {
				Registry.register(Registry.BIOME, w.getLeft(), b);
				OverworldBiomes.addContinentalBiome(b, OverworldClimate.TEMPERATE, w.getRight());
			});
		} else {
			try {
				for (Biome b : biomeMap.values()) {
					BiomeUtil.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
				}

				//Perform a swap of the biomes in the registry (yes this is a major hack)
				ArrayList<Biome> oldBiomes = new ArrayList<>();
				ArrayList<Identifier> oldIDs = new ArrayList<>();
				System.out.println("Replacing existing biomes");
				Field entries = ((SimpleRegistry) Registry.BIOME).getClass().getDeclaredField("entries");
				entries.setAccessible(true);
				BiMap<Identifier, Biome> e = (BiMap<Identifier, Biome>) entries.get(Registry.BIOME);
				BiMap<Identifier, Biome> eNew = HashBiMap.create(e);
				for (Identifier t : e.keySet()) {
					if (t.getNamespace().equals("randombiomes")) {
						oldBiomes.add(e.get(t));
						oldIDs.add(t);
						eNew.forcePut(t, biomeMap.get(t));
					}
				}

				entries.set(Registry.BIOME, eNew);


				//Perform an even bigger hacky-hack to add this into the fabric registry
				System.out.println("injecting into the fabric registry");
				Field biomes = VanillaLayeredBiomeSource.class.getDeclaredField("BIOMES");
				biomes.setAccessible(true);
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(biomes, biomes.getModifiers() & ~Modifier.FINAL);
				HashSet<Biome> fabricBiomeList = (HashSet<Biome>) biomes.get(null);
				HashSet<Biome> fabricBiomeNew = new HashSet<>();
				for (Biome fb : fabricBiomeList) {
					if (biomeMap.containsKey(Registry.BIOME.getId(fb))) {
						fabricBiomeNew.add(biomeMap.get(Registry.BIOME.getId(fb)));
					} else {
						fabricBiomeNew.add(fb);
					}
				}
				Field f = Unsafe.class.getDeclaredField("theUnsafe");
				f.setAccessible(true);
				Unsafe unsafe = (Unsafe) f.get(null);
				final Object base = unsafe.staticFieldBase(biomes);
				unsafe.putObject(base, unsafe.staticFieldOffset(biomes), fabricBiomeNew);

				//Perform *another* hacky-hack to inject into the fabric registry
				System.out.println("injecting into the biome picker");
				Field biomePickers = InternalBiomeData.class.getDeclaredField("OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS");
				biomePickers.setAccessible(true);
				Field biomePickersField = Field.class.getDeclaredField("modifiers");
				biomePickersField.setAccessible(true);
				biomePickersField.setInt(biomePickers, biomePickers.getModifiers() & ~Modifier.FINAL);
				EnumMap<OverworldClimate, WeightedBiomePicker> biomePickersList = (EnumMap<OverworldClimate, WeightedBiomePicker>) biomePickers.get(null);
				for (OverworldClimate c : biomePickersList.keySet()) { //Find all of the climates
					WeightedBiomePicker picker = biomePickersList.get(c);
					Field weightField = picker.getClass().getDeclaredField("entries");
					weightField.setAccessible(true);
					List<?> weightList = (List<?>)weightField.get(picker); //Get the list of all of the biomes
					for (Object testClass : weightList) {
						Field biome = testClass.getClass().getDeclaredField("biome");
						biome.setAccessible(true);
						Field weight = testClass.getClass().getDeclaredField("weight");
						weight.setAccessible(true); //Make the biome and weight fields visible
						if (oldBiomes.contains(biome.get(testClass))) {
							Identifier id = oldIDs.get(oldBiomes.indexOf(biome.get(testClass)));
							for (SerializableBiomeData d : list) {
								if (d.biomeID == Integer.parseInt(id.getPath())) {
									biome.set(testClass, biomeMap.get(id)); //Replace the old biome with the new biome
									weight.set(testClass, d.weight); //Replace the old weight with the new weight
									break; //If we did this right, this should eliminate ugly chunk borders and allow worlds to regenerate biomes when reloaded
								}
							}
						}
					}
				}
				System.out.println("done!");


			} catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}


		try {
			//Serialize all the biomes to a json
			//TODO: ignore existing JSON files
			Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
			path = Paths.get(path.toString(), "config", "randombiomes", saveDirectoryName);
			Files.createDirectories(path);
			path = Paths.get(path.toString(), "biomes.json");
			Gson json = new GsonBuilder().setPrettyPrinting().create();
			FileWriter writer = new FileWriter(path.toString());
			json.toJson(list, writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
