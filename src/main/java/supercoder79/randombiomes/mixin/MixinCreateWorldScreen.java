package supercoder79.randombiomes.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terraformersmc.terraform.surface.CliffSurfaceBuilder;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import supercoder79.randombiomes.biome.BiomeBase;
import supercoder79.randombiomes.biome.RandomBiomeFeatures;
import supercoder79.randombiomes.biome.RandomSurfaceBuilders;
import supercoder79.randombiomes.config.ConfigData;
import supercoder79.randombiomes.data.BiomeData;
import supercoder79.randombiomes.data.BiomeUtil;
import supercoder79.randombiomes.data.SerializableBiomeData;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen {
    @Shadow private String field_3196;

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void createLevel(CallbackInfo info) {
        List<SerializableBiomeData> list = new ArrayList<>();
        BiomeUtil.holder = null;
        SerializableBiomeData data;
        BiomeUtil.idBiomeMap.clear();
        Random r = new Random();
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
            float depth = -0.5F + (r.nextFloat()*(1.4F - -0.5F));
            float scale = r.nextFloat()*1.4F;
            float temperature = r.nextFloat();
            float downfall = r.nextFloat();
            int grassAmt = r.nextInt(4)+2;
            features.put("grass", grassAmt);
            int fernAmt = r.nextInt(4);
            features.put("ferns", fernAmt);
            int weight = r.nextInt(3)+1;

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
            Biome b = Registry.register(Registry.BIOME, id, BiomeBase.template.builder()
                    .configureSurfaceBuilder(s, c)
                    .depth(depth)
                    .scale(scale)
                    .temperature(temperature)
                    .downfall(downfall)
                    .waterColor(waterColor)
                    .waterFogColor(waterColor)
                    .addTreeFeature(Feature.NORMAL_TREE, oakTreeAmt)
                    .addTreeFeature(Feature.BIRCH_TREE, birchTreeAmt)
                    .addTreeFeature(Feature.PINE_TREE, spruceTreeAmt)
                    .addTreeFeature(RandomBiomeFeatures.OAK_FALLEN_LOG, oakLogAmt)
                    .addTreeFeature(RandomBiomeFeatures.BIRCH_FALLEN_LOG, birchLogAmt)
                    .addTreeFeature(RandomBiomeFeatures.SPRUCE_FALLEN_LOG, spruceLogAmt)
                    .addTreeFeature(RandomBiomeFeatures.JUNGLE_PALM_TREE, palmTreeAmt)
                    .addTreeFeature(RandomBiomeFeatures.OAK_SHRUB, oakShrubAmt)
                    .addGrassFeature(Blocks.GRASS.getDefaultState(), grassAmt)
                    .addGrassFeature(Blocks.FERN.getDefaultState(), fernAmt)
                    .addCustomFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.configureFeature(Feature.CACTUS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(cactusCount)))
                    .build());
            OverworldBiomes.addContinentalBiome(b, OverworldClimate.TEMPERATE, weight);
            //This is all debug stuff that will get removed eventually (TM)
            if (BiomeUtil.holder == null) {
                BiomeUtil.holder = b;
            }
            BiomeData biomeData = new BiomeData(b, Registry.BIOME.getRawId(b), id);
            BiomeUtil.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
            BiomeUtil.data.add(biomeData);
            BiomeUtil.firstLoad = true;

            data = new SerializableBiomeData(Registry.BIOME.getRawId(b), j, depth, scale, temperature, downfall, waterColor, features, surfaceBuilder, surfaceConfig, weight);
            list.add(data);
        }
        try {
            //Serialize all the biomes to a json
            //TODO: ignore existing JSON files
            Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
            path = Paths.get(path.toString(), "config", "randombiomes", this.field_3196);
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
