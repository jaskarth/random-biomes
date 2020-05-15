package supercoder79.randombiomes.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import supercoder79.randombiomes.data.BiomeUtil;

@Mixin(BiomeLayerSampler.class)
public class MixinBiomeLayerSampler {
    @Inject(method = "getBiome", at = @At("HEAD"), cancellable = true)
    private void getBiome(int int_1, CallbackInfoReturnable<Biome> info) {
        //Very hacky code to prevent log spam, this needs to be gone ASAP
        if (int_1 == 255 || int_1 == -1) info.setReturnValue(BiomeUtil.holder);
        if (BiomeUtil.idBiomeMap.containsKey(int_1)) {
            info.setReturnValue(BiomeUtil.idBiomeMap.get(int_1));
        }
    }
}
