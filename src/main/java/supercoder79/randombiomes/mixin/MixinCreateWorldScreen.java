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
import supercoder79.randombiomes.BiomeTest;
import supercoder79.randombiomes.RandomBiomeFeatures;
import supercoder79.randombiomes.RandomSurfaceBuilders;
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
        SerializableBiomeData data = null;
        BiomeStateManager.idBiomeMap.clear();
        int i = Registry.BIOME.getRawId(Registry.BIOME.get(new Identifier("randombiomes", "testing")));
        Random r = new Random();
        if (i == 0) {
            for (int j = 0; j<=9;j++) {

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
                int surfaceBuilder = r.nextInt(10);
                SurfaceBuilder s = SurfaceBuilder.DEFAULT;
                switch (surfaceBuilder) {
                    case 0:
                    case 1:
                    case 2:
                        break;
                    case 3:
                        s = SurfaceBuilder.SWAMP;
                        break;
                    case 4:
                        s = SurfaceBuilder.MOUNTAIN;
                        break;
                    case 5:
                        s = SurfaceBuilder.SHATTERED_SAVANNA;
                        break;
                    case 6:
                        s = SurfaceBuilder.GIANT_TREE_TAIGA;
                        break;
                    case 7:
                        s = RandomSurfaceBuilders.CLIFF;
                        break;
                    case 8:
                        s = RandomSurfaceBuilders.MOSTLY_GRASS;
                        System.out.println("MS: " + j);
                        break;
                    case 9:
                        s = RandomSurfaceBuilders.MOSTLY_SAND;
                        System.out.println("MG: " + j);
                        break;
                }
                int surfaceConfig = r.nextInt(5);
                if (s instanceof CliffSurfaceBuilder) {
                    surfaceConfig = 5;
                }
                TernarySurfaceConfig c = SurfaceBuilder.GRASS_CONFIG;
                switch (surfaceConfig) {
                    case 0:
                    case 1:
                    case 2:
                        break;
                    case 3:
                        c = SurfaceBuilder.BADLANDS_CONFIG;
                        break;
                    case 4:
                        c = SurfaceBuilder.SAND_CONFIG;
                        break;
                    case 5:
                        c = RandomSurfaceBuilders.BASALT_CONFIG;
                        break;
                }
                int cactusCount = 0;
                if (c == SurfaceBuilder.SAND_CONFIG || c == SurfaceBuilder.BADLANDS_CONFIG) {
                    cactusCount = r.nextInt(21)+4;
                }

                if (s == RandomSurfaceBuilders.MOSTLY_SAND || s == RandomSurfaceBuilders.MOSTLY_GRASS) {
                    cactusCount = r.nextInt(60)+120;
                }

                int waterColor = 4159204 + (r.nextInt((10000 - -10000) + 1) + -10000);
                float depth = -0.3F + (r.nextFloat()*(1.2F - -0.3F));
                float scale = r.nextFloat()*1.25F;
                float temperature = r.nextFloat();
                float downfall = r.nextFloat();
                int grassAmt = r.nextInt(4)+2;
                int fernAmt = r.nextInt(4);
                Identifier id = new Identifier("randombiomes", Integer.toString(j));
                Biome b = Registry.register(Registry.BIOME, id, BiomeTest.template.builder()
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
                if (BiomeStateManager.holder == null) {
                    BiomeStateManager.holder = b;
                }
                BiomeData biomeData = new BiomeData(b, Registry.BIOME.getRawId(b), id);
                BiomeStateManager.idBiomeMap.put(Registry.BIOME.getRawId(b), b);
                BiomeStateManager.data.add(biomeData);
                BiomeStateManager.firstLoad = true;
                OverworldBiomes.addContinentalBiome(b, OverworldClimate.TEMPERATE, r.nextInt(4)+1);
                data = new SerializableBiomeData(Registry.BIOME.getRawId(b), j, depth, scale, temperature, downfall, waterColor, oakTreeAmt, birchTreeAmt, spruceTreeAmt, oakLogAmt, birchLogAmt, spruceLogAmt, grassAmt, fernAmt, cactusCount, 0, surfaceBuilder, surfaceConfig);
                list.add(data);
            }
        }
        try {
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
