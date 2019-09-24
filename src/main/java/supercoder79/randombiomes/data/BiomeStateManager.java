package supercoder79.randombiomes.data;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import supercoder79.randombiomes.biome.RandomSurfaceBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * General purpose class used to handle a bunch of biome related stuff
 * It should really be renamed (eventually)
 */
public class BiomeStateManager {
    public static ArrayList<BiomeData> data = new ArrayList<>();
    public static boolean firstLoad = false;
    public static Random rand = new Random();
    public static Map<Integer, Biome> idBiomeMap = new HashMap<>();
    public static Biome holder = null;

    public static SurfaceBuilder getSurfaceBuilder(int in) {
        SurfaceBuilder s = SurfaceBuilder.DEFAULT;
        switch (in) {
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
                break;
            case 9:
                s = RandomSurfaceBuilders.MOSTLY_SAND;
                break;
        }
        return s;
    }

    public static TernarySurfaceConfig getSurfaceConfig(int in) {
        TernarySurfaceConfig c = SurfaceBuilder.GRASS_CONFIG;
        switch (in) {
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
                c = RandomSurfaceBuilders.CLIFF_CONFIG;
                break;
        }
        return c;
    }
}
