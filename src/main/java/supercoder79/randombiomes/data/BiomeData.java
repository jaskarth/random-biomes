package supercoder79.randombiomes.data;

import com.terraformersmc.terraform.biome.builder.TerraformBiome;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeData {
    public Biome rawBiome;
    public int rawID;
    public Identifier identifier;

    public BiomeData(Biome rawBiome, int rawID, Identifier identifier) {
        this.rawBiome = rawBiome;
        this.rawID = rawID;
        this.identifier = identifier;
    }
}
