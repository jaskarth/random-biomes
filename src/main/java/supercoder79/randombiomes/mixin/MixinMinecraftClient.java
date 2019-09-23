package supercoder79.randombiomes.mixin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.terraform.biome.builder.TerraformBiome;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.impl.biomes.InternalBiomeData;
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
import supercoder79.randombiomes.BiomeTest;
import supercoder79.randombiomes.RandomBiomeFeatures;
import supercoder79.randombiomes.StupidShit;
import supercoder79.randombiomes.data.BiomeStateManager;
import supercoder79.randombiomes.data.SerializableBiomeData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(method = "startIntegratedServer", at = @At("HEAD"))
    public void startIntegratedServer(String string_1, String string_2, LevelInfo levelInfo_1, CallbackInfo info){
        if (!BiomeStateManager.firstLoad) {
            System.out.println("Attempting to load random biomes");
            BiomeStateManager.holder = null;
            BiomeStateManager.idBiomeMap.clear();
            try {
                Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
                path = Paths.get(path.toString(), "config", "random-biomes", string_1, "biomes.json");
                Gson json = new GsonBuilder().create();
                SerializableBiomeData[] list = json.fromJson(new FileReader(path.toString()), SerializableBiomeData[].class);

//                System.out.println("remove from the fabric registry");
//                Field fabricBiomes = InternalBiomeData.class.getDeclaredField("OVERWORLD_INJECTED_BIOMES");
//                fabricBiomes.setAccessible(true);
//                Field modifiersField = Field.class.getDeclaredField("modifiers");
//                modifiersField.setAccessible(true);
//                modifiersField.setInt(fabricBiomes, fabricBiomes.getModifiers() & ~Modifier.FINAL);
//                List<Biome> fabricBiomeList = (List<Biome>)fabricBiomes.get(null);
//                List<Biome> fabricBiomeNew = new ArrayList<>();
//                for (Biome fb : fabricBiomeList) {
//                    if(toDelete.contains(fb)) {
//                        //
//                    } else {
//                        fabricBiomeNew.add(fb);
//                    }
//                }

//                System.out.println("Performing unsafe operations");
//                Field f = Unsafe.class.getDeclaredField("theUnsafe");
//                f.setAccessible(true);
//                Unsafe unsafe = (Unsafe) f.get(null);
//                final Object base = unsafe.staticFieldBase(fabricBiomes);
//                unsafe.putObject(base, unsafe.staticFieldOffset(fabricBiomes), fabricBiomeNew);
//
//                //StupidShit.setFinalStatic(fabricBiomes, fabricBiomeNew);
//                //fabricBiomes.set(null, fabricBiomeNew);

                boolean toAddNew = false;
                Map<Identifier, Biome> biomeList = new HashMap<>();


                for (SerializableBiomeData data : list) {
                    Biome b_raw = BiomeTest.template.builder()
                            .configureSurfaceBuilder(BiomeStateManager.getSurfaceBuilder(data.surfaceBuilder), BiomeStateManager.getSurfaceConfig(data.surfaceBuilderConfig))
                            .depth(data.depth)
                            .scale(data.scale)
                            .temperature(data.temperature)
                            .downfall(data.rainfall)
                            .waterColor(data.waterColor)
                            .waterFogColor(data.waterColor)
                            .addTreeFeature(Feature.NORMAL_TREE, data.oakTreeAmt)
                            .addTreeFeature(Feature.BIRCH_TREE, data.birchTreeAmt)
                            .addTreeFeature(Feature.PINE_TREE, data.spruceTreeAmt)
                            .addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG, data.oakLogAmt)
                            .addTreeFeature(RandomBiomeFeatures.BIRCH_FALLEN_LOG, data.birchLogAmt)
                            .addTreeFeature(RandomBiomeFeatures.SPRUCE_FALLEN_LOG, data.spruceLogAmt)
                            .addGrassFeature(Blocks.GRASS.getDefaultState(), data.grassAmt)
                            .addGrassFeature(Blocks.FERN.getDefaultState(), data.fernAmt)
                            .addCustomFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.configureFeature(Feature.CACTUS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(data.cactusAmt)))
                            .build();
                    if (BiomeStateManager.holder == null) {
                        BiomeStateManager.holder = b_raw;
                    }
                    Identifier id = new Identifier("randombiomes", Integer.toString(data.biomeID));
                    if (Registry.BIOME.containsId(id)) {
                        biomeList.put(id, b_raw);
                        if (!toAddNew) toAddNew = true;
                    } else {
                        Biome b;
                        b = Registry.register(Registry.BIOME, data.rawID, id.toString(), b_raw);
                        OverworldBiomes.addContinentalBiome(b, OverworldClimate.TEMPERATE, new Random().nextInt(4) + 1);
                    }
                }
                if (toAddNew) {
                    for (Biome b : biomeList.values()) {
                        BiomeStateManager.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
                    }

                    System.out.println("Stripping existing biomes");
                    Field entries = ((SimpleRegistry) Registry.BIOME).getClass().getDeclaredField("entries");
                    entries.setAccessible(true);
                    BiMap<Identifier, Biome> e = (BiMap<Identifier, Biome>) entries.get(Registry.BIOME);
                    BiMap<Identifier, Biome> eNew = HashBiMap.create(e);
                    ArrayList<TerraformBiome> toDelete = new ArrayList<>();
                    for (Identifier t : e.keySet()) {
                        if (t.getNamespace().equals("randombiomes")) {
                            eNew.forcePut(t, biomeList.get(t));
                        }
                    }

                    entries.set(((SimpleRegistry) Registry.BIOME), eNew);

                    Thread.sleep(256L);
                    //TODO: inject into the fabric registry as well
                    System.out.println("inject into the fabric registry");
                    Field fabricBiomes = InternalBiomeData.class.getDeclaredField("OVERWORLD_INJECTED_BIOMES");
                    fabricBiomes.setAccessible(true);
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(fabricBiomes, fabricBiomes.getModifiers() & ~Modifier.FINAL);
                    List<Biome> fabricBiomeList = (List<Biome>) fabricBiomes.get(null);
                    List<Biome> fabricBiomeNew = new ArrayList<>();
                    for (Biome fb : fabricBiomeList) {
                        if (biomeList.containsKey(Registry.BIOME.getId(fb))) {
                            fabricBiomeNew.add(biomeList.get(Registry.BIOME.getId(fb)));
                        } else {
                            fabricBiomeNew.add(fb);
                        }
                    }
                    System.out.println("Performing unsafe operations");
                    Field f = Unsafe.class.getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    Unsafe unsafe = (Unsafe) f.get(null);
                    final Object base = unsafe.staticFieldBase(fabricBiomes);
                    unsafe.putObject(base, unsafe.staticFieldOffset(fabricBiomes), fabricBiomeNew);
                }

                //StupidShit.setFinalStatic(fabricBiomes, fabricBiomeNew);
                //fabricBiomes.set(null, fabricBiomeNew);

            } catch (FileNotFoundException e) {
                System.out.println("No random biome files found");
            } catch (NoSuchFieldException e) {
                System.out.println("No such field!");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.out.println("Illegal Access!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
