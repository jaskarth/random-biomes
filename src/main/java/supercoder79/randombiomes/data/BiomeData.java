package supercoder79.randombiomes.data;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

/**
 * Will eventually be used for synchronizing data from the server to the client through a packet
 **/
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
