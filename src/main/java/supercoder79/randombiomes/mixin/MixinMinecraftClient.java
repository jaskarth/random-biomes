package supercoder79.randombiomes.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.terraform.biome.builder.TerraformBiome;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
import net.fabricmc.fabric.impl.biomes.WeightedBiomePicker;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sun.misc.Unsafe;
import supercoder79.randombiomes.biome.BiomeBase;
import supercoder79.randombiomes.biome.RandomBiomeFeatures;
import supercoder79.randombiomes.data.BiomeUtil;
import supercoder79.randombiomes.data.SerializableBiomeData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "startIntegratedServer", at = @At("HEAD"))
    public void startIntegratedServer(String string_1, String string_2, LevelInfo levelInfo_1, CallbackInfo info){
        if (!BiomeUtil.firstLoad) {
            System.out.println("Attempting to load random biomes");
            BiomeUtil.holder = null;
            BiomeUtil.idBiomeMap.clear();
            try {
                //Try loading from json
                Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
                path = Paths.get(path.toString(), "config", "randombiomes", string_1, "biomes.json");
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
                            .addTreeFeature(Feature.NORMAL_TREE, data.features.getOrDefault("oak_trees", 0))
                            .addTreeFeature(Feature.BIRCH_TREE, data.features.getOrDefault("birch_trees", 0))
                            .addTreeFeature(Feature.PINE_TREE, data.features.getOrDefault("spruce_trees", 0))
                            .addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG, data.features.getOrDefault("oak_logs", 0))
                            .addTreeFeature(RandomBiomeFeatures.BIRCH_FALLEN_LOG, data.features.getOrDefault("birch_logs", 0))
                            .addTreeFeature(RandomBiomeFeatures.SPRUCE_FALLEN_LOG, data.features.getOrDefault("spruce_logs", 0))
                            .addGrassFeature(Blocks.GRASS.getDefaultState(), data.features.getOrDefault("grass", 0))
                            .addGrassFeature(Blocks.FERN.getDefaultState(), data.features.getOrDefault("ferns", 0))
                            .addCustomFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.configureFeature(Feature.CACTUS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(data.features.getOrDefault("cacti", 0))))
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
                    ArrayList<TerraformBiome> toDelete = new ArrayList<>();
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
                    Field fabricBiomes = InternalBiomeData.class.getDeclaredField("OVERWORLD_INJECTED_BIOMES");
                    fabricBiomes.setAccessible(true);
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(fabricBiomes, fabricBiomes.getModifiers() & ~Modifier.FINAL);
                    List<Biome> fabricBiomeList = (List<Biome>) fabricBiomes.get(null);
                    List<Biome> fabricBiomeNew = new ArrayList<>();
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
                    final Object base = unsafe.staticFieldBase(fabricBiomes);
                    unsafe.putObject(base, unsafe.staticFieldOffset(fabricBiomes), fabricBiomeNew);

                    //Perform *another* hacky-hack to inject into the fabric registry
                    System.out.println("injecting into the biome picker");
                    Field biomePickers = InternalBiomeData.class.getDeclaredField("OVERWORLD_MODDED_CONTINENTAL_BIOME_PICKERS");
                    biomePickers.setAccessible(true);
                    Field biomePickersField = Field.class.getDeclaredField("modifiers");
                    biomePickersField.setAccessible(true);
                    biomePickersField.setInt(biomePickers, fabricBiomes.getModifiers() & ~Modifier.FINAL);
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
}
