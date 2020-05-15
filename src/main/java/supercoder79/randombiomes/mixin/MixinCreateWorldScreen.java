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
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import supercoder79.randombiomes.MixinHelper;
import supercoder79.randombiomes.biome.BiomeBase;
import supercoder79.randombiomes.biome.RandomBiomeFeatures;
import supercoder79.randombiomes.biome.RandomSurfaceBuilders;
import supercoder79.randombiomes.config.ConfigData;
import supercoder79.randombiomes.data.BiomeData;
import supercoder79.randombiomes.data.BiomeUtil;
import supercoder79.randombiomes.data.SerializableBiomeData;
import supercoder79.randombiomes.features.RandomBiomeFeatureConfigs;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen {
    @Shadow private String saveDirectoryName;

    @Inject(method = "createLevel", at = @At("HEAD"))
    private void createLevel(CallbackInfo info) {
        MixinHelper.createLevel(saveDirectoryName);
    }
}
