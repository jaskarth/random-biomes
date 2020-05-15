package supercoder79.randombiomes.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Holder class for JSON serializing biomes
 **/
public class SerializableBiomeData {
    public int rawID; //registry ID
    public int biomeID; //for loop num (0-9)
    public float depth;
    public float scale;
    public float temperature;
    public float rainfall;
    public int waterColor; //used for water fog color too
    public int surfaceBuilder;
    public int surfaceBuilderConfig;
    public double weight;
    public Map<String, Integer> features;

    public SerializableBiomeData(int rawID, int biomeID, float depth, float scale, float temperature, float rainfall, int waterColor, Map<String, Integer> features, int surfaceBuilder, int surfaceBuilderConfig, double weight) {
        this.rawID = rawID;
        this.biomeID = biomeID;
        this.depth = depth;
        this.scale = scale;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.waterColor = waterColor;
        this.surfaceBuilder = surfaceBuilder;
        this.surfaceBuilderConfig = surfaceBuilderConfig;
        this.weight = weight;
        Map<String, Integer> map = new HashMap<>();
        for (String s : features.keySet()) {
            if (features.get(s) != 0) map.put(s, features.get(s));
        }
        this.features = map;
    }
}
