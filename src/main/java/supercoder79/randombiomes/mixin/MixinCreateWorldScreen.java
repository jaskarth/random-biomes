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
import supercoder79.randombiomes.data.BiomeData;
import supercoder79.randombiomes.data.BiomeStateManager;
import supercoder79.randombiomes.data.SerializableBiomeData;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen {
    @Shadow private String field_3196;

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void createLevel(CallbackInfo info) {
        List<SerializableBiomeData> list = new ArrayList<>();
        BiomeStateManager.holder = null;
        SerializableBiomeData data;
        BiomeStateManager.idBiomeMap.clear();
        Random r = new Random();
        for (int j = 0; j<=9;j++) {
            //Create trees and fallen logs
            int oakTreeAmt = r.nextInt(4);
            int oakLogAmt = 0;
            if (oakTreeAmt > 0) {
                oakLogAmt = r.nextInt(2);
            }
            int birchTreeAmt = r.nextInt(4);
            int birchLogAmt = 0;
            if (birchTreeAmt > 0) {
                birchLogAmt = r.nextInt(2);
            }
            int spruceTreeAmt = r.nextInt(4);
            int spruceLogAmt = 0;
            if (spruceTreeAmt > 0) {
                spruceLogAmt = r.nextInt(2);
            }
            //Create surface builders and configs
            int surfaceBuilder = r.nextInt(10);
            SurfaceBuilder s = BiomeStateManager.getSurfaceBuilder(surfaceBuilder);
            int surfaceConfig = r.nextInt(5);
            if (s instanceof CliffSurfaceBuilder) { //Cliff Surface Builder will crash without Cliff Surface Config
                surfaceConfig = 5;
            }
            TernarySurfaceConfig c = BiomeStateManager.getSurfaceConfig(surfaceConfig);

            //Generate cactus for desert-like and mesa-like biomes
            int cactusCount = 0;
            if (c == SurfaceBuilder.SAND_CONFIG || c == SurfaceBuilder.BADLANDS_CONFIG) {
                cactusCount = r.nextInt(25)+5;
            }
            //Generate cactus for lush deserts
            if (s == RandomSurfaceBuilders.MOSTLY_SAND || s == RandomSurfaceBuilders.MOSTLY_GRASS) {
                cactusCount = r.nextInt(60)+120; //Lush deserts have more cactus because... er.. reasons
            }

            //Generate basic biome info
            int waterColor = 4159204 + (r.nextInt((10000 - -10000) + 1) + -10000);
            float depth = -0.3F + (r.nextFloat()*(1.2F - -0.3F));
            float scale = r.nextFloat()*1.25F;
            float temperature = r.nextFloat();
            float downfall = r.nextFloat();
            int grassAmt = r.nextInt(4)+2;
            int fernAmt = r.nextInt(4);
            int weight = r.nextInt(4)+1;
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
                    .addGrassFeature(Blocks.GRASS.getDefaultState(), grassAmt)
                    .addGrassFeature(Blocks.FERN.getDefaultState(), fernAmt)
                    .addCustomFeature(GenerationStep.Feature.VEGETAL_DECORATION, Biome.configureFeature(Feature.CACTUS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(cactusCount)))
                    .build());
            OverworldBiomes.addContinentalBiome(b, OverworldClimate.TEMPERATE, weight);
            //This is all debug stuff that will get removed eventually (TM)
            if (BiomeStateManager.holder == null) {
                BiomeStateManager.holder = b;
            }
            BiomeData biomeData = new BiomeData(b, Registry.BIOME.getRawId(b), id);
            BiomeStateManager.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
            BiomeStateManager.data.add(biomeData);
            BiomeStateManager.firstLoad = true;

            data = new SerializableBiomeData(Registry.BIOME.getRawId(b), j, depth, scale, temperature, downfall, waterColor, oakTreeAmt, birchTreeAmt, spruceTreeAmt, oakLogAmt, birchLogAmt, spruceLogAmt, grassAmt, fernAmt, cactusCount, 0, surfaceBuilder, surfaceConfig, weight);
            list.add(data);
        }
        try {
            //Serialize all the biomes to a json
            //TODO: ignore existing JSON files
            Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
            path = Paths.get(path.toString(), "config", "random-biomes", this.field_3196);
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
