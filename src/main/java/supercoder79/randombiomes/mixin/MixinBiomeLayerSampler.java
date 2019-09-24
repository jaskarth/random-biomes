package supercoder79.randombiomes.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.BiomeLayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import supercoder79.randombiomes.data.BiomeStateManager;

@Mixin(BiomeLayerSampler.class)
public class MixinBiomeLayerSampler {
    @Inject(method = "getBiome", at = @At("HEAD"), cancellable = true)
    private void getBiome(int int_1, CallbackInfoReturnable<Biome> info) {
        //Very hacky code to prevent log spam, this needs to be gone ASAP
        if (int_1 == 255 || int_1 == -1) info.setReturnValue(BiomeStateManager.holder);
        if (BiomeStateManager.idBiomeMap.containsKey(int_1)) {
            info.setReturnValue(BiomeStateManager.idBiomeMap.get(int_1));
        }
    }
}
